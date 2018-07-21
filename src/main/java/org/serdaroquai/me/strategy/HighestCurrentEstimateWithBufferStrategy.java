package org.serdaroquai.me.strategy;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.serdaroquai.me.Config;
import org.serdaroquai.me.MinerConfig;
import org.serdaroquai.me.MinerConfig.MinerContext;
import org.serdaroquai.me.components.MinerManager;
import org.serdaroquai.me.components.ProfitabilityManager;
import org.serdaroquai.me.event.ProfitabilityUpdateEvent;
import org.serdaroquai.me.event.StrategyChangeEvent;
import org.serdaroquai.me.strategy.HighestCurrentEstimateWithBufferStrategy.HighestCurrentEstimateWithBufferStrategyCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.event.EventListener;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.stereotype.Component;


@Component("highestCurrentEstimateWithBuffer")
@Conditional(HighestCurrentEstimateWithBufferStrategyCondition.class)
public class HighestCurrentEstimateWithBufferStrategy implements IStrategy{
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired ApplicationEventPublisher applicationEventPublisher;
	@Autowired Config config;
	@Autowired MinerConfig minerConfig;
	@Autowired MinerManager minerManager;
	
	//buffers the new strategy for at least the amount of time before publishing it
	@Value("${strategy.debounceTime:5}") int debounceTime;
	
	Strategy currentStrategy = Strategy.IDLE;
	Strategy bufferStrategy = null;
	
	public static class HighestCurrentEstimateWithBufferStrategyCondition implements Condition {
		@Override
		public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
			return context.getEnvironment().getProperty("strategy.name").equals("highestCurrentEstimateWithBuffer");
		}
	}	
	
	@PostConstruct
	private void init() {
		logger.info(String.format("Initialize using %s", this.getClass()));
	}

	@Override
	public StrategyChangeEvent generateAction(ProfitabilityManager manager) {

		StrategyChangeEvent event = new StrategyChangeEvent(this, currentStrategy);
		
		// algo-tag, estimation
		Map<String, BigDecimal> latestEstimations = manager.getLatestNormalizedEstimations();
		
		// algo, miner (mineable ones) 
		Map<String, MinerContext> minerMap = minerConfig.getMinerMap();
		
		//remove the ones that are not mineable by config
		final Set<String> mineableAlgorithms = latestEstimations.keySet().stream()
				.filter(algo -> minerMap.containsKey(algo))
				.collect(Collectors.toSet());
		
		// check custom rules 
		Optional<Entry<String, BigDecimal>> findFirstRule = config.getPrioritize().entrySet().stream()
			.filter(e -> mineableAlgorithms.contains(e.getKey()))
			.filter(e -> latestEstimations.get(e.getKey()).compareTo(e.getValue()) > 0)
			.findFirst();
		
		// if there is a rule
		if (findFirstRule.isPresent()) {
			String algo = findFirstRule.get().getKey();
			BigDecimal ifAbove = findFirstRule.get().getValue();
			BigDecimal latestEstimation = latestEstimations.get(algo);
			
			event.log(String.format("Following defined rule for %s being greater than %s, currentEstimation: %s ", 
					algo, ifAbove, latestEstimation));
			
			event.setStrategy(new Strategy(algo));
			return event;
		} else {
			event.log("No defined rules fulfill");
		}
		
		// calculate the maximum estimation
		Optional<BigDecimal> optional = mineableAlgorithms.stream()
			.map(algo -> {
				BigDecimal est1 = latestEstimations.get(algo);
				return est1 == null ? BigDecimal.ZERO : est1;
			})
			.sorted(Comparator.<BigDecimal>reverseOrder())
			.findFirst();
		
		// calculate minimum estimation requirement
		long miningForSeconds = minerManager.miningFor(ChronoUnit.SECONDS);
		BigDecimal threshold = getCurrentThreshold(miningForSeconds);
		event.log(String.format("Current threshold is %s (%s seconds)", threshold, miningForSeconds));
		
		BigDecimal max = optional.orElse(BigDecimal.ZERO);
		BigDecimal limit = max.subtract(max.multiply(threshold));
		
		// if there is no estimations keep doing whatever you are doing
		if (BigDecimal.ZERO.equals(max)) {
			event.log(String.format("Maximum estimation is zero, keeping currentStrategy: %s", currentStrategy));
			return event;
		} else {
			event.log(String.format("Acceptable estimation is above: %s ", limit));
		}
		
		// filter out the choices that are below threshold
		Set<String> filteredMineableAlgorithms = mineableAlgorithms.stream()
			.filter(algo -> {
					BigDecimal estimation = latestEstimations.get(algo) == null ? BigDecimal.ZERO : latestEstimations.get(algo);
					boolean keep = estimation.compareTo(limit) >= 0;
					if (keep) {
						event.log(String.format("%s fulfills this with an estimation of %s", algo, estimation));						
					}
					return keep;
				})
			.collect(Collectors.toSet());
		
		// check if current algo is among top paying one just keep it
		boolean shouldStay = filteredMineableAlgorithms.stream()
			.anyMatch(algo -> algo.equals(currentStrategy.getAlgo()));
		
		if (shouldStay) {
			event.log(String.format("%s is still above threshold, keeping same algo", currentStrategy.getAlgo()));
			return event;			
		} else {
			// else just pick the highest estimation
			Optional<String> first = filteredMineableAlgorithms.stream()
				.filter(algo -> max.compareTo(latestEstimations.get(algo)) == 0)
				.findFirst();
			
			String algorithm = first.get();
			event.log(String.format("Changing to: %s with estimation: %s", 
					algorithm, 
					latestEstimations.get(algorithm)));
			
			event.setStrategy(new Strategy(algorithm));
			return event;
		}
		
	}
	
	private BigDecimal getCurrentThreshold(long miningForSeconds) {
		
		// get appropriate interval
		Optional<Long> key = config.getThreshold().keySet().stream()
			.filter(seconds -> seconds <= miningForSeconds)
			.sorted(Comparator.<Long>reverseOrder())
			.findFirst();
		
		//find threshold setting
		return config.getThreshold().get(key.orElse(0L));
	}
	
	
	
	
	@EventListener
	public void onProfitabilityUpdateEvent(ProfitabilityUpdateEvent event) {
		
		// TODO fix this ugly way of making sure we don't have concurrency issues
		synchronized(this) {
			
			StrategyChangeEvent strategyChangeEvent = generateAction((ProfitabilityManager) event.getSource());
			Strategy suggestedStrategy = strategyChangeEvent.getStrategy();
			
			if (!currentStrategy.equals(Strategy.IDLE) && !currentStrategy.equals(suggestedStrategy)) {
				// if buffer is empty just buffer it
				if (bufferStrategy == null) {
					logger.info(String.format("Buffering %s", suggestedStrategy));
					bufferStrategy = suggestedStrategy;
				} else {
					// we already have something buffered
					if (suggestedStrategy.equals(bufferStrategy)) {
						// check time is up
						if (suggestedStrategy.getInstant().compareTo(bufferStrategy.getInstant().plusSeconds(debounceTime)) > 0) {
							//time is up, publish new strategy
							currentStrategy = suggestedStrategy;
							bufferStrategy = null;
							logger.info(String.format("DebounceTime is up. Publishing %s", suggestedStrategy));
							applicationEventPublisher.publishEvent(strategyChangeEvent);
						} else {
							//no-op wait for debounce time
						}
						
					} else {
						//a new suggestion, rebuffer
						logger.info(String.format("Rebuffering %s", suggestedStrategy));
						bufferStrategy = suggestedStrategy;
					}
				}
			} else {
				// publish same strategy event
				currentStrategy = suggestedStrategy;
				bufferStrategy = null;
				applicationEventPublisher.publishEvent(strategyChangeEvent);
			}
			
		}
	}
}

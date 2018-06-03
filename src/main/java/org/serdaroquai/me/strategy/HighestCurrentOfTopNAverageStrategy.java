package org.serdaroquai.me.strategy;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.serdaroquai.me.Config;
import org.serdaroquai.me.MinerConfig;
import org.serdaroquai.me.MinerConfig.MinerContext;
import org.serdaroquai.me.PoolConfig;
import org.serdaroquai.me.PoolConfig.Pool;
import org.serdaroquai.me.components.MinerManager;
import org.serdaroquai.me.components.ProfitabilityManager;
import org.serdaroquai.me.entity.PoolDetail;
import org.serdaroquai.me.event.PoolDetailEvent;
import org.serdaroquai.me.event.ProfitabilityUpdateEvent;
import org.serdaroquai.me.event.StrategyChangeEvent;
import org.serdaroquai.me.misc.Algorithm;
import org.serdaroquai.me.service.RestService;
import org.serdaroquai.me.strategy.HighestCurrentOfTopNAverageStrategy.HighestCurrentOfTopNAverageStrategyCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.event.EventListener;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component("highestCurrentOfTopNAverage")
@Conditional(HighestCurrentOfTopNAverageStrategyCondition.class)
public class HighestCurrentOfTopNAverageStrategy implements IStrategy{
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired ApplicationEventPublisher applicationEventPublisher;
	@Autowired Config config;
	@Autowired MinerConfig minerConfig;
	@Autowired MinerManager minerManager;
	@Autowired RestService restService;
	@Autowired PoolConfig poolConfig;
	
	Strategy currentStrategy = Strategy.IDLE; 
	
	Map<Pool,Map<String,PoolDetail>> poolDetails = new ConcurrentHashMap<>();
	
	public static class HighestCurrentOfTopNAverageStrategyCondition implements Condition {
		@Override
		public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
			return context.getEnvironment().getProperty("strategy.name").equals("highestCurrentOfTopNAverage");
		}
	}	
	
	@PostConstruct
	private void init() {
		logger.info(String.format("Initialize using %s", this.getClass()));
	}
	
	@Scheduled(fixedDelayString="${strategy.poolQueryFrequency:30000}")
	public void getPoolDetails() {
		poolConfig.getPool().values().forEach(pool -> restService.getPoolDetails(pool));
	}
	
	@EventListener
	public void handle(PoolDetailEvent event) {
		poolDetails.put(event.getPool(), event.getPayload());
	}
	
//	private List<Algorithm> getTopNAlgorithmEstimations(@Value("${strategy.numberOfAlgos:5}") int n) {
//		poolDetails.values().forEach(action);
//	}

	@Override
	public StrategyChangeEvent generateAction(ProfitabilityManager manager) {

		StrategyChangeEvent event = new StrategyChangeEvent(this, currentStrategy);
		
		// algo-tag, estimation
		Map<Algorithm, BigDecimal> latestEstimations = manager.getLatestNormalizedEstimations();
		
		// algo, miner (mineable ones) 
		Map<Algorithm, MinerContext> minerMap = minerConfig.getMinerMap();
		
		//remove the ones that are not mineable by config
		Set<Algorithm> mineableAlgorithms = latestEstimations.keySet().stream()
				.filter(algo -> minerMap.containsKey(algo))
				.collect(Collectors.toSet());
		
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
		mineableAlgorithms = mineableAlgorithms.stream()
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
		boolean shouldStay = mineableAlgorithms.stream()
			.anyMatch(algo -> algo.equals(currentStrategy.getAlgo()));
		
		if (shouldStay) {
			event.log(String.format("%s is still above threshold, keeping same algo", currentStrategy.getAlgo()));
			return event;			
		} else {
			// else just pick the highest estimation
			Optional<Algorithm> first = mineableAlgorithms.stream()
				.filter(algo -> max.compareTo(latestEstimations.get(algo)) == 0)
				.findFirst();
			
			Algorithm algorithm = first.get();
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
		StrategyChangeEvent strategyChangeEvent = generateAction((ProfitabilityManager) event.getSource());
		currentStrategy = strategyChangeEvent.getStrategy();
		
		applicationEventPublisher.publishEvent(strategyChangeEvent);
	}

}

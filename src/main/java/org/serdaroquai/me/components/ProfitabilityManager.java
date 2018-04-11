package org.serdaroquai.me.components;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.serdaroquai.me.Config;
import org.serdaroquai.me.entity.Estimation;
import org.serdaroquai.me.event.EstimationEvent;
import org.serdaroquai.me.event.ProfitabilityUpdateEvent;
import org.serdaroquai.me.event.StrategyChangeEvent;
import org.serdaroquai.me.misc.Algorithm;
import org.serdaroquai.me.misc.Pair;
import org.serdaroquai.me.strategy.Strategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ProfitabilityManager {

	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired Config config;
	@Autowired ApplicationEventPublisher applicationEventPublisher;
	
	Map<Algorithm,Estimation> latestEstimations = new ConcurrentHashMap<>();
	private volatile Strategy strategy = Strategy.IDLE;
	
	public Map<Algorithm,BigDecimal> getLatestEstimations() {
		return latestEstimations.entrySet().stream()
			.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getBtcRevenue()));
	}
	
	public Map<Algorithm,BigDecimal> getLatestNormalizedEstimations() {
		
		return getLatestEstimations().entrySet().stream()
				.filter(e -> config.getHashrateMap().containsKey(e.getKey()))
				.collect(Collectors.toMap(
						e -> e.getKey(), 
						e -> multiply.apply(benchmarkOf.apply(e.getKey()), e.getValue())));
	}
	
	private Function<Algorithm, BigDecimal> benchmarkOf = (algo) -> config.getHashrateMap().getOrDefault(algo, BigDecimal.ZERO);
	private BinaryOperator<BigDecimal> multiply = (first, second) -> first.multiply(second);
	private Function<Estimation, BigDecimal> normalize = (estimation) -> multiply.apply(benchmarkOf.apply(estimation.getAlgo()), estimation.getBtcRevenue());

	public Map<Algorithm, Pair<String,BigDecimal>> getBrief() {
		return latestEstimations.values().parallelStream()
			.filter(estimation -> config.getHashrateMap().containsKey(estimation.getAlgo()))
			.collect(Collectors.toMap(
					estimation -> estimation.getAlgo(), 
					estimation -> new Pair<String,BigDecimal>(estimation.getSymbol(), normalize.apply(estimation))));
	}
	
	public Strategy getStrategy() {
		return strategy;
	};
	
	@EventListener
	public void handleEvent(EstimationEvent event) {
		
		Collection<Estimation> estimations = event.getPayload();
		estimations.forEach(estimation -> latestEstimations.put(estimation.getAlgo(), estimation));
				
		applicationEventPublisher.publishEvent(new ProfitabilityUpdateEvent(this, strategy));
	}
	
	@EventListener
	public void handleEvent(StrategyChangeEvent event) {
		strategy = event.getStrategy();
	}
	
}

package org.serdaroquai.me.components;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.serdaroquai.me.Algo;
import org.serdaroquai.me.CoinConfig;
import org.serdaroquai.me.CoinConfig.Coin;
import org.serdaroquai.me.Config;
import org.serdaroquai.me.entity.Difficulty;
import org.serdaroquai.me.entity.Estimation;
import org.serdaroquai.me.event.DifficultyUpdateEvent;
import org.serdaroquai.me.event.EstimationEvent;
import org.serdaroquai.me.event.PoolQueryEvent;
import org.serdaroquai.me.event.PoolUpdateEvent;
import org.serdaroquai.me.misc.Algorithm;
import org.serdaroquai.me.misc.Pair;
import org.serdaroquai.me.service.RestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EstimationManager {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired Config config;
	@Autowired CoinConfig coinConfig;
	@Autowired RestService restService;
	@Autowired WhattomineComponent whattomineComponent;
	@Autowired ExchangeComponent exchange;
	@Autowired ApplicationEventPublisher applicationEventPublisher;
	@Value("${estimationManager.queryFrequency:120}") long queryFrequencyInSeconds;
	
	private Map<Algorithm,Estimation> latestEstimations = new ConcurrentHashMap<>();
	private long lastPoolQueryEvent = 0L;
	
	@Scheduled(fixedDelayString = "${updatePeriod:30000}")
	private void tick() {
		try {
			restService.getPoolStatus();			
		} catch (Exception e) {
			logger.error("Error getting pool status",e);
		}
	}
	
	private BigDecimal estimate(Coin coin, Difficulty difficulty) {
		
		// total network hashrate
		BigDecimal networkHashrate = difficulty.getDifficulty().multiply(new BigDecimal(Math.pow(2, 32))).divide(coin.getBlockTime(),0,RoundingMode.FLOOR);
		
		// how much BTC is produced per day with network hashrate 
		BigDecimal networkBtcEarning = coin.getBlockRewardByHeight(difficulty.getBlockHeight())
				.multiply(new BigDecimal(60*60*24))
				.divide(coin.getBlockTime(), 5, RoundingMode.HALF_DOWN)
				.multiply(coin.getExchangeRate());
		
		BigDecimal btcPerKhashPerDay = new BigDecimal(1000).multiply(networkBtcEarning).divide(networkHashrate,RoundingMode.HALF_DOWN);
		
		return btcPerKhashPerDay;
	}

	@EventListener
	public void handleEvent(DifficultyUpdateEvent event) {
		
		Difficulty difficulty = event.getPayload();
		Optional<Coin> optional = whattomineComponent.getDetails(new Pair<String,Algorithm>(difficulty.getSymbol(),difficulty.getAlgo()));
		Coin coin = optional.orElse(coinConfig.createOrGet(difficulty.getSymbol()));
		exchange.getLastPrice(coin).ifPresent(price -> coin.setExchangeRate(price));
		
		if (coin.hasAllData()) {
			
			Estimation estimation = new Estimation(estimate(coin, difficulty), difficulty);
			latestEstimations.put(difficulty.getAlgo(), estimation);
			logger.info(estimation.toString());
			applicationEventPublisher.publishEvent(new EstimationEvent(this, estimation));
			
		} else {
			logger.warn(String.format("Missing data for %s-%s %s", coin.getSymbol(), difficulty.getAlgo(), coin));
		}
	}
	
	public Map<Algorithm,BigDecimal> getLatestEstimations() {
		return latestEstimations.entrySet().stream()
			.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getBtcRevenue()));
	}
	
	@EventListener
	public void handleEvent(PoolQueryEvent event) {
		
		if (lastPoolQueryEvent > event.getTimestamp()) {
			logger.warn("Discarding stale poolquery event");
			return;
		}
		
		lastPoolQueryEvent = event.getTimestamp();
		
		Map<String, Algo> updates = event.getPayload();
		
		// filter out the ones not in algo and substitute them by proper key
		Map<Algorithm, Algo> collect = updates.values().stream()
			.filter(a -> { // filter the ones 
				Optional<Algorithm> optional = Algorithm.getByAhashpoolKey(a.getName());
				return optional.isPresent() /* && minerAlgos.contains(optional.get().toString())*/;
			}).collect(Collectors.toMap(a -> Algorithm.getByAhashpoolKey(a.getName()).get(), a -> a));
		
		// multiply estimations by hashrate
		collect.entrySet()
			.forEach(e -> {
				BigDecimal speed = config.getHashrateMap().get(e.getKey());
				speed = (speed == null) ? BigDecimal.ZERO : speed;
				Algo algo = e.getValue();
				algo.setEstimateCurrent(speed.multiply(algo.getEstimateCurrent()));
				algo.setEstimate24hr(speed.multiply(algo.getEstimate24hr()));
			});
		
		// publish event
		applicationEventPublisher.publishEvent(new PoolUpdateEvent(this, collect));
		
	}

	public Map<Algorithm, BigDecimal> getLatestNormalizedEstimations() {
		
		return getLatestEstimations().entrySet().stream()
			.filter(e -> config.getHashrateMap().containsKey(e.getKey()))
			.collect(Collectors.toMap(
					e -> e.getKey(), 
					e -> {
						BigDecimal hashrate = config.getHashrateMap().get(e.getKey());
						return hashrate.multiply(e.getValue());
					}));
	}

}

package org.serdaroquai.me.components;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.serdaroquai.me.CoinConfig.Coin;
import org.serdaroquai.me.service.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Component
public class ExchangeComponent {

	@Autowired RestService restService;

	// symbol, bigDecimal
	LoadingCache<String, BigDecimal> cache;
	
	@PostConstruct
	private void init() {
		
		cache = CacheBuilder.newBuilder().refreshAfterWrite(5, TimeUnit.MINUTES)
				.build(new CacheLoader<String, BigDecimal>() {

					@Override
					public BigDecimal load(String key) throws Exception {
						Map<String, BigDecimal> all = loadAll(null);
						return all.get(key);
					}
					
					@Override
					public Map<String, BigDecimal> loadAll(Iterable<? extends String> keys) throws Exception {
						
						Map<String, BigDecimal> markets = new HashMap<>(); 
						markets.putAll(restService.getCoinExchangeIoMarkets());
						markets.putAll(restService.getCryptopiaMarkets());
						
						cache.putAll(markets);
						
						return markets;
					}
				});
	}
	
	public Optional<BigDecimal> getLastPrice(Coin coin) {
		try {
			return Optional.ofNullable(cache.getUnchecked(coin.getSymbol()));
		} catch (Exception e) {
			return Optional.empty();
		}
	}
	
	
}

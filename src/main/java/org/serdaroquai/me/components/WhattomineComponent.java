package org.serdaroquai.me.components;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.serdaroquai.me.CoinConfig;
import org.serdaroquai.me.CoinConfig.Coin;
import org.serdaroquai.me.entity.WhattomineBrief;
import org.serdaroquai.me.entity.WhattomineBriefEnvelope;
import org.serdaroquai.me.entity.WhattomineDetail;
import org.serdaroquai.me.misc.Algorithm;
import org.serdaroquai.me.misc.Builder;
import org.serdaroquai.me.misc.Pair;
import org.serdaroquai.me.service.RestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Component
public class WhattomineComponent {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired RestService restService;
	@Autowired CoinConfig coinConfig;
	
	private final String BRIEF_CACHE_ONLY_KEY ="key";
	private LoadingCache<String, WhattomineBriefEnvelope> whattomineBriefCache;
	private LoadingCache<WhattomineBrief, WhattomineDetail> whattomineDetailCache;
	
	@PostConstruct
	public void init() {

		// Initialize whattomine caches for less query frequency
		whattomineDetailCache = CacheBuilder.newBuilder().refreshAfterWrite(5, TimeUnit.MINUTES)
				.build(new CacheLoader<WhattomineBrief, WhattomineDetail>() {

					@Override
					public WhattomineDetail load(WhattomineBrief brief) throws Exception {
						if ("Active".equals(brief.getStatus())) {
							return restService.getWhattomineDetail(brief);							
						} else {
							WhattomineDetail detail = WhattomineDetail.from(brief);
							logger.warn(String.format("Fabricated %s", detail));
							return detail;
						}
					}
				});
		
		// Initialize whattomine cache for less query frequency
		whattomineBriefCache = CacheBuilder.newBuilder().refreshAfterWrite(5, TimeUnit.MINUTES)
				.build(new CacheLoader<String,WhattomineBriefEnvelope>() {

					@Override
					public WhattomineBriefEnvelope load(String key) throws Exception {
						return restService.getWhattomineBriefInfo();
					}
				});

	}
	
	public Optional<Coin> getDetails(Pair<String,Algorithm> pair) {
		Optional<WhattomineDetail> optional = getIfExists(pair);
		
		if (optional.isPresent()) {
			return Optional.of(Builder.of(() -> coinConfig.createOrGet(pair.getFirst()))
				.with(Coin::setSymbol, pair.getFirst())
				.with(Coin::setSingleBlockReward, optional.get().getBlockReward())
				.with(Coin::setBlockTime, optional.get().getBlockTime())
				.with(Coin::setExchangeRate, optional.get().getExchangeRate())
				.build());
		} else {
			return Optional.empty();
		}
	}
	
	private Optional<WhattomineDetail> getIfExists(Pair<String,Algorithm> pair) {
		Map<String, WhattomineBrief> coins = whattomineBriefCache.getUnchecked(BRIEF_CACHE_ONLY_KEY).getCoins();
		
		Optional<WhattomineBrief> findFirst = coins.values().parallelStream()
			.filter(brief -> pair.getSecond().toString().equals(brief.getAlgorithm()))
			.filter(brief -> brief.getTag().equals(pair.getFirst()))
			.findFirst();
		
		if (findFirst.isPresent()) {
			return Optional.ofNullable(whattomineDetailCache.getUnchecked(findFirst.get()));
		} else {
			return Optional.empty();
		}
	}
	
}

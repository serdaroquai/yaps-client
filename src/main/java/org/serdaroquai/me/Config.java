package org.serdaroquai.me;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.serdaroquai.me.misc.Algorithm;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties
public class Config {
	
	private Map<Algorithm, BigDecimal> hashrateMap = new HashMap<>();
	private Map<Long,BigDecimal> threshold = new HashMap<Long,BigDecimal>();

	public Map<Long, BigDecimal> getThreshold() {
		return threshold;
	}
	public Map<Algorithm, BigDecimal> getHashrateMap() {
		return hashrateMap;
	}
	
}

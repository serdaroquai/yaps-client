package org.serdaroquai.me;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.serdaroquai.me.misc.Algorithm;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties
public class Config {
	
	public static enum LoginParam {token,rigId,userId,version}
	
	private Map<Algorithm, BigDecimal> hashrateMap = new HashMap<>();
	private Map<Long,BigDecimal> threshold = new HashMap<>();
	private Map<Algorithm,BigDecimal> prioritize = new LinkedHashMap<>();
	private Map<String, String> login = new HashMap<>();
	
	public Map<String, String> getLogin() {
		return login;
	}
	public Map<Long, BigDecimal> getThreshold() {
		return threshold;
	}
	public Map<Algorithm, BigDecimal> getHashrateMap() {
		return hashrateMap;
	}
	public Map<Algorithm, BigDecimal> getPrioritize() {
		return prioritize;
	}
	
	
}

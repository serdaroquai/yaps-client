package org.serdaroquai.me;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties
public class PoolConfig {

	Map<String, Pool> pool = new HashMap<String,Pool>();
	
	public Map<String, Pool> getPool() {
		return pool;
	}
	
	public Optional<Pool> getByPoolName(String name) {
		return pool.values().stream()
			.filter(pool -> name.equals(pool.name))
			.findFirst();
	}
	
	public static class Pool {
		String name;
		String baseUrl;
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public String getBaseUrl() {
			return baseUrl;
		}
		public void setBaseUrl(String baseUrl) {
			this.baseUrl = baseUrl;
		}
		
	}
}

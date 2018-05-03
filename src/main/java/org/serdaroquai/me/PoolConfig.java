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
		String currencyUrl;
		String statusUrl;
		
		@Override
		public String toString() {
			return "Pool [name=" + name + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Pool other = (Pool) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}
		
		public String getStatusUrl() {
			return statusUrl;
		}
		
		public void setStatusUrl(String statusUrl) {
			this.statusUrl = statusUrl;
		}
		
		public String getCurrencyUrl() {
			return currencyUrl;
		}
		
		public void setCurrencyUrl(String currencyUrl) {
			this.currencyUrl = currencyUrl;
		}
		
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

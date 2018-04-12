package org.serdaroquai.me;

import java.util.HashMap;
import java.util.Map;

import org.serdaroquai.me.misc.Algorithm;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties
public class MinerConfig {

	private Map<Algorithm, MinerContext> minerMap = new HashMap<>();
	
	public Map<Algorithm, MinerContext> getMinerMap() {
		return minerMap;
	}

	public static class MinerContext {
		String path;
		Algorithm algo;
		String kill;
		boolean active=true;
		
		public String getKill() {
			return kill;
		}
		
		public void setKill(String kill) {
			this.kill = kill;
		}
		
		public String getPath() {
			return path;
		}
		public void setPath(String path) {
			this.path = path;
		}
		public Algorithm getAlgo() {
			return algo;
		}
		public void setAlgo(Algorithm algo) {
			this.algo = algo;
		}
		public boolean isActive() {
			return active;
		}
		public void setActive(boolean active) {
			this.active = active;
		}
		
		@Override
		public String toString() {
			return String.format("MinerContext [%s, %s, %s]", algo, path, isActive() ? "active" : "inactive");
		}
		
	}
	
}

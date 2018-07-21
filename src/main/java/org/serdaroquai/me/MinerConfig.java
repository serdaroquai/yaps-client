package org.serdaroquai.me;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties
public class MinerConfig {

	//algo, minercontext
	private Map<String, MinerContext> minerMap = new HashMap<>();
	
	public Map<String, MinerContext> getMinerMap() {
		return minerMap;
	}

	public static class MinerContext {
		String path;
		String algo;
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
		public String getAlgo() {
			return algo;
		}
		public void setAlgo(String algo) {
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

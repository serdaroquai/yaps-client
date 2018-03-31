package org.serdaroquai.me.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NicehashStats {

	List<Algo> estimations = new ArrayList<Algo>();
	
	public NicehashStats() {}
	
	public NicehashStats(@JsonProperty("simplemultialgo") List<Algo> estimations ) {
		this.estimations = estimations;
	}
	
	public List<Algo> getEstimations() {
		return estimations;
	}
	
	public void setEstimations(List<Algo> estimations) {
		this.estimations = estimations;
	}
	
	@Override
	public String toString() {
		return "NicehashStats [" + estimations + "]";
	}



	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Algo {
		
		BigDecimal paying;
		int port;
		String name;
		
		public Algo() {}
		
		public Algo(
				@JsonProperty BigDecimal paying, 
				@JsonProperty int port, 
				@JsonProperty String name) {
			this.paying = paying;
			this.port = port;
			this.name = name;
		}

		public BigDecimal getPaying() {
			return paying;
		}

		public void setPaying(BigDecimal paying) {
			this.paying = paying;
		}

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}

		public String getName() {
			return name;
		}
		
		

		@Override
		public String toString() {
			return "Nicehash [" + name + ", " + paying + "]";
		}

		public void setName(String name) {
			this.name = name;
		}
		
	}
	
}

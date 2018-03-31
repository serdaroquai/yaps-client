package org.serdaroquai.me.entity;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class WhattomineBriefEnvelope {

	private Map<String,WhattomineBrief> coins;
	
	public WhattomineBriefEnvelope(
			@JsonProperty("coins") Map<String,WhattomineBrief> coins) {
		this.coins = coins;
	}
	
	public WhattomineBriefEnvelope() {}
	
	public Map<String, WhattomineBrief> getCoins() {
		return coins;
	}
	
	public void setCoins(Map<String, WhattomineBrief> coins) {
		this.coins = coins;
	}
}

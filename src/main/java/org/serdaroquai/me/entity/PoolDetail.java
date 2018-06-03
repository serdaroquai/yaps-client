package org.serdaroquai.me.entity;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class PoolDetail {

    private String algo;
    private int port;
    private String name;
    private int height;
    private BigDecimal estimate;
    private int dayBlocks;
    private int dayBtc;
    private int lastblock;
    private long timesincelast;
    private String symbol;
    private String key; // added this since key plays a role when creating stratum connection param "mc=XVG-lyra2v2"
    private BigDecimal reward; // zergpool has this, why not use it
	
	public PoolDetail(
			@JsonProperty("algo") String algo, 
			@JsonProperty("port") int port, 
			@JsonProperty("name") String name, 
			@JsonProperty("height") int height, 
			@JsonProperty("estimate") BigDecimal estimate,
			@JsonProperty("24h_blocks") int dayBlocks,
			@JsonProperty("24h_btc") int dayBtc,
			@JsonProperty("lastblock") int lastblock,
			@JsonProperty("timesincelast") long timesincelast, 
			@JsonProperty("symbol") String symbol,
			@JsonProperty("reward") BigDecimal reward) {
		
		this.algo = algo;
		this.port = port;
		this.name = name;
		this.height = height;
		this.estimate = estimate;
		this.dayBlocks = dayBlocks;
		this.dayBtc = dayBtc;
		this.lastblock = lastblock;
		this.timesincelast = timesincelast;
		this.symbol = symbol;
		this.reward = reward;
	}
	
	public PoolDetail() {}

	public BigDecimal getReward() {
		return reward;
	}
	
	public void setReward(BigDecimal reward) {
		this.reward = reward;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getAlgo() {
		return algo;
	}

	public void setAlgo(String algo) {
		this.algo = algo;
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

	public void setName(String name) {
		this.name = name;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public BigDecimal getEstimate() {
		return estimate;
	}

	public void setEstimate(BigDecimal estimate) {
		this.estimate = estimate;
	}

	public int getDayBlocks() {
		return dayBlocks;
	}

	public void setDayBlocks(int dayBlocks) {
		this.dayBlocks = dayBlocks;
	}

	public int getDayBtc() {
		return dayBtc;
	}

	public void setDayBtc(int dayBtc) {
		this.dayBtc = dayBtc;
	}

	public int getLastblock() {
		return lastblock;
	}

	public void setLastblock(int lastblock) {
		this.lastblock = lastblock;
	}

	public long getTimesincelast() {
		return timesincelast;
	}

	public void setTimesincelast(long timesincelast) {
		this.timesincelast = timesincelast;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	};
	
	
	
	
	
}

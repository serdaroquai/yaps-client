package org.serdaroquai.me.entity;

import java.math.BigDecimal;

import org.serdaroquai.me.misc.Algorithm;
import org.serdaroquai.me.misc.Builder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class WhattomineDetail {

	private int id;
	private String symbol;
	private Algorithm algorithm;
	private BigDecimal blockTime;
	private BigDecimal blockReward;
	private BigDecimal exchangeRate;
	private String status;
	private boolean lagging;
	
	public WhattomineDetail() {	}

	public WhattomineDetail(
			@JsonProperty("id") int id, 
			@JsonProperty("tag") String symbol, 
			@JsonProperty("algorithm") Algorithm algorithm, 
			@JsonProperty("block_time") BigDecimal blockTime, 
			@JsonProperty("block_reward") BigDecimal blockReward,
			@JsonProperty("exchange_rate") BigDecimal exchangeRate,
			@JsonProperty("status") String status,
			@JsonProperty("lagging") boolean lagging) {
		this.id = id;
		this.symbol = symbol;
		this.algorithm = algorithm;
		this.blockTime = blockTime;
		this.blockReward = blockReward;
		this.exchangeRate = exchangeRate;
		this.status = status;
		this.lagging = lagging;
	}
	
	public static WhattomineDetail from(WhattomineBrief brief) {
		return Builder.of(WhattomineDetail::new)
			.with(WhattomineDetail::setId, brief.getId())
			.with(WhattomineDetail::setSymbol, brief.getTag())
			.with(WhattomineDetail::setAlgorithm, Algorithm.valueOf(brief.getAlgorithm()))
			.with(WhattomineDetail::setStatus, brief.getStatus())
			.with(WhattomineDetail::setExchangeRate, BigDecimal.ZERO)
			.build();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public Algorithm getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(Algorithm algorithm) {
		this.algorithm = algorithm;
	}

	public BigDecimal getBlockTime() {
		return blockTime;
	}

	public void setBlockTime(BigDecimal blockTime) {
		this.blockTime = blockTime;
	}

	public BigDecimal getBlockReward() {
		return blockReward;
	}

	public void setBlockReward(BigDecimal blockReward) {
		this.blockReward = blockReward;
	}

	public BigDecimal getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(BigDecimal exchangeRate) {
		this.exchangeRate = exchangeRate;
	}
	
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isLagging() {
		return lagging;
	}

	public void setLagging(boolean lagging) {
		this.lagging = lagging;
	}

	@Override
	public String toString() {
		return String.format("%s-%s [id: %s, blockTime: %s, blockReward: %s, exchangeRate: %s]", symbol, algorithm, id, blockTime, blockReward, exchangeRate);
	}
}

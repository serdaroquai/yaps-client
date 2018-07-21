package org.serdaroquai.me.entity;

import java.math.BigDecimal;


public class Estimation {

    String id;
	BigDecimal btcRevenue;
	long timestamp;
	String algo;
	String symbol;
	BigDecimal difficulty;
	Integer blockHeight;
	
	public Estimation() {}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public BigDecimal getBtcRevenue() {
		return btcRevenue;
	}
	public void setBtcRevenue(BigDecimal btcRevenue) {
		this.btcRevenue = btcRevenue;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public String getAlgo() {
		return algo;
	}
	public void setAlgo(String algo) {
		this.algo = algo;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public BigDecimal getDifficulty() {
		return difficulty;
	}
	public void setDifficulty(BigDecimal difficulty) {
		this.difficulty = difficulty;
	}
	public Integer getBlockHeight() {
		return blockHeight;
	}
	public void setBlockHeight(Integer blockHeight) {
		this.blockHeight = blockHeight;
	}

	@Override
	public String toString() {
		return String.format("Estimation [%s-%s, %s]", symbol,algo,btcRevenue.toPlainString());
	}
	
}

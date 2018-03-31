package org.serdaroquai.me.entity;

import java.math.BigDecimal;

import org.serdaroquai.me.misc.Algorithm;
import org.springframework.data.annotation.Id;


public class Estimation {

	@Id
    String id;
	BigDecimal btcRevenue;
	long timestamp;
	Algorithm algo;
	String symbol;
	BigDecimal difficulty;
	Integer blockHeight;
	
	public Estimation() {}
	
	public Estimation(BigDecimal btcRevenue, Difficulty difficulty) {
		this.btcRevenue = btcRevenue;
		
		this.timestamp = difficulty.getTimestamp();
		this.algo = difficulty.getAlgo();
		this.symbol = difficulty.getSymbol();
		this.difficulty = difficulty.getDifficulty();
		this.blockHeight = difficulty.getBlockHeight();
	}
	
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
	public Algorithm getAlgo() {
		return algo;
	}
	public void setAlgo(Algorithm algo) {
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

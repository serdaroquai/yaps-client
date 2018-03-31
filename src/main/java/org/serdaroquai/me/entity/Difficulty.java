package org.serdaroquai.me.entity;

import java.math.BigDecimal;

import org.serdaroquai.me.misc.Algorithm;


public class Difficulty {

	long timestamp;
	Algorithm algo;
	String symbol;
	BigDecimal difficulty;
	Integer blockHeight;
	
	public Difficulty() {}
	
	public Difficulty(long timestamp, Algorithm algo, String tag, BigDecimal diff, Integer blockHeight) {
		this.timestamp = timestamp;
		this.algo = algo;
		this.symbol = tag;
		this.difficulty = diff;
		this.blockHeight = blockHeight;
	}
	
	@Override
	public String toString() {
		return String.format("Difficulty [%s-%s, %s, %s]", symbol,algo, difficulty, blockHeight);
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
	
}

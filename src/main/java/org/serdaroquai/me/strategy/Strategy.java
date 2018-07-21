package org.serdaroquai.me.strategy;

import java.time.Instant;

public class Strategy {

	public static final Strategy IDLE = new Strategy();
	
	private String algo;
	private Instant instant;
	
	public Strategy() {
		this(null, Instant.now());
	}
	
	public Strategy(String algo) {
		this(algo, Instant.now());
	}
	
	public Strategy(String algo, Instant instant) {
		this.algo = algo;
		this.instant = instant;
	}
	
	public String getAlgo() {
		return algo;
	}
	
	public Instant getInstant() {
		return instant;
	}

	@Override
	public String toString() {
		return "Strategy [algo=" + algo + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((algo == null) ? 0 : algo.hashCode());
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
		Strategy other = (Strategy) obj;
		if (algo == null) {
			if (other.algo != null)
				return false;
		} else if (!algo.equals(other.algo))
			return false;
		return true;
	}
	
	
	
	
}

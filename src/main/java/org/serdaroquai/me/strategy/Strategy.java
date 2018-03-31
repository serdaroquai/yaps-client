package org.serdaroquai.me.strategy;

import org.serdaroquai.me.misc.Algorithm;

public class Strategy {

	public static final Strategy IDLE = new Strategy(null);
	
	private Algorithm algo;
	
	public Strategy() {}
	
	public Strategy(Algorithm algo) {
		this.algo = algo;
	}

	public Algorithm getAlgo() {
		return algo;
	}

	public void setAlgo(Algorithm algo) {
		this.algo = algo;
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

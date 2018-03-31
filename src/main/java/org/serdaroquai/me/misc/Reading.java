package org.serdaroquai.me.misc;

import java.io.Serializable;

/**
 * A simple class represents a certain value at a certain time,
 * Value should be serializable since this is also shared with UI
 * 
 * @author tr1b6162
 *
 * @param <T>
 */
@SuppressWarnings("serial")
public class Reading<T extends Serializable> implements Serializable{

	private long timestamp;
	private T value;
	
	public Reading(long timestamp, T value) {
		this.timestamp = timestamp;
		this.value = value;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public T getValue() {
		return value;
	}
	public void setValue(T value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return String.format("[%s,%s]", timestamp, value);
	}
	
}

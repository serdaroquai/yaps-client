package org.serdaroquai.me.event;

import java.time.Instant;

import org.serdaroquai.me.MinerConfig.MinerContext;
import org.springframework.context.ApplicationEvent;

@SuppressWarnings("serial")
public class MinerEvent extends ApplicationEvent {
	
	String line;
	boolean disconnected;
	MinerContext payload; //we need context in case we need to distinguish simultaneous events
	Instant startDate;
	
	public MinerEvent(Object source, MinerContext payload, Instant startDate, String line, boolean disconnected) {
		super(source);
		this.payload=payload;
		this.startDate = startDate;
		this.line = line;
		this.disconnected = disconnected;
	}
	
	public Instant getStartDate() {
		return startDate;
	}
	
	public MinerContext getPayload() {
		return payload;
	}
	
	public String getLine() {
		return line;
	}
	
	public boolean isDisconnected() {
		return disconnected;
	}

	@Override
	public String toString() {
		return "MinerEvent [line=" + line + ", disconnected=" + disconnected + "]";
	}
	
}

package org.serdaroquai.me.event;

import java.time.Instant;

import org.json.JSONObject;
import org.serdaroquai.me.Config.StratumConnection;

@SuppressWarnings("serial")
public class StratumEvent extends AbstractEvent<JSONObject> {

	StratumConnection context;
	boolean disconnected;
	Instant startTime;
	
	public StratumEvent(Object source, StratumConnection context, JSONObject payload, boolean disconnected) {
		this(source,context,payload,disconnected,null);
	}
	
	public StratumEvent(Object source, StratumConnection context, JSONObject payload, boolean disconnected, Instant startTime) {
		super(source, payload);
		this.context = context;
		this.disconnected = disconnected;
		this.startTime = startTime;
	}

	public void setDisconnected(boolean disconnected) {
		this.disconnected = disconnected;
	}

	public boolean isDisconnected() {
		return disconnected;
	}

	public void setContext(StratumConnection context) {
		this.context = context;
	}
	
	public StratumConnection getContext() {
		return context;
	}
	
	public void setStartTime(Instant startTime) {
		this.startTime = startTime;
	}
	
	public Instant getStartTime() {
		return startTime;
	}

	@Override
	public String toString() {
		return "StratumEvent [context=" + context + ", payload=" + getPayload() + ", disconnected=" + disconnected + "]";
	}
	
	

}

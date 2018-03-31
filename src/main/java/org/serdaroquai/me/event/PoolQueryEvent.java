package org.serdaroquai.me.event;

import java.util.Collections;
import java.util.Map;

import org.serdaroquai.me.Algo;
import org.springframework.context.ApplicationEvent;

@SuppressWarnings("serial")
public class PoolQueryEvent extends ApplicationEvent {
 
    private Map<String,Algo> payload;
    private String poolId;

	public PoolQueryEvent(Object source, Map<String,Algo> payload, String poolId) {
        super(source);
        this.payload = payload;
        this.poolId = poolId;
    }
	
	public Map<String,Algo> getPayload() {
		return Collections.unmodifiableMap(payload);
	}
	
	public String getPoolId() {
		return poolId;
	}

	@Override
	public String toString() {
		return "PoolQueryEvent [payload=" + payload + "]";
	}
	
	
}
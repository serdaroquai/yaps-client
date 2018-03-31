package org.serdaroquai.me.event;

import org.serdaroquai.me.entity.Estimation;
import org.springframework.context.ApplicationEvent;

@SuppressWarnings("serial")
public class EstimationUpdateEvent extends ApplicationEvent {
 
	private boolean nicehash;
    private Estimation payload;

	public EstimationUpdateEvent(Object source, Estimation payload, boolean isNicehash) {
        super(source);
        this.payload = payload;
        this.nicehash = isNicehash;
    }
	
	public Estimation getPayload() {
		return payload;
	}
	
	public boolean isNicehash() {
		return nicehash;
	}
	
	@Override
	public String toString() {
		return "EstimationUpdateEvent [payload=" + payload + "]";
	}
	
	
}
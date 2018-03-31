package org.serdaroquai.me.event;

import org.serdaroquai.me.entity.Estimation;
import org.springframework.context.ApplicationEvent;

@SuppressWarnings("serial")
public class EstimationQueryEvent extends ApplicationEvent {
 
    private Estimation payload;

	public EstimationQueryEvent(Object source, Estimation payload) {
        super(source);
        this.payload = payload;
    }
	
	public Estimation getPayload() {
		return payload;
	}

	@Override
	public String toString() {
		return "EstimationUpdateEvent [payload=" + payload + "]";
	}
	
	
}
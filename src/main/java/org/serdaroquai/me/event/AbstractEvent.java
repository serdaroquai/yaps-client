package org.serdaroquai.me.event;

import org.springframework.context.ApplicationEvent;

@SuppressWarnings("serial")
public abstract class AbstractEvent<T> extends ApplicationEvent {

	private T payload;
	
	public AbstractEvent(Object source, T payload) {
		super(source);
		this.payload = payload;
	}
	
	public T getPayload() {
		return payload;
	}
	
	@Override
	public String toString() {
		return String.format("%s [payload: %s]", this.getClass(), getPayload());
	}
}

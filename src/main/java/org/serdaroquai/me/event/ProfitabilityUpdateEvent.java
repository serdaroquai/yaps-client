package org.serdaroquai.me.event;

import org.springframework.context.ApplicationEvent;

@SuppressWarnings("serial")
public class ProfitabilityUpdateEvent extends ApplicationEvent {

//	private Strategy strategy;

	public ProfitabilityUpdateEvent(Object source/*, Strategy strategy*/) {
		super(source);
//		this.strategy = strategy;
	}
	
//	public Strategy getStrategy() {
//		return strategy;
//	}
}

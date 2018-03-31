package org.serdaroquai.me.event;

import java.util.ArrayList;
import java.util.List;

import org.serdaroquai.me.strategy.Strategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;

@SuppressWarnings("serial")
public class StrategyChangeEvent extends ApplicationEvent{

	private Strategy strategy;
	private List<String> logs = new ArrayList<String>();

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	
	public StrategyChangeEvent(Object source, Strategy strategy) {
		super(source);
		this.strategy = strategy;
		
	}
	
	public Strategy getStrategy() {
		return strategy;
	}
	
	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}
	
	public void log(String log) {
		logger.info(log);
		this.logs.add(log);
	}
	
	public List<String> getLogs() {
		return logs;
	};
	
	public String getLogsAsString() {
		return logs.stream()
			.reduce((first,second) -> first + "," + second)
			.orElse("");
	}

}

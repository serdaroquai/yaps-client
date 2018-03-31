package org.serdaroquai.me.event;

import org.springframework.context.ApplicationEvent;

@SuppressWarnings("serial")
public class MinerUpdateEvent extends ApplicationEvent {
    
    String line;

	public MinerUpdateEvent(Object source, String line) {
        super(source);
		this.line = line;
    }
	
	public String getLine() {
		return line;
	}
}
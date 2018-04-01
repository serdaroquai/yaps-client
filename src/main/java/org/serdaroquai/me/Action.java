package org.serdaroquai.me;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Action implements Serializable {
	
	public static enum Command {
		stopMiner,
		changeAlgo,
		register,
		setProfitSwitching,
		alive
	}
	
	private Command command;
	private Object payload;
	
	public Action() {
		//Jackson Constructor
	}
	
	public Action(Command type, Object payload) {
		this.command = type;
		this.payload = payload;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Object> T getPayload() {
		return (T) payload;
	}
	
	public void setPayload(Object payload) {
		this.payload = payload;
	}
	
	public Command getCommand() {
		return command;
	}
	
	public void setCommand(Command type) {
		this.command = type;
	}

	@Override
	public String toString() {
		return "Action [" + command + ", " + payload + "]";
	}
	
	
	
}

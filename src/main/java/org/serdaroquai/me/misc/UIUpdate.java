package org.serdaroquai.me.misc;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UIUpdate<T> implements Serializable{

	public static enum Type {
		profitabilityUpdate,
		minerUpdate,
		poolUpdate
	}
	
	private Type type;
	private T payload;
	
	public UIUpdate() {}
	
	public UIUpdate(Type type, T payload) {
		this.type = type;
		this.payload = payload;
	}
	
	public T getPayload() {
		return payload;
	}
	
	public Type getType() {
		return type;
	}

	@Override
	public String toString() {
		return "UIUpdate ["+ type + ", " + payload + "]";
	}
}

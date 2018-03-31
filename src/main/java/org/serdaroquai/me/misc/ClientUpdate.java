package org.serdaroquai.me.misc;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class ClientUpdate implements Serializable{

	public static enum Type {
		test,
		estimationsUpdate,
		minerUpdate,
		estimationLabelsUpdate,
		poolUpdate,
	}
	
	private Type type;
	private Map<String,Object> payload = new HashMap<String,Object>();
	
	public ClientUpdate() {}
	
	private ClientUpdate(Of b) {
		this.type = b.type;
		this.payload = b.payload;
	}
	
	public Map<String, Object> getPayload() {
		return payload;
	}
	
	public Object get(String key) {
		return payload.get(key);
	}
	
	public Type getType() {
		return type;
	}
	
	public static class Of {
		
		Type type;
		Map<String,Object> payload = new HashMap<String,Object>();
		
		public Of(Type type) {
			this.type = type;
		}
		
		public Of with(String key, Object value) {
			payload.put(key,value);
			return this;
		}
		
		public ClientUpdate build() {
			return new ClientUpdate(this);
		}
	}

	@Override
	public String toString() {
		return "UIUpdate ["+ type + ", " + payload + "]";
	}
	
	
	
}

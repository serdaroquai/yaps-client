package org.serdaroquai.me.misc;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.serdaroquai.me.entity.Estimation;

@SuppressWarnings("serial")
public class ClientUpdate implements Serializable{

	/*
		test,
		estimationsUpdate,
		minerUpdate,
		estimationLabelsUpdate,
		poolUpdate,
	*/
	
	private String type;
	private Map<String,Estimation> payload = new HashMap<String,Estimation>();
	
	public ClientUpdate() {}
	
	private ClientUpdate(Of b) {
		this.type = b.type;
		this.payload = b.payload;
	}
	
	public Map<String, Estimation> getPayload() {
		return payload;
	}
	
	public Estimation get(String key) {
		return payload.get(key);
	}
	
	public String getType() {
		return type;
	}
	
	public static class Of {
		
		String type;
		Map<String,Estimation> payload = new HashMap<String,Estimation>();
		
		public Of(String type) {
			this.type = type;
		}
		
		public Of with(String key, Estimation value) {
			payload.put(key,value);
			return this;
		}
		
		public ClientUpdate build() {
			return new ClientUpdate(this);
		}
	}

	@Override
	public String toString() {
		return "ClientUpdate ["+ type + ", " + payload + "]";
	}
	
	
	
}

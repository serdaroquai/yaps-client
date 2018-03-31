package org.serdaroquai.me.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NicehashResponse {

	NicehashStats result;
	
	public NicehashResponse() {}
	
	public NicehashResponse(@JsonProperty NicehashStats result) {
		this.result = result;
	}
	
	public NicehashStats getResult() {
		return result;
	}
	public void setResult(NicehashStats result) {
		this.result = result;
	}
	
}

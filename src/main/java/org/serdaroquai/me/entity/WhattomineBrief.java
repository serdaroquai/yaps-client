package org.serdaroquai.me.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class WhattomineBrief {

	private int id;
    private String tag;
    private String algorithm;
    private boolean lagging;
    private boolean listed;
    private String status;
    private boolean testing;
	
	
	public WhattomineBrief(
			@JsonProperty("id") int id, 
			@JsonProperty("tag") String tag, 
			@JsonProperty("algorithm") String algorithm, 
			@JsonProperty("lagging") boolean lagging, 
			@JsonProperty("listed") boolean listed, 
			@JsonProperty("status") String status,
			@JsonProperty("testing") boolean testing) {
		super();
		this.id = id;
		this.tag = tag;
		this.algorithm = algorithm;
		this.lagging = lagging;
		this.listed = listed;
		this.status = status;
		this.testing = testing;
	}

	public WhattomineBrief() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public boolean isLagging() {
		return lagging;
	}

	public void setLagging(boolean lagging) {
		this.lagging = lagging;
	}

	public boolean isListed() {
		return listed;
	}

	public void setListed(boolean listed) {
		this.listed = listed;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isTesting() {
		return testing;
	}

	public void setTesting(boolean testing) {
		this.testing = testing;
	}

	@Override
	public String toString() {
		return String.format("WhattomineBrief [%s-%s, %s]", tag, algorithm, status);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WhattomineBrief other = (WhattomineBrief) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
}

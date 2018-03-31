package org.serdaroquai.me;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;


//@Entity
//@Table(name="POOL_STATUS",
//		indexes = {@Index(name = "IDX_POOL_STATUS_TIMESTAMP",  columnList="timestamp", unique = false)})
//@JsonIgnoreProperties(ignoreUnknown = true)

@SuppressWarnings("serial")
public class Algo implements Serializable {

	long timestamp;
	String port;
	String name;
	int workers;
//	@Column(name="estimate", columnDefinition = "DECIMAL(65535, 32767)")
	BigDecimal estimateCurrent;
//	@Column(name="estimate24hr", columnDefinition = "DECIMAL(65535, 32767)")
	BigDecimal estimate24hr;
//	@Column(name="actual24hr", columnDefinition = "DECIMAL(65535, 32767)")
	BigDecimal actual24hr;
//	@Column(name="hashrate", columnDefinition = "DECIMAL(65535, 32767)")
	BigDecimal hashrate;
//	@Column(name="hashrate24hr", columnDefinition = "DECIMAL(65535, 32767)")
	BigDecimal hashrate24hr;
	
	
	// {"name":"blake2s","port":5766,"coins":1,"fees":1,"hashrate":51100873092207,"workers":2655,
	//"estimate_current":"0.00004885","estimate_last24h":"0.00012122","actual_last24h":"0.10497","hashrate_last24h":42415778135827}
	public Algo(@JsonProperty("name") String name, 
			@JsonProperty("port") String port,
			@JsonProperty("workers") int workers,
			@JsonProperty("estimate_current") BigDecimal estimateCurrent,
			@JsonProperty("estimate_last24h") BigDecimal estimate24hr,
			@JsonProperty("actual_last24h") BigDecimal actual24hr,
			@JsonProperty("hashrate") BigDecimal hashrate,
			@JsonProperty("hashrate_last24h") BigDecimal hashrate24hr
			) {
		this.name= name;
		this.port = port;
		this.workers = workers;
		this.estimateCurrent = estimateCurrent;
		this.estimate24hr = estimate24hr;
		this.actual24hr = actual24hr;
		this.hashrate = hashrate;
		this.hashrate24hr = hashrate24hr;
		
//		// TODO if this is too much detail then we can get rid of the millis and keep seconds when we persist = time - mod1000(time)
//		this.timestamp = Instant.now().toEpochMilli(); 
	}
	
	public Algo() {}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getWorkers() {
		return workers;
	}

	public void setWorkers(int workers) {
		this.workers = workers;
	}

	public BigDecimal getEstimateCurrent() {
		return estimateCurrent;
	}

	public void setEstimateCurrent(BigDecimal estimateCurrent) {
		this.estimateCurrent = estimateCurrent;
	}
	
	public BigDecimal getEstimate24hr() {
		return estimate24hr;
	}
	
	public void setEstimate24hr(BigDecimal estimate24hr) {
		this.estimate24hr = estimate24hr;
	}

	public BigDecimal getActual24hr() {
		return actual24hr;
	}

	public void setActual24hr(BigDecimal actual24hr) {
		this.actual24hr = actual24hr;
	}

	public BigDecimal getHashrate() {
		return hashrate;
	}

	public void setHashrate(BigDecimal hashrate) {
		this.hashrate = hashrate;
	}

	public BigDecimal getHashrate24hr() {
		return hashrate24hr;
	}

	public void setHashrate24hr(BigDecimal hashrate24hr) {
		this.hashrate24hr = hashrate24hr;
	}

	@Override
	public String toString() {
		return "Coin [name=" + name + ", actual24hr=" + actual24hr + ", hashrate24hr=" + hashrate24hr + "]";
	}
	
	
}

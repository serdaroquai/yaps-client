package org.serdaroquai.me.event;

import java.util.Map;

import org.serdaroquai.me.PoolConfig.Pool;
import org.serdaroquai.me.entity.PoolDetail;

@SuppressWarnings("serial")
public class PoolDetailEvent extends AbstractEvent<Map<String,PoolDetail>> {

	Pool pool;
	
	public PoolDetailEvent(Object source, Map<String,PoolDetail> payload, Pool pool) {
		super(source, payload);
		this.pool = pool;
	}

	public Pool getPool() {
		return pool;
	}
}

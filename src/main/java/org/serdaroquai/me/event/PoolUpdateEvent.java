package org.serdaroquai.me.event;

import java.util.Map;

import org.serdaroquai.me.Algo;
import org.serdaroquai.me.misc.Algorithm;

@SuppressWarnings("serial")
public class PoolUpdateEvent extends AbstractEvent<Map<Algorithm,Algo>> {

	public PoolUpdateEvent(Object source, Map<Algorithm, Algo> payload) {
		super(source, payload);
	}
	
}
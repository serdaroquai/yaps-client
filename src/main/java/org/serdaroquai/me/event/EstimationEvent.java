package org.serdaroquai.me.event;

import org.serdaroquai.me.entity.Estimation;

@SuppressWarnings("serial")
public class EstimationEvent extends AbstractEvent<Estimation>{

	public EstimationEvent(Object source, Estimation payload) {
		super(source, payload);
	}

}

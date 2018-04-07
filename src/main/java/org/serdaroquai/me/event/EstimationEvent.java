package org.serdaroquai.me.event;

import java.util.Collection;

import org.serdaroquai.me.entity.Estimation;

@SuppressWarnings("serial")
public class EstimationEvent extends AbstractEvent<Collection<Estimation>>{

	public EstimationEvent(Object source, Collection<Estimation> payload) {
		super(source, payload);
	}

}

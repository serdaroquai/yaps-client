package org.serdaroquai.me.event;

import org.serdaroquai.me.entity.GpuReading;

/**
 * Any update sent from nvidia-smi process
 * 
 * @author simo
 *
 */
@SuppressWarnings("serial")
public class GpuEvent extends AbstractEvent<GpuReading> {
    
	public GpuEvent(Object source, GpuReading payload) {
        super(source, payload);
    }
	
}
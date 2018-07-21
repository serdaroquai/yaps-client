package org.serdaroquai.me.event;

/**
 * When components receive this event, they send a status report
 * 
 * @author simo
 *
 */
@SuppressWarnings("serial")
public class StatusEvent extends AbstractEvent<Void> {

	public StatusEvent(Object source) {
		super(source, null);
	}
}

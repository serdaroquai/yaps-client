package org.serdaroquai.me.components;

import org.serdaroquai.me.Action;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Receives updates from other components and notifies remote server accordingly.
 * For now just send heartbeats every 30 seconds
 * 
 * @author simo
 */
@Component
public class NotificationsManager {

	@Autowired RemoteConnectionManager remoteConnectionManager;
	@Value("${id}") String id;
	
	@Scheduled(fixedDelay=30000)
	public void sendHeartbeat() {
		remoteConnectionManager.send(new Action("alive",id));
	}
}

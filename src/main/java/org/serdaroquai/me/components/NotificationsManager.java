package org.serdaroquai.me.components;

import org.serdaroquai.me.Action;
import org.serdaroquai.me.Config;
import org.serdaroquai.me.Config.LoginParam;
import org.serdaroquai.me.event.StatusEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
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
	@Autowired Config config;
	@Autowired GpuManager gpuManager;
	@Autowired MinerManager minerManager;
	
	@Scheduled(fixedDelay=30000)
	public void sendHeartbeat() {
		remoteConnectionManager.send(new Action("alive",config.getLogin().get(LoginParam.rigId.name())));
	}
	
	@EventListener
	public void handle(StatusEvent event) {
		StringBuilder status = new StringBuilder();
		status.append(config.getLogin().get(LoginParam.rigId.name())).append("\n");
		gpuManager.getGpuReadings().values().forEach(reading -> status.append(reading.toString()).append("\n"));
		status.append(minerManager.getDigest());
		
		remoteConnectionManager.send(new Action("status",status.toString()));
	}
}

package org.serdaroquai.me.components;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import javax.annotation.PreDestroy;

import org.serdaroquai.me.entity.GpuReading;
import org.serdaroquai.me.event.GpuEvent;
import org.serdaroquai.me.misc.Util;
import org.serdaroquai.me.service.ProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class GpuManager {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired ApplicationEventPublisher applicationEventPublisher;
	@Autowired ProcessService processService;
	
	@Value("${gpuMonitor.nvdia-smi-path:}") String smiPath;
	
	Map<Integer, GpuReading> gpuReadings = new ConcurrentHashMap<>();

	Future<Void> gpuMonitoring; 
	
	@Scheduled(initialDelay=0L, fixedDelay=Long.MAX_VALUE)
	public void startGpuMonitoring() {
		if (!Util.isEmpty(smiPath)) {
			logger.info("Starting GPU monitoring..");
			gpuMonitoring = processService.startGpuMonitoring(smiPath);
		}
	}
	
	@PreDestroy
	public void destroy() {
		logger.info("Shutting down GPU monitoring..");
		gpuMonitoring.cancel(true);
	}
	
	@EventListener
	public void handle(GpuEvent event) {
		gpuReadings.put(event.getPayload().getIndex(), event.getPayload());
		logger.debug(event.getPayload().toString());
	}
	
	public Map<Integer, GpuReading> getGpuReadings() {
		return gpuReadings;
	}
	
}

package org.serdaroquai.me.components;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.serdaroquai.me.MinerConfig;
import org.serdaroquai.me.MinerConfig.MinerContext;
import org.serdaroquai.me.event.MinerEvent;
import org.serdaroquai.me.event.MinerUpdateEvent;
import org.serdaroquai.me.event.StrategyChangeEvent;
import org.serdaroquai.me.misc.Algorithm;
import org.serdaroquai.me.service.MinerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *  Controls and tracks the current status of system miner.
 *  
 * @author simo
 */
@Component
public class MinerManager {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired ApplicationEventPublisher applicationEventPublisher;
	@Autowired MinerService minerService;
	@Autowired MinerConfig minerConfig;
	
	@Value("${miner.enable:true}") boolean isEnabled;
	@Value("${log.minerEvents:false}") boolean logMinerEvents;
	
	
	Future<Void> activeMiner;
	Algorithm activeAlgo = null;
	Algorithm targetAlgo = null;
	StrategyChangeEvent latestEvent;
	
	AtomicBoolean isShuttingDown = new AtomicBoolean(false);
	AtomicBoolean isStarted = new AtomicBoolean(false);
	LocalDateTime startedSince;
	
	// Log of what we have been mining
	List<MinerEvent> minerEvents= new LinkedList<MinerEvent>();
	
	private boolean profitSwitching = false;
	
	@Scheduled(initialDelayString="${minerStart.initialDelay:60000}", fixedDelay=Long.MAX_VALUE)
	public void startProfitSwitching() {
		setProfitSwitching(true);
	}
	
	public void setProfitSwitching(boolean profitSwitching) {
		String line = String.format("Automatic profit switching = %b", profitSwitching);
		logger.info(line);
		this.profitSwitching = profitSwitching;
		applicationEventPublisher.publishEvent(new MinerUpdateEvent(this, line));
	}
	
	@EventListener
	public void handleMinerEvent(MinerEvent event) {
		
		// add it to list of events
		minerEvents.add(event);
		
		if (event.isDisconnected()) {
			isStarted.compareAndSet(true, false);
			isShuttingDown.compareAndSet(true, false);
			startedSince = null;
			applicationEventPublisher.publishEvent(new MinerUpdateEvent(this, "Miner stopped."));
			
			if (profitSwitching && targetAlgo != null) {
				startMiner(targetAlgo);
			}
			
		} else {
			if(logMinerEvents) {
				logger.info(event.getLine());
			}
			applicationEventPublisher.publishEvent(new MinerUpdateEvent(this, event.getLine()));
		}
	}
	
	public void startMiner(Algorithm algo) {
		
		if (!isEnabled) {
			return;
		}
		
		MinerContext minerContext = minerConfig.getMinerMap().get(algo);
		if (minerContext == null) {
			 logger.warn(String.format("Miner for %s is not set",algo));
			 return;
		}
		
		// make sure a prev miner not shutting down and is not started 
		if (!isShuttingDown.get() && isStarted.compareAndSet(false, true)) {
			//isStarted = true, isShuttingDown = false
			
			// start miner
			activeMiner = minerService.startMiner(minerContext);
			activeAlgo = minerContext.getAlgo();
			startedSince = LocalDateTime.now();
			
		} else {
			logger.warn("There is already a miner running");
		}
	}
	
	public boolean stopCurrentMiner() {
		if (!isEnabled) {
//			logger.info("MinerManager is not enabled");
			return false;
		}
		
		// make sure miner isStarted and is not shutting down
		if (isStarted.get() && isShuttingDown.compareAndSet(false, true)) {
			//isStarted = true, isShuttingDown = true
			
			//stop miner
			activeMiner.cancel(true);
			applicationEventPublisher.publishEvent(new MinerUpdateEvent(this, "Miner stopping..."));
			
			return true;
			
		} else {
			logger.warn(String.format("There is no running miner or it is trying to stop: isStarted: %s, isShuttingDown: %s",isStarted.get(), isShuttingDown.get()));
			return false;
		}
		
	}
	
	public String getDigest() {
		return String.format("activeAglo: %s, targetAlgo: %s, isStarted: %s, isShuttingDown: %s, startedSince: %s, latest strategy logs: %s", 
				activeAlgo, 
				targetAlgo, 
				isStarted.get(), 
				isShuttingDown.get(), 
				startedSince == null ? "" : startedSince.toString(), 
				latestEvent.getLogsAsString());
	}
	
	@EventListener
	public void onStrategyChangeEvent(StrategyChangeEvent event) {

		latestEvent = event;
		targetAlgo = event.getStrategy().getAlgo();
		
		// check if we need to switch
		if (shouldChangeAlgo()) {
			if (!stopCurrentMiner()) {
				startMiner(targetAlgo);
			}; // automatically starts miner if in profitSwitch mode
		}
	}
	
	private boolean shouldChangeAlgo() {
		return profitSwitching && targetAlgo != null && !targetAlgo.equals(activeAlgo);
	}
	
	public Optional<LocalDateTime> getStartedSince() {
		return Optional.ofNullable(startedSince);
	}
	
	/**
	 * Returns how many seconds it has been since the start of last miner
	 *  
	 * @param unit
	 * @return
	 */
	public long miningFor(ChronoUnit unit) {
		if (!getStartedSince().isPresent()) {
			return 0L;
		} else {
			return unit.between(getStartedSince().get(), LocalDateTime.now());			
		}
	}
	
	/**
	 * Returns how many seconds we have spent on an algo since last server start
	 * 
	 * @param algo
	 * @return
	 */
	public long getTotalMiningSeconds(Algorithm algo) {
				
		long totalMillis = 0;
		long tempStart = 0;
		for (MinerEvent event: minerEvents) {
			
			if (!event.getPayload().getAlgo().equals(algo))
				continue;
			
			if (!event.isDisconnected()) {
				tempStart = event.getStartDate().toEpochMilli();
			} else {
				if (tempStart != 0) {
					totalMillis += event.getTimestamp()-tempStart;
					tempStart = 0;
				}
			}
		}
		
		// check if we are still mining that algo
		if (tempStart != 0) {
			totalMillis += Instant.now().toEpochMilli() - tempStart; 
		}
		
		return totalMillis / 1000;
	}
	
	public Map<Algorithm,Long> getTotalMiningTime() {
		return minerConfig.getMinerMap().keySet().stream()
			.collect(Collectors.toMap(algo -> algo,algo -> getTotalMiningSeconds(algo)));
	}
	
}

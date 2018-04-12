package org.serdaroquai.me.components;

import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import org.serdaroquai.me.MinerConfig;
import org.serdaroquai.me.MinerConfig.MinerContext;
import org.serdaroquai.me.event.MinerEvent;
import org.serdaroquai.me.event.StrategyChangeEvent;
import org.serdaroquai.me.misc.Algorithm;
import org.serdaroquai.me.misc.MinerStatus;
import org.serdaroquai.me.service.MinerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;

/**
 *  Controls and tracks the current status of system miner.
 *  
 * @author simo
 */
//@Component
@Deprecated()
public class MinerManagerV2 {

	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	AtomicReference<MinerStatus> minerStatus = new AtomicReference<MinerStatus>(MinerStatus.STOPPED);
	
	@Autowired ApplicationEventPublisher applicationEventPublisher;
	@Autowired MinerService minerService;
	@Autowired MinerConfig minerConfig;
	
	
	Future<Void> activeMiner;
	AtomicReference<Optional<MinerContext>> activeContext = new AtomicReference<Optional<MinerContext>>(Optional.empty()); 
	AtomicReference<Optional<Algorithm>> targetAlgorithm = new AtomicReference<Optional<Algorithm>>(Optional.empty());
	
	@EventListener
	public void handleMinerEvent(MinerEvent event) {
		minerStatus.set(event.isDisconnected() 
				? MinerStatus.STOPPED 
				: MinerStatus.STARTED);
		activeContext.set(event.isDisconnected() 
				? Optional.empty() 
				: Optional.of(event.getPayload()));
		
		executeNext();
	}
	
	private void executeNext() {
	}
	
	
	private void stopMiner() {
		if (minerStatus.compareAndSet(MinerStatus.STARTED, MinerStatus.STOPPING)) {
			activeMiner.cancel(true);
			activeMiner = null;
		}
	}
	
	private void startMiner(MinerContext context) {
		if (minerStatus.compareAndSet(MinerStatus.STOPPED, MinerStatus.STARTING)) {
			activeMiner = minerService.startMiner(context);
			activeContext.set(Optional.of(context));
		} else {
//			commandInQueue.add(stop());
//			commandInQueue.add(mine(context));
		}
	}
	
	private Runnable mine(MinerContext context) {
		return () -> startMiner(context);
	}
	
	private Runnable stop() {
		return () -> stopMiner();
	}
	
	@EventListener
	public void onStrategyChangeEvent(StrategyChangeEvent event) {
		Algorithm algo = event.getStrategy().getAlgo();
		
		MinerContext minerContext = minerConfig.getMinerMap().get(algo);
		if (minerContext != null) {
			
		}
	}
	
	
//	public void startMiner(Algorithm algo) {
//		synchronized (minerStatus) {
//			if (MinerStatus.STOPPED.equals(minerStatus)) {
//					
//				MinerContext minerContext = minerConfig.getMinerMap().get(algo);
//				if (minerContext != null) {
//					
//					activeMiner = minerService.startMiner(minerContext);
//					minerStatus.setStarting(true);
//					
//				} else {
//					logger.warn(String.format("Miner for %s is not set",algo));				
//				}
//				
//			} else {
//				logger.warn("There is already a miner running");
//			}
//		}
//	}
//	
//	public boolean stopCurrentMiner() {
//		
//		synchronized (minerStatus) {
//			if (!minerStatus.isStarting() && minerStatus.isStarted() && !minerStatus.isStopping()) {
//				
//			}
//		}
//		// make sure miner isStarted and is not shutting down
//		if (isStarted.get() && isShuttingDown.compareAndSet(false, true)) {
//			//isStarted = true, isShuttingDown = true
//			
//			//stop miner
//			activeMiner.cancel(true);
//			applicationEventPublisher.publishEvent(new MinerUpdateEvent(this, "Miner stopping..."));
//			
//			return true;
//			
//		} else {
//			logger.warn(String.format("There is no running miner or it is trying to stop: isStarted: %s, isShuttingDown: %s",isStarted.get(), isShuttingDown.get()));
//			return false;
//		}
//		
//	}
	
	

	
	
}

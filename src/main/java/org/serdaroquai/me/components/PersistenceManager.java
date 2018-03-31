package org.serdaroquai.me.components;

import org.serdaroquai.me.event.EstimationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Catches information and persists it.
 * Decoupling history logging from in memory operations 
 * 
 * @author simo
 *
 */
@Component
public class PersistenceManager {

	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
//	@Autowired DifficultyRepository difficultyRepository;
	@Autowired EstimationRepository estimationRepository;
//	@Value("${persistence.enable:false}") boolean isEnabled;
		
//	@EventListener
//	public void handleEvent(DifficultyUpdateEvent event) {
//		if (isEnabled) {
//			difficultyRepository.save(event.getPayload());			
//		}
//	}
	
	@EventListener
	public void handleEvent(EstimationEvent event) {
//		if (isEnabled) {
			estimationRepository.save(event.getPayload());
//		}
	}
	
//	@EventListener
//	public void handleEvent(MinerEvent event) {
//	}

	
}

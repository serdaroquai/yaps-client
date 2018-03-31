package org.serdaroquai.me.misc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.stereotype.Component;

@Component
public class MyStompSessionHandler implements StompSessionHandler{

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
		logger.info("!!Connected " + session.toString());
		
	}
	
	@Override
	public void handleTransportError(StompSession session, Throwable exception) {
		logger.info("!!Transport error");
		
	}
	
	@Override
	public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload,
			Throwable exception) {
		logger.error(exception.getMessage(), exception);
		
	}
	
	@Override
	public void handleFrame(StompHeaders headers, Object payload) {
		logger.info("!!handleFrame " + payload);
		
	}
	
	@Override
	public java.lang.reflect.Type getPayloadType(StompHeaders headers) {
		return ClientUpdate.class;
	}
	
}

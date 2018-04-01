package org.serdaroquai.me.components;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONException;
import org.serdaroquai.me.Action;
import org.serdaroquai.me.Action.Command;
import org.serdaroquai.me.entity.Estimation;
import org.serdaroquai.me.event.EstimationEvent;
import org.serdaroquai.me.misc.ClientUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSession.Subscription;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@Component
public class RemoteConnectionManager implements StompSessionHandler{

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired WebSocketStompClient stompClient; 
	@Value("${remote.url:ws://yaps.serdarbaykan.com:8090/pokerNight}") String remoteUrl;
	@Autowired ApplicationEventPublisher applicationEventPublisher;
	
	
	AtomicBoolean isConnected = new AtomicBoolean(false);
	StompSession stompSession;
	Map<String,Subscription> subscriptions = new ConcurrentHashMap<>();
	
	@Scheduled(fixedDelay=10000)
	private void connect() throws InterruptedException, ExecutionException, JSONException {
		
		if (isConnected.compareAndSet(false, true)) {
			try {
				logger.info(String.format("Establishing websocket connection to %s", remoteUrl));
				ListenableFuture<StompSession> connect = stompClient.connect(remoteUrl, this);
				stompSession = connect.get();
				
			} catch (Exception e) {
				disconnect();
				logger.error(String.format("Could not connect to %s", remoteUrl));
			}
		}
	}
	
	public void send(Object message) {
		if (isConnected.get() && stompSession != null) {
			logger.info(String.format("Sending %s", message));
			stompSession.send("/app/message", message);
		}
	}
	
	
	@Override
	public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
		logger.info(String.format("Connected to websocket %s", session.toString()));
		
		// subscribe to estimations topic
		Subscription subscription = stompSession.subscribe("/topic/estimations", this);
		subscriptions.put("/topic/estimations", subscription);
		
		// send register message
		send(new Action(Command.register,null));
	}
	
	@Override
	public void handleTransportError(StompSession session, Throwable exception) {
		logger.info(String.format("%s Transport error: %s, %s", session, exception.getMessage(), exception));
		disconnect();
	}
	
	@Override
	public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload,
			Throwable exception) {
		logger.error(String.format("Exception while processing STOMP frame: %s, %s", exception.getMessage(), exception));
		disconnect();
	}
	
	@Override
	public void handleFrame(StompHeaders headers, Object stompPayload) {
		
		if (headers.get("destination").get(0).equals("/topic/estimations")) {
			Estimation estimation = ((ClientUpdate) stompPayload).get("payload");
			logger.info(estimation.toString());
			applicationEventPublisher.publishEvent(new EstimationEvent(this, estimation));			
		}
		
	}
	
	@Override
	public java.lang.reflect.Type getPayloadType(StompHeaders headers) {
		return ClientUpdate.class;
	}
	
	private void disconnect() {
		isConnected.set(false);
		if (stompSession != null) {
			stompSession.disconnect();
		}
		stompSession = null;
		subscriptions.clear();		
	}
	
}

package org.serdaroquai.me.components;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONException;
import org.serdaroquai.me.entity.Estimation;
import org.serdaroquai.me.event.EstimationEvent;
import org.serdaroquai.me.misc.Algorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class RemoteConnectionManager implements StompSessionHandler{

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Value("${remote.url:ws://yaps.serdarbaykan.com:8090/pokerNight}") String remoteUrl;
	@Value("${token}") String token;
	@Value("${userId}") String userId;
	@Value("${id}") String id;
	
	@Autowired WebSocketStompClient stompClient; 
	@Autowired ApplicationEventPublisher applicationEventPublisher;
	@Autowired ObjectMapper objectMapper;
	
	AtomicBoolean isConnected = new AtomicBoolean(false);
	StompSession stompSession;
	
	@Scheduled(fixedDelay=10000)
	private void connect() throws InterruptedException, ExecutionException, JSONException {
		
		if (isConnected.compareAndSet(false, true)) {
			try {
				logger.info(String.format("Establishing websocket connection to %s", remoteUrl));
				
				WebSocketHttpHeaders handshakeHeaders = new WebSocketHttpHeaders();
				handshakeHeaders.set("token", token);
				handshakeHeaders.set("userId", userId);
				
				ListenableFuture<StompSession> connect = stompClient.connect(remoteUrl, handshakeHeaders, this);
				stompSession = connect.get();
				
			} catch (Exception e) {
				logger.error(String.format("Could not connect to %s", remoteUrl));
				disconnect();
			}
		}
	}
	
	public void send(Object message) {
		if (isConnected.get() && stompSession != null) {
			String destination = "/app/message";
			logger.info(String.format("Sending [%s]<- %s",destination, message));
			stompSession.send(destination, message);
		}
	}
	
	
	@Override
	public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
		logger.info(String.format("Connected to websocket %s", session.toString()));
		
		// subscribe to private channel
		stompSession.subscribe("/user/queue/private", this);
		
		// subscribe to estimations
		stompSession.subscribe("/topic/estimations", this);
		
	}
	
	@Override
	public void handleTransportError(StompSession session, Throwable exception) {
		logger.info(String.format("%s Transport error: %s, %s", session, exception.getMessage(), exception));
		disconnect();
	}
	
	@Override
	public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload,
			Throwable exception) {
		logger.error(String.format("Exception while processing STOMP frame: %s", exception.getMessage()), exception);
		disconnect();
	}
	
	@Override
	public void handleFrame(StompHeaders headers, Object stompPayload) {
		logger.info(String.format("Received [%s]-> %s", headers.getDestination(),stompPayload.toString()));
		
		JsonNode message = (JsonNode) stompPayload;
		
		switch (headers.get("destination").get(0)) {
		case "/topic/estimations":
			
			try {
				
				Estimation estimation = objectMapper.treeToValue(message.get("payload").get("payload"), Estimation.class);
				logger.info(estimation.toString());
				
				applicationEventPublisher.publishEvent(new EstimationEvent(this, estimation));	
				
			} catch (JsonProcessingException e) {
				throw new RuntimeException(String.format("Can not parse %s", stompPayload));
			}
			
			break;
		case "/user/queue/private":

			if ("estimationsUpdate".equals(message.get("type").textValue())) {	
				try {
					
					@SuppressWarnings("unchecked")
					Map<Algorithm, BigDecimal> result = objectMapper.convertValue(message.get("payload"), Map.class);
					logger.info(result.toString());
					
				} catch (Exception e) {
					throw new RuntimeException(String.format("Can not parse %s", stompPayload));
				}
			}
			break;
		default:			
		}
		
		
	}
	
	@Override
	public java.lang.reflect.Type getPayloadType(StompHeaders headers) {
		return JsonNode.class;
	}
	
	private void disconnect() {
		isConnected.set(false);
		if (stompSession != null) {
			stompSession.disconnect();
		}
		stompSession = null;
	}
	
}

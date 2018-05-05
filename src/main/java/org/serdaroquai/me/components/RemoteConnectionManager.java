package org.serdaroquai.me.components;

import static org.serdaroquai.me.misc.Util.isEmpty;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.json.JSONException;
import org.serdaroquai.me.Config;
import org.serdaroquai.me.Config.LoginParam;
import org.serdaroquai.me.PoolConfig;
import org.serdaroquai.me.entity.Estimation;
import org.serdaroquai.me.event.EstimationEvent;
import org.serdaroquai.me.misc.Algorithm;
import org.serdaroquai.me.misc.ClientVersion;
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
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class RemoteConnectionManager implements StompSessionHandler{

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired Config config;
	@Autowired PoolConfig poolConfig;
	@Value("${remote.url:ws://yaps.serdarbaykan.com:8090/pokerNight}") String remoteUrl;
	
	@Autowired WebSocketStompClient stompClient; 
	@Autowired ApplicationEventPublisher applicationEventPublisher;
	@Autowired ObjectMapper objectMapper;
	
	AtomicBoolean isConnected = new AtomicBoolean(false);
	StompSession stompSession;
	List<String> subscriptions;
	
	@PostConstruct
	public void init() {
		subscriptions = poolConfig.getPool().values().stream()
			.map(pool -> String.format("/topic/%s", pool.getName()))
			.collect(Collectors.toList());
		
		if (subscriptions.isEmpty()) {
			throw new IllegalStateException("You need to set up a pool in config");
		}
	}
	
	@Scheduled(fixedDelay=10000)
	private void connect() throws InterruptedException, ExecutionException, JSONException {
		
		if (isConnected.compareAndSet(false, true)) {
			try {
				logger.info(String.format("Establishing websocket connection to %s", remoteUrl));
				
				WebSocketHttpHeaders handshakeHeaders = new WebSocketHttpHeaders();
				config.getLogin().entrySet().stream()
					.filter(entry -> !isEmpty(entry.getValue()))
					.forEach(entry -> handshakeHeaders.add(entry.getKey(), entry.getValue()));

				//set version
				handshakeHeaders.set(LoginParam.version.name(), ClientVersion.v1_03.name());
				
				stompClient.connect(remoteUrl, handshakeHeaders, this);
				
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
		
		stompSession = session;
				
		// subscribe to private channel
		stompSession.subscribe("/user/queue/private", this);
		
		// subscribe to pool estimations
		subscriptions.forEach(to -> stompSession.subscribe(to, this));
		
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
		
		try {

			String destination = headers.get("destination").get(0);
			if (subscriptions.contains(destination)) {
				
				Estimation estimation = objectMapper.treeToValue(message.get("payload").get("payload"), Estimation.class);
				applicationEventPublisher.publishEvent(new EstimationEvent(this, Arrays.asList(estimation)));	
				
			} else if ("/user/queue/private".equals(destination)) {
				
				if ("estimationsUpdate".equals(message.get("type").textValue())) {	
					
					Map<Algorithm,Estimation> estimations = objectMapper.convertValue(message.get("payload"), new TypeReference<Map<Algorithm,Estimation>>() { });
					applicationEventPublisher.publishEvent(new EstimationEvent(this, estimations.values()));
					
				}
			}
		
		} catch (Exception e) {
			// TODO update client necessary
			logger.warn(String.format("Can not parse %s", stompPayload));
		}
		
	}
	
	@Override
	public java.lang.reflect.Type getPayloadType(StompHeaders headers) {
		return JsonNode.class;
	}
	
	private void disconnect() {
		try {
			if (stompSession != null) {
				stompSession.disconnect();
				stompSession = null;			
			}
		} finally {
			isConnected.set(false);			
		}
	}
	
}

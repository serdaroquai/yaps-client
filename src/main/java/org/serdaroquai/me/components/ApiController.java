package org.serdaroquai.me.components;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.serdaroquai.me.Action;
import org.serdaroquai.me.Action.Command;
import org.serdaroquai.me.CoinConfig;
import org.serdaroquai.me.CoinConfig.Coin;
import org.serdaroquai.me.TelegramBot;
import org.serdaroquai.me.misc.Algorithm;
import org.serdaroquai.me.misc.MyStompSessionHandler;
import org.serdaroquai.me.service.RestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@RestController
@RequestMapping("/api")
public class ApiController {

	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired RestService restService;
	@Autowired EstimationManager estimationManager;
	@Autowired CoinConfig coinConfig;
	@Autowired TelegramBot telegramBot;
	@Autowired WebSocketStompClient stompClient; 
	@Autowired MyStompSessionHandler handler;
	
	@RequestMapping(value ="/connect")
	private void connect() throws InterruptedException, ExecutionException, JSONException {
		String url = "ws://yaps.serdarbaykan.com:8080/pokerNight";
		ListenableFuture<StompSession> connect = stompClient.connect(url, handler);
		StompSession stompSession = connect.get();
		
		/* Subscription subscribe = */ stompSession.subscribe("/topic/estimations", handler);
		stompSession.send("/app/message",new Action(Command.register,null));
	}
	
	@RequestMapping(value ="/getEstimations")
	public Map<Algorithm, BigDecimal> getEstimations() {
		return estimationManager.getLatestEstimations();
	}
	
	@RequestMapping(value ="/getNormalizedEstimations")
	public Map<Algorithm, BigDecimal> getNormalizedEstimations() {
		return estimationManager.getLatestNormalizedEstimations();
	}
	
	@RequestMapping(value ="/getCoinConfig")
	public Map<String,Coin> getCoinConfig() {
		return coinConfig.getCoin();
	}
	
	
	
	
}

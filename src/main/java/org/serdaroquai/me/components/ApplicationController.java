package org.serdaroquai.me.components;

import org.serdaroquai.me.Action;
import org.serdaroquai.me.event.ProfitabilityUpdateEvent;
import org.serdaroquai.me.misc.UIUpdate;
import org.serdaroquai.me.misc.UIUpdate.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Controller
public class ApplicationController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired SimpMessagingTemplate template;

	@MessageMapping("/message")
	public void accept(Action action) throws Exception {
		logger.info(String.format("Received: %s",action));
	}

	public void dispatch(UIUpdate<?> update) {
		template.convertAndSend("/topic/estimations", update);
	}

	@EventListener
	public void handleWebSocketConnectListener(SessionConnectedEvent event) {
		logger.info(event.toString());
	}
	
	
	@EventListener
	public void handleSessionSubscribeListener(SessionSubscribeEvent event) {
		String destination = (String) event.getMessage().getHeaders().get("simpDestination");
		
		switch (destination) {
		case "/topic/estimations":
			dispatch(new UIUpdate<Void>(Type.profitabilityUpdate, null));
			break;
		default:
			
		}
	}

	@EventListener
	public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
		logger.info(event.toString());
	}

	@EventListener
	public void handleProfitabilityUpdate(ProfitabilityUpdateEvent event) {
		dispatch(new UIUpdate<Void>(Type.profitabilityUpdate, null));
	}

}

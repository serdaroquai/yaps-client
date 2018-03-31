package org.serdaroquai.me;

import org.serdaroquai.me.Action.Command;
import org.serdaroquai.me.misc.ClientUpdate;
import org.serdaroquai.me.misc.ClientUpdate.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Controller
public class ApplicationController {

	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired private SimpMessagingTemplate template;

	@MessageMapping("/message")
	public void accept(@Payload Action action) throws Exception {

		if (Command.register.equals(action.getCommand())) {
			 
			 // send estimations
			 dispatch(new ClientUpdate.Of(Type.estimationsUpdate).build());

			 //send pool estimations
			 dispatch(new ClientUpdate.Of(Type.poolUpdate).build());
		}
	}

	public void dispatch(ClientUpdate update) {
		template.convertAndSend("/topic/estimations", update);
	}

	@EventListener
	public void handleWebSocketConnectListener(SessionConnectedEvent event) throws InterruptedException {

	}

	@EventListener
	public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
	}


}

package org.serdaroquai.me;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSocketMessageBroker
@EnableAsync
@EnableScheduling
public class ApplicationConfig implements AsyncConfigurer, WebSocketMessageBrokerConfigurer{

	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
		
	@Bean
	public WebSocketStompClient webSocketClient(TaskScheduler taskScheduler) {
		
		List<Transport> transports = new ArrayList<Transport>();
		transports.add(new WebSocketTransport( new StandardWebSocketClient()) );
		WebSocketClient transport = new SockJsClient(transports);
		WebSocketStompClient stompClient = new WebSocketStompClient(transport);
		
		stompClient.setMessageConverter(new MappingJackson2MessageConverter());
		stompClient.setTaskScheduler(taskScheduler); // for heartbeats
		return stompClient;
	}
	
	//spring asynchronous event handling
	@Bean(name = "applicationEventMulticaster")
    public ApplicationEventMulticaster simpleApplicationEventMulticaster(Executor executor) {
        SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
        eventMulticaster.setTaskExecutor(executor);
        return eventMulticaster;
    }
	
	@Override
	@Bean("asyncExecutor")
	public Executor getAsyncExecutor() {
		SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
		taskExecutor.setDaemon(true);
		return taskExecutor;
	}
	
	@Bean
	@Primary
	public Executor getExecutor() {
		SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
		return taskExecutor;
	}
	
	//jackson json object mapper for manual mapping
	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}
	
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
//        config.enableSimpleBroker("/topic", "/queue");
//        config.setApplicationDestinationPrefixes("/app");
//        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/dummy").withSockJS();
    }
}
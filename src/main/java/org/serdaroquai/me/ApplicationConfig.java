package org.serdaroquai.me;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSocketMessageBroker
@EnableAsync
@EnableScheduling
public class ApplicationConfig implements AsyncConfigurer {

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
		SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor("exec_asyncTask");
		taskExecutor.setDaemon(true);
		return taskExecutor;
	}
	
	@Bean
	@Primary
	public Executor getExecutor() {
		SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor("exec");
		return taskExecutor;
	}
	
	//jackson json object mapper for manual mapping
	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}
}
package org.serdaroquai.me;

import java.util.concurrent.Executor;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;
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
	@Primary
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(Runtime.getRuntime().availableProcessors() * 2);
		executor.setDaemon(true);
		executor.setThreadNamePrefix("asyncExec-");
		executor.initialize();
		return executor;
	}
		
	//jackson json object mapper for manual mapping
	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}
	
	@Bean
	public RestTemplate restTemplate(
			@Value("${restService.connectTimeout:15000}") int connectTimeout,
			@Value("${restService.readTimeout:15000}") int readTimeout) {
		
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
		factory.setConnectTimeout(connectTimeout);
		factory.setReadTimeout(readTimeout);
		return  new RestTemplate(factory);
		
	}
}
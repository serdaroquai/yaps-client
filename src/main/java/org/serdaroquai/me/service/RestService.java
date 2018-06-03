package org.serdaroquai.me.service;

import java.util.Map;
import java.util.concurrent.Future;

import org.serdaroquai.me.PoolConfig.Pool;
import org.serdaroquai.me.entity.PoolDetail;
import org.serdaroquai.me.event.PoolDetailEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RestService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired ApplicationEventPublisher applicationEventPublisher;
	@Autowired RestTemplate restTemplate;
	@Autowired ObjectMapper objectMapper;
	
	//https://www.ahashpool.com/api/currencies/
	@Async
	public Future<Map<String,PoolDetail>> getPoolDetails(Pool pool) {
		
		try {
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(pool.getCurrencyUrl());
			
			//ahashpool returns text/xml, so receive as string parse json manually
			ResponseEntity<String> response = restTemplate.exchange(
					builder.build().encode().toUri(), 
					HttpMethod.GET, 
					null, 
					String.class);
			
			Map<String,PoolDetail> map = objectMapper.readValue(response.getBody(), new TypeReference<Map<String, PoolDetail>>(){});
		
			// set key values 
			map.entrySet().parallelStream().forEach(e-> e.getValue().setKey(e.getKey()));
			
			applicationEventPublisher.publishEvent(new PoolDetailEvent(this, map, pool));
			
			return new AsyncResult<Map<String,PoolDetail>>(map);
			
		} catch (Exception e) {
			logger.error(String.format("Can not get %s details: %s", pool, e.getMessage()));
		}
		
		return new AsyncResult<Map<String,PoolDetail>>(null);
	}

	
}

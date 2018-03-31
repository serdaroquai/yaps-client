package org.serdaroquai.me.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.serdaroquai.me.Algo;
import org.serdaroquai.me.entity.PoolDetail;
import org.serdaroquai.me.entity.WhattomineBrief;
import org.serdaroquai.me.entity.WhattomineBriefEnvelope;
import org.serdaroquai.me.entity.WhattomineDetail;
import org.serdaroquai.me.event.PoolQueryEvent;
import org.serdaroquai.me.misc.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RestService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired ApplicationEventPublisher applicationEventPublisher;
	@Autowired RestTemplate restTemplate;
	@Autowired ObjectMapper objectMapper;
	
	//https://www.cryptopia.co.nz/api/GetMarkets/BTC
	public Map<String,BigDecimal> getCryptopiaMarkets() {
		
		try {
			
			String urlString = "https://www.cryptopia.co.nz/api/GetMarkets/BTC";
			logger.info(String.format("Fetching resource %s", urlString));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://www.cryptopia.co.nz/api/GetMarkets/BTC");
			
			ResponseEntity<String> response = restTemplate.exchange(
					builder.build().encode().toUri(), 
					HttpMethod.GET, 
					null, 
					String.class);
			
			JsonNode result = objectMapper.readTree(response.getBody()).get("Data");
			
			return StreamSupport.stream(result.spliterator(), true)
					.collect(Collectors.toMap(
							node -> node.get("Label").textValue().replaceFirst("/BTC", ""), 
							node -> node.get("LastPrice").decimalValue()));
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public Map<String,BigDecimal> getCoinExchangeIoMarkets() {
		
		try {
			
			String urlString = "https://www.coinexchange.io/api/v1/getmarkets";
			logger.info(String.format("Fetching resource %s", urlString));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlString);
			
			ResponseEntity<String> response = restTemplate.exchange(
					builder.build().encode().toUri(), 
					HttpMethod.GET, 
					null, 
					String.class);
			
			Map<String,String> idSymbolMap = new HashMap<>();
			JsonNode result = objectMapper.readTree(response.getBody()).get("result");
			for (final JsonNode node : result) {
				idSymbolMap.put(node.get("MarketID").textValue(), node.get("MarketAssetCode").textValue());
		    }

			
			builder = UriComponentsBuilder.fromHttpUrl("https://www.coinexchange.io/api/v1/getmarketsummaries");
			
			response = restTemplate.exchange(
					builder.build().encode().toUri(), 
					HttpMethod.GET, 
					null, 
					String.class);
			
			Map<String,BigDecimal> symbolPrice = new HashMap<>();
			result = objectMapper.readTree(response.getBody()).get("result");
			
			for (final JsonNode node : result) {
				String symbol = idSymbolMap.get(node.get("MarketID").textValue());
				if (!Util.isEmpty(symbol)) {
					symbolPrice.put(symbol, new BigDecimal(node.get("LastPrice").textValue()));					
				}
		    }
			
			return symbolPrice;
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public WhattomineBriefEnvelope getWhattomineBriefInfo() {
		
		try {
			String urlString = "http://whattomine.com/calculators.json";
			logger.info(String.format("Fetching resource %s", urlString));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlString);
			
			// add header user agent for forbidden whattomine
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			
			ResponseEntity<WhattomineBriefEnvelope> response = restTemplate.exchange(
					builder.build().encode().toUri(), 
					HttpMethod.GET, 
					entity, 
					WhattomineBriefEnvelope.class);

			return response.getBody();
			
		} catch (Exception e) {
			logger.error(String.format("WhattomineBrief exception: %s, %s",e.getMessage()));
			throw new RuntimeException(e);
		}
	}
	
	public WhattomineDetail getWhattomineDetail(WhattomineBrief brief) {
		
		
		try {
			
			String urlString = String.format("http://whattomine.com/coins/%s.json", brief.getId());
			logger.info(String.format("Fetching resource %s", urlString));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlString);
			
			// add header user agent for forbidden whattomine
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			
			ResponseEntity<WhattomineDetail> response = restTemplate.exchange(
					builder.build().encode().toUri(), 
					HttpMethod.GET, 
					entity, 
					WhattomineDetail.class);
			
			return response.getBody();
			
		} catch (Exception e) {
			logger.error(String.format("WhattomineDetail exception: %s, %s",e.getMessage(), brief));
			throw new RuntimeException(e);
		}
		
	}
	
//	@Async
//	public Future<Estimation> getEstimatedPrice(BigDecimal diff, Info info) {
//		Estimation body = null;
//		
//		try {
//			
//			String urlString = String.format("https://whattomine.com/coins/%s.json?hr=%s&d_enabled=true&d=%s", info.getUrl(), info.getHr(),diff.toString());
//			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlString);
//			
//			// add header user agent for forbidden whattomine
//			HttpHeaders headers = new HttpHeaders();
//			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//			headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
//			
//			HttpEntity<?> entity = new HttpEntity<>(headers);
//			ParameterizedTypeReference<Estimation> responseType = new ParameterizedTypeReference<Estimation>() {};
//			
//			ResponseEntity<Estimation> response = restTemplate.exchange(
//					builder.build().encode().toUri(), 
//					HttpMethod.GET, 
//					entity, 
//					responseType);
//			
//			body = response.getBody();
////			body.setTimestamp(Instant.now().toEpochMilli()); 
////			body.setDifficulty(diff);
//			
//			applicationEventPublisher.publishEvent(new EstimationQueryEvent(this, body));
//		} catch (Exception e) {
//			logger.error(e.getMessage(),e);
//		}
//		return new AsyncResult<Estimation>(body);
//		
//	}
	
	@Async
	public Future<Map<String,Algo>> getPoolStatus() throws JsonParseException, JsonMappingException, IOException {
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://ahashpool.com/api/status");
		
		//ahashpool returns text/xml, so receive as string parse json manually
		ResponseEntity<String> response = restTemplate.exchange(
		        builder.build().encode().toUri(), 
		        HttpMethod.GET, 
		        null, 
		        String.class);
		
		Map<String,Algo> map = objectMapper.readValue(response.getBody(), new TypeReference<Map<String, Algo>>(){});
		
		// Set Algo timestamps
		long now = Instant.now().toEpochMilli();
		map.values().stream().forEach(a -> a.setTimestamp(now));
		
		applicationEventPublisher.publishEvent(new PoolQueryEvent(this, map, "ahashpool"));
		
		return new AsyncResult<Map<String,Algo>>(map);

	}
	
	//https://www.ahashpool.com/api/currencies/
	public Map<String,PoolDetail> getPoolDetails() {
		
		Map<String,PoolDetail> map = Collections.emptyMap();
		
		try {
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://ahashpool.com/api/currencies");
			
			//ahashpool returns text/xml, so receive as string parse json manually
			ResponseEntity<String> response = restTemplate.exchange(
					builder.build().encode().toUri(), 
					HttpMethod.GET, 
					null, 
					String.class);
			
			map = objectMapper.readValue(response.getBody(), new TypeReference<Map<String, PoolDetail>>(){});
			
		} catch (Exception e) {
			//TODO handle exceptions?
			logger.error("Can not get pool details");
		}
		
		return map;

	}
	
}

package org.serdaroquai.me.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.time.Instant;
import java.util.concurrent.Future;

import org.json.JSONObject;
import org.serdaroquai.me.Config.StratumConnection;
import org.serdaroquai.me.event.StratumEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class StratumConnectionService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final String greeting = "{\"method\":\"mining.subscribe\",\"id\":\"1\",\"jsonrpc\":\"2.0\",\"params\":[]}";
	@Autowired ApplicationEventPublisher applicationEventPublisher;
	
	@Async
	public Future<Void> connect(StratumConnection context) {
		
		Instant start = Instant.now();
		
		try {
			Socket socket = new Socket();
			InetSocketAddress address = null;
			
			try {
				// connect
				address = new InetSocketAddress(context.getHost(),context.getPort());
				logger.info(String.format("Connecting to %s (%s)",address.toString(),context.getId()));
				socket.connect(address, 5000);
				socket.setSoTimeout(5000);
				
				// Create input output streams
				PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
				BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				
				// send greeting and subscribe
				output.println(new JSONObject(greeting));
				output.println(new JSONObject(context.getSubscribe()));
				
				String line = "";
				do {
					try {
						line = input.readLine();
						if (line != null) {
							applicationEventPublisher.publishEvent(new StratumEvent(this, context, new JSONObject(line),false));											
						}
					} catch (SocketTimeoutException timeout) {
						//NO-OP
					}
				} while (line != null && !Thread.interrupted());				
			} finally {
				logger.info(String.format("Disconnected from %s (%s)",address.toString(),context.getId()));
				if (socket != null) {
					socket.close();
				}
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		} finally {
			applicationEventPublisher.publishEvent(new StratumEvent(this, context, null, true, start));
		}			
		
		return null;
	}
	
}

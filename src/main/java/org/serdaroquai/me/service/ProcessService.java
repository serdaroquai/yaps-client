package org.serdaroquai.me.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.concurrent.Future;

import org.serdaroquai.me.MinerConfig.MinerContext;
import org.serdaroquai.me.entity.GpuReading;
import org.serdaroquai.me.event.GpuEvent;
import org.serdaroquai.me.event.MinerEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ProcessService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired ApplicationEventPublisher applicationEventPublisher;
	@Value("${minerStart.delay:2000}") long minerStartDelay;
	@Value("${miner.startMinimized:false}") boolean startMinimized;
	
	
	@Async
	public Future<Void> startMiner(MinerContext context) {
		
		Instant started = Instant.now();
		
		try {
			
			logger.info("Starting miner process " + context.toString());
			Runtime.getRuntime().exec(String.format("cmd /c start %s%s", 
					startMinimized ? "/min " : "",
					context.getPath()));
			
			applicationEventPublisher.publishEvent(new MinerEvent(this, context, started, "Started " + context, false));
	        
    		try {
    			while (!Thread.interrupted()) {
					Thread.sleep(1000);
    			}
			} catch (InterruptedException e) {}

    		logger.info("Shutting down miner process " + context.toString());
        	Runtime.getRuntime().exec(String.format("taskkill /im %s", context.getKill()));
        	
        } catch (IOException e) {
        	logger.error("Error with miner process " + context.toString(), e);
        } finally {
        	try {
        		// wait two second before publishing its done event
        		// give it some time to cool off
				Thread.sleep(minerStartDelay);
			} catch (InterruptedException e) {}
        	applicationEventPublisher.publishEvent(new MinerEvent(this, context, started, null, true));
        }
		
		return null;
	}
	
	@Async
	public Future<Void> restartPc() {
		
		try {
			
			logger.info("Restarting PC...");
			Runtime.getRuntime().exec("shutdown -t 30 -r");
        	
        } catch (IOException e) {
        	logger.error("Error restarting PC:", e);
        } 
		
		return null;
	}
	
	@Async
	public Future<Void> cancelRestartPc() {
		
		try {
			
			Runtime.getRuntime().exec("shutdown -a");
			logger.info("Restart Cancelled");
        	
        } catch (IOException e) {
        	logger.error("Error cancelling restart:", e);
        } 
		
		return null;
	}
	
	@Async
	public Future<Void> startGpuMonitoring(String path) {
		try {
			ProcessBuilder builder = new ProcessBuilder(
					path, 
					"--query-gpu=index,power.draw,temperature.gpu,utilization.gpu,utilization.memory",
					"--format=csv,noheader,nounits",
					"-l",
					"5");
			
			builder.redirectErrorStream(true);
			final Process process = builder.start();
			
			try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String line;
				while ((line = br.readLine()) != null || !Thread.interrupted()) {
					String[] readingArr = line.split(",");					
					
					GpuReading reading = new GpuReading(
							Integer.valueOf(readingArr[0].trim()), 
							readingArr[1].trim(),
							Integer.valueOf(readingArr[2].trim()),
							Integer.valueOf(readingArr[3].trim()),
							Integer.valueOf(readingArr[4].trim()));
					
					applicationEventPublisher.publishEvent(new GpuEvent(this, reading));
				}
				
				logger.info("Shutting down gpu monitoring process");
				process.destroy();
			}
	    } catch (Exception e) {
	    	logger.error(String.format("Error running Gpu Monitoring: %s", e.getMessage()));
	    }
		
		return null;
	}
}

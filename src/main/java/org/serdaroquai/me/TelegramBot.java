package org.serdaroquai.me;

import javax.annotation.PreDestroy;

import org.serdaroquai.me.components.EstimationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.generics.BotSession;

public class TelegramBot extends TelegramLongPollingBot{

	private static final Logger logger = LoggerFactory.getLogger(TelegramBot.class);
	private String telegramToken;
	private String botname;
	private String chatId; //each user should have their own chat with their own bot. this is used to send a message
	
	@Autowired ApplicationController controller;
	@Autowired EstimationManager estimationManager;
	
	public TelegramBot(String telegramToken, String botname, String chatId) {
		super();
		this.telegramToken = telegramToken;
		this.botname = botname;
		this.chatId = chatId;
	}
	
	private BotSession botSession;
	
	@Scheduled(fixedDelay=10000) 
	private void startTelegram() {
		if (botSession == null || !botSession.isRunning()) {
			logger.info("Starting telegram bot");	
			try {
				botSession = new TelegramBotsApi().registerBot(this);				
			} catch (Exception e){
				logger.error(String.format("Could not start Telegram API: %s", e));
			}
		}
	}
	
	@PreDestroy
	private void destroy() {
		if (botSession != null || botSession.isRunning()) {
			logger.info("Stopping telegramBot");
			botSession.stop();			
		}
	}
		
	
	public Message sendMessage(String messageText) throws TelegramApiException {
		
		logger.info("Sending:" + messageText);
		
		//notify user
		SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
				.setChatId(chatId)
		        .setText(messageText);
		
		Message result = execute(message);
		return result;
	}
	
	@Override
	public void onUpdateReceived(Update update) {
		
		// We check if the update has a message and the message has text
	    if (update.hasMessage() && update.getMessage().hasText()) {
	    	// log message and chat id
	    	logger.info(String.format("Received from %s: %s", update.getMessage().getChatId(), update.getMessage().getText()));
	    	
	        String text = null;
	        if ("/estimations".equals(update.getMessage().getText())) {
	        	
//	        	text = String.format("Last update: %s ", estimationManager.getLastSuccessfulQuery());
	        	
	        	
	        } else if ("/stratum".equals(update.getMessage().getText())) {	
	        	
//	        	LinkedHashMap<String, LocalDateTime> sorted = difficultyManager.getLatestDifficultyUpdates().entrySet().stream()
//	        		.sorted(Map.Entry.comparingByValue(Comparator.comparing(Difficulty::getTimestamp)))
//	        		.collect(Collectors.toMap(entry->entry.getKey(), 
//	        				entry-> Instant.ofEpochMilli(entry.getValue().getTimestamp()).atZone(ZoneId.systemDefault()).toLocalDateTime(), 
//	        				(oldValue, newValue) -> oldValue, LinkedHashMap::new));
//	        	text = sorted.toString();
	        	
	        } else {
	        	
	        	text = "/status, /estimations, /uptime, /mining, /restart, /cancel, /stratum, /earnings, /timespent";
	        	
	        }
	    	
	        try {
	        	if (text != null) {
	        		sendMessage (text);
	        	}
	        } catch (TelegramApiException e) {
	            logger.error(e.getMessage(),e);
	        }
	    }
		
	}
	
	@Override
	public String getBotUsername() {
		return botname;
	}

	@Override
	public String getBotToken() {
		return telegramToken;
	}

}

package org.serdaroquai.me.components;

import java.math.BigDecimal;
import java.util.Map;

import org.serdaroquai.me.misc.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {

	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired ProfitabilityManager profitabilityManager;
	
	//algo, bigdecimal
	@RequestMapping(value ="/estimations")
	public Map<String, Pair<String,BigDecimal>> getBrief() {
		return profitabilityManager.getBrief();
	}
	
}

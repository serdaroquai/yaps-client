package org.serdaroquai.me.strategy;

import org.serdaroquai.me.components.ProfitabilityManager;
import org.serdaroquai.me.event.StrategyChangeEvent;

public interface IStrategy {

	/**A strategy must override this method and generate a StrategyChangeEvent if necessary
	 * 
	 * @param manager
	 * @param config
	 * @return
	 */
	StrategyChangeEvent generateAction(ProfitabilityManager manager);
}

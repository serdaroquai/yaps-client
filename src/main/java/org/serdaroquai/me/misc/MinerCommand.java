package org.serdaroquai.me.misc;

@FunctionalInterface
public interface MinerCommand {

	MinerCommand execute(MinerCommand next);
	
}

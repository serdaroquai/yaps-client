package org.serdaroquai.me.event;

import org.serdaroquai.me.entity.Difficulty;

@SuppressWarnings("serial")
public class DifficultyUpdateEvent extends AbstractEvent<Difficulty> {
    
	public DifficultyUpdateEvent(Object source, Difficulty diff) {
        super(source, diff);
    }
}
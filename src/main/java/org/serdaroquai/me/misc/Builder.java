package org.serdaroquai.me.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * General purpose Builder pattern for all objects using Java 8
 * 
 * Example use:
 * 
 * Person value = Builder.of(Person::new).with(Person::setName, "blah").with(Person::setAge, 5).build();
 * 
 * @author tr1b6162
 *
 * @param <T>
 */
public class Builder<T> {

	private final Supplier<T> instantiator;
	
	private List<Consumer<T>> instanceModifiers = new ArrayList<>();
	
	private Builder(Supplier<T> instantiator) {
		this.instantiator = instantiator;
	}
	
	public static <T> Builder<T> of (Supplier<T> instantiator) {
		return new Builder<T>(instantiator);
	}
	
	public <U> Builder<T> with(BiConsumer<T, U> consumer, U value) {
        Consumer<T> c = instance -> consumer.accept(instance, value);
        instanceModifiers.add(c);
        return this;
    }

    public T build() {
        T value = instantiator.get();
        instanceModifiers.forEach(modifier -> modifier.accept(value));
        return value;
    }
    
    public Builder<T> clear() {
    	instanceModifiers.clear();
    	return this;
    }
}
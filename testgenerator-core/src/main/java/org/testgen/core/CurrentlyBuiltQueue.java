package org.testgen.core;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Holds all Values that are currently built and register a result listener that
 * is executed after the build process is finished.
 *
 * @param <T> Type of the Value that is built
 */
public class CurrentlyBuiltQueue<T> {
	private final Map<Object, List<Consumer<T>>> register = new HashMap<>();
	private final List<Entry> keyValueRegister = new ArrayList<>();

	/**
	 * Register a value, there be build process will start now.
	 * 
	 * @param buildingValue
	 */
	public void register(Object buildingValue) {
		register.put(buildingValue, new ArrayList<>());
	}

	/**
	 * Returns true if the parameter value is registered, otherwise false is
	 * returned. Exclusion: {@link Proxy}, because Proxies normally have no
	 * implementation of {@link Object#hashCode()} and they have no object hierarchy
	 * that could currently built.
	 * 
	 * @param value
	 * @return
	 */
	public boolean isCurrentlyBuilt(Object value) {
		if (Proxy.isProxyClass(value.getClass()))
			return false;

		return register.containsKey(value);
	}

	/**
	 * Add a listener for currently built value. After the build process for this
	 * value is finished the resultAction is executed.
	 * 
	 * @param value
	 * @param resultAction
	 */
	public void addResultListener(Object value, Consumer<T> resultAction) {
		Objects.requireNonNull(value);
		Objects.requireNonNull(resultAction);

		if (isCurrentlyBuilt(value))
			register.get(value).add(resultAction);

		else
			throw new NoSuchElementException(value + "isn't currently built");
	}

	/**
	 * Add a listener for currently built key value pair. After the build process
	 * for both values is finished the resultAction is executed. The first parameter
	 * of the {@link BiConsumer} is used for the key, the second for the value.
	 * 
	 * @param key
	 * @param value
	 * @param resultAction
	 */
	public void addResultListener(Object key, Object value, BiConsumer<T, T> resultAction) {
		Objects.requireNonNull(key, "key");
		Objects.requireNonNull(value, "value");
		Objects.requireNonNull(resultAction);

		if (!isCurrentlyBuilt(key) || !isCurrentlyBuilt(value))
			throw new IllegalArgumentException("key or value isn't currently built");

		Entry entry = new Entry(key, value, resultAction);

		keyValueRegister.add(entry);
	}

	/**
	 * if the build process for a value its finished and the result exists, this
	 * method executes all Listeners waiting for this value to be built.
	 * 
	 * @param value
	 * @param result
	 */
	public void executeResultListener(Object value, T result) {
		List<Consumer<T>> resultActions = register.get(value);

		if (resultActions != null) {

			for (Consumer<T> resultAction : resultActions)
				resultAction.accept(result);
		}
		register.remove(value);

		for (Entry entry : keyValueRegister) {
			if (entry.key == value)
				entry.keyResult = result;

			if (entry.value == value)
				entry.valueResult = result;
		}

		for (int i = 0; i < keyValueRegister.size(); i++) {
			Entry entry = keyValueRegister.get(i);

			if (entry.isExecutable()) {
				entry.excuteResultAction();

				keyValueRegister.remove(entry);
			}
		}
	}

	private class Entry {
		final Object key;
		final Object value;
		final BiConsumer<T, T> resultAction;

		T keyResult;
		T valueResult;

		Entry(Object key, Object value, BiConsumer<T, T> resultAction) {
			this.key = key;
			this.value = value;
			this.resultAction = resultAction;
		}

		boolean isExecutable() {
			return keyResult != null && valueResult != null;
		}

		void excuteResultAction() {
			resultAction.accept(keyResult, valueResult);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Objects.hash(key, value);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			@SuppressWarnings("unchecked")
			Entry other = (Entry) obj;

			return Objects.equals(key, other.key) && Objects.equals(value, other.value);
		}

	}

}

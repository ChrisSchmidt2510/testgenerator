package org.testgen.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class MapBuilder<K, V> {
	private final Map<K, V> map;

	private MapBuilder(Map<K, V> map) {
		this.map = map;
	}

	public static <K, V> MapBuilder<K, V> hashMapBuilder() {
		return new MapBuilder<>(new HashMap<>());
	}

	public MapBuilder<K, V> add(K key, V value) {
		map.put(key, value);
		return this;
	}

	public Map<K, V> toMap() {
		return map;
	}

	public Map<K, V> toUnmodifiableMap() {
		return Collections.unmodifiableMap(map);
	}

}

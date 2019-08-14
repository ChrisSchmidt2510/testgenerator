package de.nvg.testgenerator;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class CollectionUtils {

	@SafeVarargs
	public static <K, V> Map<K, V> toUnmodifiableMap(SimpleEntry<K, V>... entries) {
		Map<K, V> map = new HashMap<>();

		for (SimpleEntry<K, V> entry : entries) {
			map.put(entry.getKey(), entry.getValue());
		}

		return Collections.unmodifiableMap(map);
	}

	public static <K, V> Map<V, K> swap(Map<K, V> map) {
		Map<V, K> swappedMap = new HashMap<>();

		for (Entry<K, V> entry : map.entrySet()) {
			swappedMap.put(entry.getValue(), entry.getKey());
		}

		return swappedMap;
	}

}

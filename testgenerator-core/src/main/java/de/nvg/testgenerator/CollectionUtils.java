package de.nvg.testgenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class CollectionUtils {

	public static <K, V> Map<V, K> swap(Map<K, V> map) {
		Map<V, K> swappedMap = new HashMap<>();

		for (Entry<K, V> entry : map.entrySet()) {
			swappedMap.put(entry.getValue(), entry.getKey());
		}

		return swappedMap;
	}

}

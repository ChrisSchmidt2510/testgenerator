package de.nvg.valuetracker.blueprint.collections;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import de.nvg.valuetracker.blueprint.BasicCollectionBluePrint;
import de.nvg.valuetracker.blueprint.BluePrint;

public class MapBluePrint extends BasicCollectionBluePrint<Map<?, ?>> {
	private List<BluePrint> keyBluePrints = new ArrayList<>();
	private List<BluePrint> valueBluePrints = new ArrayList<>();

	public MapBluePrint(String name, Map<?, ?> value, Class<?> interfaceClassName) {
		super(name, value, interfaceClassName);
	}

	public void addKeyValuePair(BluePrint key, BluePrint value) {
		Objects.requireNonNull(key);
		Objects.requireNonNull(value);

		keyBluePrints.add(key);
		valueBluePrints.add(value);
	}

	@Override
	public List<BluePrint> getPreExecuteBluePrints() {
		keyBluePrints.addAll(valueBluePrints);

		return keyBluePrints;
	}

	public Set<Entry<BluePrint, BluePrint>> getBluePrints() {
		Set<Entry<BluePrint, BluePrint>> set = new HashSet<>();

		for (int i = 0; i < keyBluePrints.size(); i++) {
			BluePrint key = keyBluePrints.get(i);
			BluePrint value = valueBluePrints.get(i);

			set.add(new SimpleImmutableEntry<>(key, value));
		}

		return Collections.unmodifiableSet(set);
	}

}

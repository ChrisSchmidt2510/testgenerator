package de.nvg.valuetracker.blueprint.collections;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.nvg.valuetracker.blueprint.BasicCollectionBluePrint;
import de.nvg.valuetracker.blueprint.BluePrint;

public class MapBluePrint extends BasicCollectionBluePrint<Map<?, ?>> {
	private List<BluePrint> keyBluePrints = new ArrayList<>();
	private List<BluePrint> valueBluePrints = new ArrayList<>();

	public MapBluePrint(String name, Map<?, ?> value, String interfaceClassName, String factoryMethod) {
		super(name, value, interfaceClassName, factoryMethod);
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

}

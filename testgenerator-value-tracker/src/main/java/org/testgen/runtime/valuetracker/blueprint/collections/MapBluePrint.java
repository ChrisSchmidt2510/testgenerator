package org.testgen.runtime.valuetracker.blueprint.collections;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.testgen.runtime.valuetracker.blueprint.AbstractBasicCollectionBluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrintFactory;

public class MapBluePrint extends AbstractBasicCollectionBluePrint<Map<?, ?>> {
	private static final Predicate<BluePrint> CHECK_COMPLEX_TYPES = BluePrint::isComplexType;

	private List<BluePrint> keyBluePrints = new ArrayList<>();
	private List<BluePrint> valueBluePrints = new ArrayList<>();

	public MapBluePrint(String name, Map<?, ?> value) {
		super(name, value, Map.class);
	}

	public void addKeyValuePair(BluePrint key, BluePrint value) {
		Objects.requireNonNull(key);
		Objects.requireNonNull(value);

		keyBluePrints.add(key);
		valueBluePrints.add(value);
	}

	@Override
	public List<BluePrint> getPreExecuteBluePrints() {
		Predicate<BluePrint> checkComplexTypes = BluePrint::isComplexType;

		List<BluePrint> complexTypes = keyBluePrints.stream().filter(checkComplexTypes).collect(Collectors.toList());

		valueBluePrints.stream().filter(checkComplexTypes).forEach(complexTypes::add);

		return complexTypes;
	}

	@Override
	public void resetBuildState() {
		if (build) {
			build = false;
			keyBluePrints.forEach(BluePrint::resetBuildState);
			valueBluePrints.forEach(BluePrint::resetBuildState);
		}
	}

	public List<BluePrint> getComplexKeys() {
		return keyBluePrints.stream().filter(CHECK_COMPLEX_TYPES).collect(Collectors.toList());
	}

	public List<BluePrint> getComplexValues() {
		return valueBluePrints.stream().filter(CHECK_COMPLEX_TYPES).collect(Collectors.toList());
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

	public static class MapBluePrintFactory implements BluePrintFactory {

		@Override
		public boolean createBluePrintForType(Object value) {
			return value instanceof Map<?, ?>;
		}

		@Override
		public BluePrint createBluePrint(String name, Object value,
				BiFunction<String, Object, BluePrint> childCallBack) {
			Map<?, ?> map = (Map<?, ?>) value;

			MapBluePrint mapBluePrint = new MapBluePrint(name, map);

			for (Entry<?, ?> entry : map.entrySet()) {
				BluePrint keyBluePrint = childCallBack.apply(name + "Key", entry.getKey());
				BluePrint valueBluePrint = childCallBack.apply(name + "Value", entry.getValue());

				mapBluePrint.addKeyValuePair(keyBluePrint, valueBluePrint);
			}

			return mapBluePrint;
		}

	}

}

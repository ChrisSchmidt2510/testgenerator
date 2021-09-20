package org.testgen.runtime.valuetracker.blueprint.collections;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;

import org.junit.Assert;
import org.junit.Test;
import org.testgen.runtime.valuetracker.CurrentlyBuildedBluePrints;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.collections.MapBluePrint.MapBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.NumberBluePrint.NumberBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.StringBluePrint.StringBluePrintFactory;

public class MapBluePrinttest {

	private MapBluePrintFactory factory = new MapBluePrintFactory();

	private StringBluePrintFactory strFactory = new StringBluePrintFactory();

	private NumberBluePrintFactory numFactory = new NumberBluePrintFactory();

	private CurrentlyBuildedBluePrints currentlyBuildedBluePrints = new CurrentlyBuildedBluePrints();

	@Test
	public void testBluePrintFactory() {
		Assert.assertTrue(factory.createBluePrintForType(new HashMap<>()));
		Assert.assertFalse(factory.createBluePrintForType(5));
		Assert.assertFalse(factory.createBluePrintForType(null));
		Assert.assertFalse(factory.createsSimpleBluePrint());
		Assert.assertEquals(1, factory.getPriority());
	}

	@Test
	public void testTrackMap() {
		Map<Integer, String> map = new LinkedHashMap<>();
		map.put(1, "Powerpoint");
		map.put(2, "Word");
		map.put(3, "Outlook");
		map.put(4, "Exel");

		BiFunction<String, Object, BluePrint> mapper = (name, value) -> numFactory.createBluePrintForType(value)
				? numFactory.createBluePrint(name, (Number) value)
				: strFactory.createBluePrint(name, (String) value);

		BluePrint bluePrint = factory.createBluePrint("dictionary", map, currentlyBuildedBluePrints, mapper);

		Assert.assertTrue(bluePrint.isCollectionBluePrint());

		MapBluePrint mapBP = (MapBluePrint) bluePrint;

		Assert.assertEquals("dictionary", mapBP.getName());
		Assert.assertEquals(Map.class, mapBP.getInterfaceClass());
		Assert.assertEquals(LinkedHashMap.class, mapBP.getImplementationClass());
		Assert.assertTrue(mapBP.getPreExecuteBluePrints().isEmpty());
		Assert.assertTrue(mapBP.getComplexKeys().isEmpty());
		Assert.assertTrue(mapBP.getComplexValues().isEmpty());

		Set<Entry<BluePrint, BluePrint>> compareSet = new LinkedHashSet<>();

		compareSet.add(new SimpleImmutableEntry<>(numFactory.createBluePrint("dictionaryKey", 1),
				strFactory.createBluePrint("dictionaryValue", "Powerpoint")));
		compareSet.add(new SimpleImmutableEntry<>(numFactory.createBluePrint("dictionaryKey", 2),
				strFactory.createBluePrint("dictionaryValue", "Word")));
		compareSet.add(new SimpleImmutableEntry<>(numFactory.createBluePrint("dictionaryKey", 3),
				strFactory.createBluePrint("dictionaryValue", "Outlook")));
		compareSet.add(new SimpleImmutableEntry<>(numFactory.createBluePrint("dictionaryKey", 4),
				strFactory.createBluePrint("dictionaryValue", "Exel")));

		Assert.assertEquals(compareSet, mapBP.getBluePrints());
	}
}

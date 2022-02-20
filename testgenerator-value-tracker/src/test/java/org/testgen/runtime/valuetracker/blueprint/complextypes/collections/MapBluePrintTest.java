package org.testgen.runtime.valuetracker.blueprint.complextypes.collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;

import org.junit.jupiter.api.Test;
import org.testgen.core.CurrentlyBuiltQueue;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.complextypes.collections.MapBluePrint.MapBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.NumberBluePrint.NumberBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.StringBluePrint.StringBluePrintFactory;

public class MapBluePrintTest {

	private MapBluePrintFactory factory = new MapBluePrintFactory();

	private StringBluePrintFactory strFactory = new StringBluePrintFactory();

	private NumberBluePrintFactory numFactory = new NumberBluePrintFactory();

	private CurrentlyBuiltQueue<BluePrint> currentlyBuiltQueue = new CurrentlyBuiltQueue<>();

	@Test
	public void testBluePrintFactory() {
		assertTrue(factory.createBluePrintForType(new HashMap<>()));
		assertFalse(factory.createBluePrintForType(5));
		assertFalse(factory.createBluePrintForType(null));
		assertFalse(factory.createsSimpleBluePrint());
		assertEquals(1, factory.getPriority());
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

		BluePrint bluePrint = factory.createBluePrint("dictionary", map, currentlyBuiltQueue, mapper);

		assertTrue(bluePrint.isCollectionBluePrint());

		MapBluePrint mapBP = (MapBluePrint) bluePrint;

		assertEquals("dictionary", mapBP.getName());
		assertEquals(Map.class, mapBP.getInterfaceClass());
		assertEquals(LinkedHashMap.class, mapBP.getImplementationClass());
		assertTrue(mapBP.getPreExecuteBluePrints().isEmpty());
		assertTrue(mapBP.getComplexKeys().isEmpty());
		assertTrue(mapBP.getComplexValues().isEmpty());

		Set<Entry<BluePrint, BluePrint>> compareSet = new LinkedHashSet<>();

		compareSet.add(new SimpleImmutableEntry<>(numFactory.createBluePrint("dictionaryKey", 1),
				strFactory.createBluePrint("dictionaryValue", "Powerpoint")));
		compareSet.add(new SimpleImmutableEntry<>(numFactory.createBluePrint("dictionaryKey", 2),
				strFactory.createBluePrint("dictionaryValue", "Word")));
		compareSet.add(new SimpleImmutableEntry<>(numFactory.createBluePrint("dictionaryKey", 3),
				strFactory.createBluePrint("dictionaryValue", "Outlook")));
		compareSet.add(new SimpleImmutableEntry<>(numFactory.createBluePrint("dictionaryKey", 4),
				strFactory.createBluePrint("dictionaryValue", "Exel")));

		assertEquals(compareSet, mapBP.getBluePrints());
	}
}

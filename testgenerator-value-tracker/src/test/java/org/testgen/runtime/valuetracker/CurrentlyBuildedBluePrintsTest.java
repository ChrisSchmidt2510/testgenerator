package org.testgen.runtime.valuetracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.LocalDateTimeBluePrint.LocalDateTimeBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.NumberBluePrint.NumberBluePrintFactory;

public class CurrentlyBuildedBluePrintsTest {

	private CurrentlyBuildedBluePrints currentlyBuildedBluePrints;

	private LocalDateTimeBluePrintFactory dateTimeFactory = new LocalDateTimeBluePrintFactory();

	private NumberBluePrintFactory numFactory = new NumberBluePrintFactory();
	
	@BeforeEach
	public void init() {
		currentlyBuildedBluePrints = new CurrentlyBuildedBluePrints();
	}

	@Test
	public void testIsCurrentlyBuilded() {
		LocalDateTime value = LocalDateTime.now();

		currentlyBuildedBluePrints.register(value);
		assertTrue(currentlyBuildedBluePrints.isCurrentlyBuilded(value));
	}

	@Test
	public void testExecuteActions() {
		LocalDateTime value = LocalDateTime.now();

		currentlyBuildedBluePrints.register(value);

		List<BluePrint> result = new ArrayList<>();

		currentlyBuildedBluePrints.addFinishedListener(value, result::add);

		SimpleBluePrint<LocalDateTime> bluePrint = dateTimeFactory.createBluePrint("value", value);

		currentlyBuildedBluePrints.executeActions(value, bluePrint);

		assertFalse(currentlyBuildedBluePrints.isCurrentlyBuilded(value));
		assertTrue(result.size() == 1);
		assertTrue(result.get(0) == bluePrint);
	}

	@Test
	public void testExecuteActionsWithMapEntry() {
		LocalDateTime value = LocalDateTime.now();
		Integer key = 25;

		currentlyBuildedBluePrints.register(key);
		currentlyBuildedBluePrints.register(value);

		List<Entry<BluePrint, BluePrint>> result = new ArrayList<>();

		currentlyBuildedBluePrints.addFinishedListener(key, value,
				(entryKey, entryValue) -> result.add(new SimpleEntry<>(entryKey, entryValue)));
		
		SimpleBluePrint<LocalDateTime> bpValue = dateTimeFactory.createBluePrint("value", value);
		
		currentlyBuildedBluePrints.executeActions(value, bpValue);
		
		assertFalse(currentlyBuildedBluePrints.isCurrentlyBuilded(value));
		assertTrue(currentlyBuildedBluePrints.isCurrentlyBuilded(key));
		assertTrue(result.isEmpty());
		
		SimpleBluePrint<Number> bpKey = numFactory.createBluePrint("key", key);
		
		currentlyBuildedBluePrints.executeActions(key, bpKey);
		
		assertFalse(currentlyBuildedBluePrints.isCurrentlyBuilded(key));
		assertEquals(1, result.size());
		assertTrue(result.get(0).getKey() == bpKey);
		assertTrue(result.get(0).getValue() == bpValue);
	}

}

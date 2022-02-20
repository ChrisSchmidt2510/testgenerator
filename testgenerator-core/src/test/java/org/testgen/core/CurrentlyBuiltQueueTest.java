package org.testgen.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CurrentlyBuiltQueueTest {

	private CurrentlyBuiltQueue<String> currentlyBuildedBluePrints;

	@BeforeEach
	public void init() {
		currentlyBuildedBluePrints = new CurrentlyBuiltQueue<>();
	}

	@Test
	public void testIsCurrentlyBuilt() {
		LocalDateTime value = LocalDateTime.now();

		currentlyBuildedBluePrints.register(value);
		assertTrue(currentlyBuildedBluePrints.isCurrentlyBuilt(value));
	}

	@Test
	public void testAddResultListener() {
		LocalDateTime value = LocalDateTime.now();

		currentlyBuildedBluePrints.register(value);

		String result = "Test";

		List<String> resultList = new ArrayList<>();

		currentlyBuildedBluePrints.addResultListener(value, resultList::add);

		currentlyBuildedBluePrints.executeResultListener(value, result);

		assertFalse(currentlyBuildedBluePrints.isCurrentlyBuilt(value));
		assertEquals(Arrays.asList(result), resultList);
	}

	@Test
	public void testAddResultListenerWithKeyValuePair() {
		LocalDateTime value = LocalDateTime.now();
		Integer key = 25;

		currentlyBuildedBluePrints.register(key);
		currentlyBuildedBluePrints.register(value);

		List<Entry<String, String>> result = new ArrayList<>();

		currentlyBuildedBluePrints.addResultListener(key, value,
				(entryKey, entryValue) -> result.add(new SimpleEntry<>(entryKey, entryValue)));

		currentlyBuildedBluePrints.executeResultListener(value, "value");

		assertFalse(currentlyBuildedBluePrints.isCurrentlyBuilt(value));
		assertTrue(currentlyBuildedBluePrints.isCurrentlyBuilt(key));
		assertTrue(result.isEmpty());

		currentlyBuildedBluePrints.executeResultListener(key, "25");

		assertFalse(currentlyBuildedBluePrints.isCurrentlyBuilt(key));
		assertEquals(1, result.size());
		assertTrue(result.get(0).getKey() == "25");
		assertTrue(result.get(0).getValue() == "value");
	}
}

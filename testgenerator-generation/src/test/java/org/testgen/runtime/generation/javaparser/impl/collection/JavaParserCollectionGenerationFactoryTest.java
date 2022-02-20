package org.testgen.runtime.generation.javaparser.impl.collection;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.jupiter.api.Test;
import org.testgen.core.CurrentlyBuiltQueue;
import org.testgen.runtime.valuetracker.blueprint.BasicCollectionBluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.complextypes.collections.CollectionBluePrint.CollectionBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.complextypes.collections.MapBluePrint.MapBluePrintFactory;

public class JavaParserCollectionGenerationFactoryTest {

	private JavaParserCollectionGenerationFactory collectionFactory = new JavaParserCollectionGenerationFactory();

	private CurrentlyBuiltQueue<BluePrint> currentlyBuiltQueue = new CurrentlyBuiltQueue<>();

	@Test
	public void testCollectionGenerationCollectionsGeneration() {
		CollectionBluePrintFactory factory = new CollectionBluePrintFactory();

		BasicCollectionBluePrint<?> bluePrint = factory
				.createBluePrint("value", new ArrayList<String>(), currentlyBuiltQueue, null)
				.castToCollectionBluePrint();

		assertTrue(collectionFactory.of(bluePrint) instanceof CollectionsGeneration);
	}

	@Test
	public void testCollectionGenerationMapGeneration() {
		MapBluePrintFactory factory = new MapBluePrintFactory();
		BasicCollectionBluePrint<?> bluePrint = factory
				.createBluePrint("value", new HashMap<>(), currentlyBuiltQueue, null).castToCollectionBluePrint();

		assertTrue(collectionFactory.of(bluePrint) instanceof MapGeneration);
	}
}

package org.testgen.runtime.generation.javaparser.impl.collection;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;
import org.testgen.runtime.valuetracker.CurrentlyBuildedBluePrints;
import org.testgen.runtime.valuetracker.blueprint.BasicCollectionBluePrint;
import org.testgen.runtime.valuetracker.blueprint.complextypes.collections.CollectionBluePrint.CollectionBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.complextypes.collections.MapBluePrint.MapBluePrintFactory;

public class JavaParserCollectionGenerationFactoryTest {

	private JavaParserCollectionGenerationFactory collectionFactory = new JavaParserCollectionGenerationFactory();

	private CurrentlyBuildedBluePrints currentlyBuildedBluePrints = new CurrentlyBuildedBluePrints();
	
	@Test
	public void testCollectionGenerationCollectionsGeneration() {
		CollectionBluePrintFactory factory = new CollectionBluePrintFactory();

		BasicCollectionBluePrint<?> bluePrint = factory.createBluePrint("value", new ArrayList<String>(),currentlyBuildedBluePrints, null)
				.castToCollectionBluePrint();

		Assert.assertTrue(collectionFactory.of(bluePrint) instanceof CollectionsGeneration);
	}

	@Test
	public void testCollectionGenerationMapGeneration() {
		MapBluePrintFactory factory = new MapBluePrintFactory();
		BasicCollectionBluePrint<?> bluePrint = factory.createBluePrint("value", new HashMap<>(),currentlyBuildedBluePrints, null)
				.castToCollectionBluePrint();

		Assert.assertTrue(collectionFactory.of(bluePrint) instanceof MapGeneration);
	}
}

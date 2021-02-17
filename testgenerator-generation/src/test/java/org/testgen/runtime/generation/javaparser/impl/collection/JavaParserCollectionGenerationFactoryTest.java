package org.testgen.runtime.generation.javaparser.impl.collection;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;
import org.testgen.runtime.valuetracker.blueprint.AbstractBasicCollectionBluePrint;
import org.testgen.runtime.valuetracker.blueprint.collections.CollectionBluePrint.CollectionBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.collections.MapBluePrint.MapBluePrintFactory;

public class JavaParserCollectionGenerationFactoryTest {

	private JavaParserCollectionGenerationFactory collectionFactory = new JavaParserCollectionGenerationFactory();

	@Test
	public void testCollectionGenerationCollectionsGeneration() {
		CollectionBluePrintFactory factory = new CollectionBluePrintFactory();

		AbstractBasicCollectionBluePrint<?> bluePrint = factory.createBluePrint("value", new ArrayList<String>(), null)
				.castToCollectionBluePrint();

		Assert.assertTrue(collectionFactory.of(bluePrint) instanceof CollectionsGeneration);
	}

	@Test
	public void testCollectionGenerationMapGeneration() {
		MapBluePrintFactory factory = new MapBluePrintFactory();
		AbstractBasicCollectionBluePrint<?> bluePrint = factory.createBluePrint("value", new HashMap<>(), null)
				.castToCollectionBluePrint();

		Assert.assertTrue(collectionFactory.of(bluePrint) instanceof MapGeneration);
	}
}

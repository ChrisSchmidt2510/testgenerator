package org.testgen.runtime.generation.api.collections;

import org.testgen.runtime.valuetracker.blueprint.AbstractBasicCollectionBluePrint;

public interface CollectionGenerationFactory<T, E, S> {

	public CollectionGeneration<T, E, S> of(AbstractBasicCollectionBluePrint<?> bluePrint);
}

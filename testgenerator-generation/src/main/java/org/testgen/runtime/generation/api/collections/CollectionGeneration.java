package org.testgen.runtime.generation.api.collections;

import org.testgen.runtime.generation.api.FieldGeneration;
import org.testgen.runtime.valuetracker.blueprint.AbstractBasicCollectionBluePrint;

public interface CollectionGeneration<T, E, S>
		extends FieldGeneration<T, AbstractBasicCollectionBluePrint<?>>, CollectionGenerationFactory<T, E, S> {

	boolean canGenerateBluePrint(AbstractBasicCollectionBluePrint<?> bluePrint);

	void setCollectionGenerationFactory(CollectionGenerationFactory<T, E, S> collectionGenerationFactory);

	default int getPriority() {
		return 0;
	}

}

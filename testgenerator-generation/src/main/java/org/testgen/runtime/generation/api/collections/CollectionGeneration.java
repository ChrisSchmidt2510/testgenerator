package org.testgen.runtime.generation.api.collections;

import org.testgen.runtime.generation.api.FieldGeneration;
import org.testgen.runtime.valuetracker.blueprint.AbstractBasicCollectionBluePrint;

/**
 * /** This Interface creates for one or a group of
 * {@link AbstractBasicCollectionBluePrint} statements for an abstract syntax
 * tree.
 *
 * @param <T> Type of ClassDeclaration
 * @param <E> Type of a CodeBlock
 * @param <S> Type of a single Expression
 */
public interface CollectionGeneration<T, E, S>
		extends FieldGeneration<T, AbstractBasicCollectionBluePrint<?>>, CollectionGenerationFactory<T, E, S> {

	/**
	 * Checks if this implementation supports this
	 * {@link AbstractBasicCollectionBluePrint}.
	 * 
	 * @param bluePrint
	 * @return
	 */
	boolean canGenerateBluePrint(AbstractBasicCollectionBluePrint<?> bluePrint);

	void setCollectionGenerationFactory(CollectionGenerationFactory<T, E, S> collectionGenerationFactory);

	/**
	 * If you want to override the standard implementation of this
	 * {@link AbstractBasicCollectionBluePrint} without implementing a custom
	 * version of {@link CollectionGenerationFactory} can you set the priority
	 * higher than 0. Now your implementations is chosen to generate this
	 * {@link AbstractBasicCollectionBluePrint}.
	 * 
	 * @return
	 */
	default int getPriority() {
		return 0;
	}

}

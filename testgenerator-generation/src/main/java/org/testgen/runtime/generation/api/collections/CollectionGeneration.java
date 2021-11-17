package org.testgen.runtime.generation.api.collections;

import org.testgen.runtime.valuetracker.blueprint.BasicCollectionBluePrint;

/**
 * /** This Interface creates for one or a group of
 * {@link BasicCollectionBluePrint} statements for an abstract syntax
 * tree.
 *
 * @param <T> Type of ClassDeclaration
 * @param <E> Type of a CodeBlock
 * @param <S> Type of a single Expression
 */
public interface CollectionGeneration<T, E, S>
		extends  CollectionGenerationFactory<T, E, S> {

	/**
	 * Checks if this implementation supports this
	 * {@link BasicCollectionBluePrint}.
	 * 
	 * @param bluePrint
	 * @return
	 */
	boolean canGenerateBluePrint(BasicCollectionBluePrint<?> bluePrint);

	void setCollectionGenerationFactory(CollectionGenerationFactory<T, E, S> collectionGenerationFactory);

	/**
	 * If you want to override the standard implementation of this
	 * {@link BasicCollectionBluePrint} without implementing a custom
	 * version of {@link CollectionGenerationFactory} can you set the priority
	 * higher than 0. Now your implementations is chosen to generate this
	 * {@link BasicCollectionBluePrint}.
	 * 
	 * @return
	 */
	default int getPriority() {
		return 0;
	}

}

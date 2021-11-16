package org.testgen.runtime.generation.api.spezial;

import java.lang.reflect.Proxy;

import org.testgen.runtime.valuetracker.blueprint.BluePrint;

/**
 * This Interface creats for one or a group of spezial Objects like
 * {@link Proxy} or a Lambda-Expression for an abstract syntax tree.
 *
 * @param <T> Type of ClassDeclaration
 * @param <E> Type of a CodeBlock
 * @param <S> Type of a single Expression
 * @param <B> Type of the {@link BluePrint} implementation
 */
public interface SpezialObjectGeneration<T, E, S, B extends BluePrint>
		extends SpezialObjectGenerationFactory<T, E, S, B> {
	/**
	 * Checks if this implementation supports this {@link BluePrint}
	 * @param bluePrint
	 * @return
	 */
	boolean canGenerateBluePrint(BluePrint bluePrint);

	void setSpezialObjectGenerationFactory(SpezialObjectGenerationFactory<T, E, S, BluePrint> spezialGenerationFactory);

	/**
	 * If you want to override the standard implementation of this
	 * {@link BluePrint} without implementing a custom
	 * version of {@link SpezialObjectGenerationFactory} can you set the priority
	 * higher than 0. Now your implementations is chosen to generate this
	 * {@link BluePrint}.
	 * 
	 * @return
	 */
	default int getPriority() {
		return 0;
	}
}

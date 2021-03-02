package org.testgen.runtime.generation.api.simple;

import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;

/**
 * This Interface creates for one or a group of {@link SimpleBluePrint}
 * statements for an abstract syntax tree.
 *
 * @param <T> Type of ClassDeclaration
 * @param <E> Type of a CodeBlock
 * @param <S> Type of a single Expression
 */
public interface SimpleObjectGeneration<T, E, S> extends SimpleObjectGenerationFactory<T, E, S> {

	/**
	 * Checks if this implementation supports this {@link SimpleBluePrint}.
	 * 
	 * @param bluePrint
	 * @return
	 */
	public boolean canGenerateBluePrint(SimpleBluePrint<?> bluePrint);

	/**
	 * If you want to override the standard implementation of this
	 * {@link SimpleBluePrint} without implementing a custom version of
	 * {@link SimpleObjectGenerationFactory} can you set the priority higher than 0.
	 * Now your implementations is chosen to generate this {@link SimpleBluePrint}.
	 * 
	 * @return
	 */
	default int getPriority() {
		return 0;
	}

}

package org.testgen.runtime.generation.api.simple;

import java.util.function.Consumer;

import org.testgen.runtime.generation.api.naming.NamingService;
import org.testgen.runtime.generation.api.naming.NamingServiceProvider;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;

/**
 * Base interface that is used for generating simple Types e.g. {@link String}
 * from {@link SimpleBluePrint}. If you want to implement only one or a group of
 * Types use {@link SimpleObjectGeneration}.
 * 
 * @implNote The standard implementation of the
 *           {@link SimpleObjectGenerationFactory} includes all implementations
 *           of the {@link SimpleObjectGeneration} using the JavaSPI technology.
 *
 * @param <T> Type of ClassDeclaration
 * @param <E> Type of a CodeBlock
 * @param <S> Type of a single Expression
 */
public interface SimpleObjectGenerationFactory<T, E, S> {

	/**
	 * Adds a field from a {@link SimpleBluePrint} to a compilationUnit.
	 * 
	 * @param compilationUnit class where the field is added to
	 * @param bluePrint       for the field
	 * @param withInitalizer  flag for initializing the field
	 */
	public void createField(T compilationUnit, SimpleBluePrint<?> bluePrint, boolean withInitalizer);

	/**
	 * creates a local Variable or initialize a Field in the statementTree for a
	 * {@link SimpleBluePrint} e.g. String value ="value";
	 * 
	 * @param statementTree codeBlock where the generated is added to
	 * @param bluePrint     data for the local variable
	 * @param isField       flag if the {@link SimpleBluePrint} is a Field of the
	 *                      compilationUnit
	 */
	public void createObject(E statementTree, SimpleBluePrint<?> bluePrint, boolean isField);

	/**
	 * creates only the right side Argument of a local Variable e.g. "value" from a
	 * {@link SimpleBluePrint}, so it can directly used for setter.
	 * 
	 * @param bluePrint
	 * @return
	 */
	public S createInlineExpression(SimpleBluePrint<?> bluePrint);

	public void setImportCallBackHandler(Consumer<Class<?>> importCallBackHandler);

	default NamingService<E> getNamingService() {
		return NamingServiceProvider.getNamingService();
	}

}

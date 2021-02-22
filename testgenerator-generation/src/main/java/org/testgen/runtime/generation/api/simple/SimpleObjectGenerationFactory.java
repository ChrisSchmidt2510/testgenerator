package org.testgen.runtime.generation.api.simple;

import java.util.function.Consumer;

import org.testgen.runtime.generation.api.naming.NamingService;
import org.testgen.runtime.generation.api.naming.NamingServiceProvider;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;

public interface SimpleObjectGenerationFactory<T, E, S> {

	/**
	 * Adds a field for {@link SimpleBluePrint} to a compilationUnit.
	 * 
	 * @param compilationUnit class where the field is added to
	 * @param bluePrint       for the field
	 * @param withInitalizer  initialize the field
	 */
	public void createField(T compilationUnit, SimpleBluePrint<?> bluePrint, boolean withInitalizer);

	/**
	 * 
	 * @param statementTree codeBlock where the generated is added to
	 * @param bluePrint
	 * @param isField
	 */
	public void createObject(E statementTree, SimpleBluePrint<?> bluePrint, boolean isField);

	public S createInlineExpression(SimpleBluePrint<?> bluePrint);

	public void setImportCallBackHandler(Consumer<Class<?>> importCallBackHandler);

	default NamingService<E> getNamingService() {
		return NamingServiceProvider.getNamingService();
	}

}

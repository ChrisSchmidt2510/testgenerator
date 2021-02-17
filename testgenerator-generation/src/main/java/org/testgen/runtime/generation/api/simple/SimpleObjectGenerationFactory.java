package org.testgen.runtime.generation.api.simple;

import java.util.function.Consumer;

import org.testgen.runtime.generation.api.naming.NamingService;
import org.testgen.runtime.generation.api.naming.NamingServiceProvider;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;

public interface SimpleObjectGenerationFactory<T, E, S> {

	public void createField(T compilationUnit, SimpleBluePrint<?> bluePrint, boolean withInitalizer);

	public void createObject(E statementTree, SimpleBluePrint<?> bluePrint, boolean isField);

	public S createInlineExpression(SimpleBluePrint<?> bluePrint);

	public void setImportCallBackHandler(Consumer<Class<?>> importCallBackHandler);

	default NamingService<E> getNamingService() {
		return NamingServiceProvider.getNamingService();
	}

}

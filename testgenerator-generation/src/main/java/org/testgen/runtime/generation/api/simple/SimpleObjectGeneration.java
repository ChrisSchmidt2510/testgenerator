package org.testgen.runtime.generation.api.simple;

import java.util.function.Consumer;

import org.testgen.runtime.generation.api.GenerationFactory;
import org.testgen.runtime.generation.api.naming.NamingService;
import org.testgen.runtime.generation.api.naming.NamingServiceProvider;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;

public interface SimpleObjectGeneration<T, E, S> {

	public boolean canGenerateBluePrint(SimpleBluePrint<?> bluePrint);

	public void createField(T compilationUnit, SimpleBluePrint<?> bluePrint, boolean withInitalizer);

	public void createObject(E statementTree, SimpleBluePrint<?> bluePrint, boolean isField);

	public S createInlineObject(SimpleBluePrint<?> bluePrint);

	default Consumer<Class<?>> getImportCallBackHandler() {
		return GenerationFactory.<T, E, S>getInstance().getImportCallBackHandler();
	}

	default NamingService<E> getNamingService() {
		return NamingServiceProvider.getNamingService();
	}

}

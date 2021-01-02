package org.testgen.runtime.generation.api;

import java.util.function.Consumer;

import org.testgen.runtime.classdata.model.FieldData;
import org.testgen.runtime.classdata.model.SetterMethodData;
import org.testgen.runtime.generation.api.collections.CollectionGenerationFactory;
import org.testgen.runtime.generation.api.naming.NamingService;
import org.testgen.runtime.generation.api.naming.NamingServiceProvider;
import org.testgen.runtime.generation.api.simple.SimpleObjectGenerationFactory;
import org.testgen.runtime.valuetracker.blueprint.ArrayBluePrint;

public interface ArrayGeneration<T, E, S> extends FieldGeneration<T, ArrayBluePrint> {

	void createArray(E statementTree, ArrayBluePrint arrayBluePrint, boolean onlyCreateElements, //
			boolean isField);

	void addContainerToObject(E statementTree, ArrayBluePrint arrayBP, SetterMethodData setter, //
			String objectName);

	void addContainerToObject(E statementTree, ArrayBluePrint arrayBP, FieldData field, String objectName);

	default SimpleObjectGenerationFactory<T, E, S> getSimpleObjectGenerationFactory() {
		return GenerationFactory.<T, E, S>getInstance().getSimpleObjectGenerationFactory();
	}

	default ComplexObjectGeneration<T, E, S> getComplexObjectGeneration() {
		return GenerationFactory.<T, E, S>getInstance().getComplexObjectGeneration();
	}

	default CollectionGenerationFactory<T, E, S> getCollectionGenerationFactory() {
		return GenerationFactory.<T, E, S>getInstance().getCollectionGenerationFactory();
	}

	default Consumer<Class<?>> getImportCallBackHandler() {
		return GenerationFactory.<T, E, S>getInstance().getImportCallBackHandler();
	}

	default NamingService<E> getNamingService() {
		return NamingServiceProvider.getNamingService();
	}
}

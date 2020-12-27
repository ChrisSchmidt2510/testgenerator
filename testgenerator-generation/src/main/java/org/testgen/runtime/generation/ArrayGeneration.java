package org.testgen.runtime.generation;

import org.testgen.runtime.classdata.model.FieldData;
import org.testgen.runtime.classdata.model.SetterMethodData;
import org.testgen.runtime.generation.naming.NamingService;
import org.testgen.runtime.generation.naming.NamingServiceProvider;
import org.testgen.runtime.valuetracker.blueprint.ArrayBluePrint;

public interface ArrayGeneration<T, E> extends FieldGeneration<T, ArrayBluePrint> {

	void createArray(E statementTree, ArrayBluePrint arrayBluePrint, boolean onlyCreateElements, //
			boolean isField);

	void addContainerToObject(E statementTree, ArrayBluePrint arrayBP, SetterMethodData setter, //
			String objectName);

	void addContainerToObject(E statementTree, ArrayBluePrint arrayBP, FieldData field, String objectName);

	default SimpleObjectGeneration<T, E> getSimpleObjectGeneration() {
		return GenerationFactory.<T, E>getInstance().getSimpleObjectGeneration();
	}

	default ComplexObjectGeneration<T, E> getComplexObjectGeneration() {
		return GenerationFactory.<T, E>getInstance().getComplexObjectGeneration();
	}

	default CollectionGeneration<T, E> getCollectionGeneration() {
		return GenerationFactory.<T, E>getInstance().getContainerGeneration();
	}

	default NamingService getNamingService() {
		return NamingServiceProvider.getNamingService();
	}
}

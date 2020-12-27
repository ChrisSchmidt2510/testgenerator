package org.testgen.runtime.generation;

import org.testgen.runtime.classdata.model.FieldData;
import org.testgen.runtime.classdata.model.SetterMethodData;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;
import org.testgen.runtime.generation.naming.NamingService;
import org.testgen.runtime.generation.naming.NamingServiceProvider;
import org.testgen.runtime.valuetracker.blueprint.AbstractBasicCollectionBluePrint;

public interface CollectionGeneration<T, E> extends FieldGeneration<T, AbstractBasicCollectionBluePrint<?>> {
	void createCollection(E statementTree, AbstractBasicCollectionBluePrint<?> basicCollectionBP, //
			SignatureType signature, boolean onlyCreateCollectionElements, boolean isField);

	void addContainerToObject(E statementTree, AbstractBasicCollectionBluePrint<?> collectionBP,
			SetterMethodData setter, //
			String objectName);

	void addContainerToObject(E statementTree, AbstractBasicCollectionBluePrint<?> collectionBP, FieldData field,
			String objectName);

	default SimpleObjectGeneration<T, E> getSimpleObjectGeneration() {
		return GenerationFactory.<T, E>getInstance().getSimpleObjectGeneration();
	}

	default ComplexObjectGeneration<T, E> getComplexObjectGeneration() {
		return GenerationFactory.<T, E>getInstance().getComplexObjectGeneration();
	}

	default ArrayGeneration<T, E> getArrayGeneration() {
		return GenerationFactory.<T, E>getInstance().getArrayGeneration();
	}

	default NamingService getNamingService() {
		return NamingServiceProvider.getNamingService();
	}

}

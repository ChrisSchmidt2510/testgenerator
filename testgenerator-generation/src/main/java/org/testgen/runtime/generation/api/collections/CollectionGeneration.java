package org.testgen.runtime.generation.api.collections;

import java.util.function.Consumer;

import org.testgen.runtime.classdata.model.FieldData;
import org.testgen.runtime.classdata.model.SetterMethodData;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;
import org.testgen.runtime.generation.api.ArrayGeneration;
import org.testgen.runtime.generation.api.ComplexObjectGeneration;
import org.testgen.runtime.generation.api.FieldGeneration;
import org.testgen.runtime.generation.api.GenerationFactory;
import org.testgen.runtime.generation.api.naming.NamingService;
import org.testgen.runtime.generation.api.naming.NamingServiceProvider;
import org.testgen.runtime.generation.api.simple.SimpleObjectGenerationFactory;
import org.testgen.runtime.valuetracker.blueprint.AbstractBasicCollectionBluePrint;

public interface CollectionGeneration<T, E, S> extends FieldGeneration<T, AbstractBasicCollectionBluePrint<?>> {

	boolean canGenerateBluePrint(AbstractBasicCollectionBluePrint<?> bluePrint);

	void createCollection(E statementTree, AbstractBasicCollectionBluePrint<?> basicCollectionBP, //
			SignatureType signature, boolean onlyCreateCollectionElements, boolean isField);

	void addCollectionToObject(E statementTree, AbstractBasicCollectionBluePrint<?> collectionBP,
			SetterMethodData setter, //
			String objectName);

	void addCollectionToObject(E statementTree, AbstractBasicCollectionBluePrint<?> collectionBP, FieldData field,
			String objectName);

	default SimpleObjectGenerationFactory<T, E, S> getSimpleObjectGenerationFactory() {
		return GenerationFactory.<T, E, S>getInstance().getSimpleObjectGenerationFactory();
	}

	default ComplexObjectGeneration<T, E, S> getComplexObjectGeneration() {
		return GenerationFactory.<T, E, S>getInstance().getComplexObjectGeneration();
	}

	default CollectionGenerationFactory<T, E, S> getCollectionGenerationFactory() {
		return GenerationFactory.<T, E, S>getInstance().getCollectionGenerationFactory();
	}

	default ArrayGeneration<T, E, S> getArrayGeneration() {
		return GenerationFactory.<T, E, S>getInstance().getArrayGeneration();
	}

	default Consumer<Class<?>> getImportCallBackHandler() {
		return GenerationFactory.<T, E, S>getInstance().getImportCallBackHandler();
	}

	default NamingService<E> getNamingService() {
		return NamingServiceProvider.getNamingService();
	}

}

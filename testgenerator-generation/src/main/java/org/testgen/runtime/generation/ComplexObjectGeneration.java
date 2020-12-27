package org.testgen.runtime.generation;

import java.util.Set;

import org.testgen.runtime.classdata.model.ClassData;
import org.testgen.runtime.classdata.model.FieldData;
import org.testgen.runtime.generation.naming.NamingService;
import org.testgen.runtime.generation.naming.NamingServiceProvider;
import org.testgen.runtime.valuetracker.blueprint.ComplexBluePrint;

public interface ComplexObjectGeneration<T, E> extends FieldGeneration<T, ComplexBluePrint> {

	void createObject(E statementTree, ComplexBluePrint bluePrint, boolean isField, ClassData classData,
			Set<FieldData> calledFields);

	default SimpleObjectGeneration<T, E> getSimpleObjectGeneration() {
		return GenerationFactory.<T, E>getInstance().getSimpleObjectGeneration();
	}

	default CollectionGeneration<T, E> getCollectionGeneration() {
		return GenerationFactory.<T, E>getInstance().getContainerGeneration();
	}

	default ArrayGeneration<T, E> getArrayGeneration() {
		return GenerationFactory.<T, E>getInstance().getArrayGeneration();
	}

	default NamingService getNamingService() {
		return NamingServiceProvider.getNamingService();
	}

}

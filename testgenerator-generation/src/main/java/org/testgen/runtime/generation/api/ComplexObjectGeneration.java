package org.testgen.runtime.generation.api;

import java.util.Set;
import java.util.function.Consumer;

import org.testgen.runtime.classdata.model.ClassData;
import org.testgen.runtime.classdata.model.FieldData;
import org.testgen.runtime.classdata.model.SetterMethodData;
import org.testgen.runtime.generation.api.collections.CollectionGenerationFactory;
import org.testgen.runtime.generation.api.naming.NamingService;
import org.testgen.runtime.generation.api.naming.NamingServiceProvider;
import org.testgen.runtime.generation.api.simple.SimpleObjectGenerationFactory;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.ComplexBluePrint;

/**
 * Interface for generating {@link ComplexBluePrint}.
 *
 * @param <T> Type of ClassDeclaration
 * @param <E> Type of a CodeBlock
 * @param <S> Type of a single Expression
 */
public interface ComplexObjectGeneration<T, E, S> extends FieldGeneration<T, ComplexBluePrint> {

	void createObject(E codeBlock, ComplexBluePrint bluePrint, boolean isField, ClassData classData,
			Set<FieldData> calledFields);

	void createComplexTypes(E codeBlock, ComplexBluePrint bluePrint, ClassData classData, Set<FieldData> calledFields);

	void addChildToObject(E codeBlock, BluePrint bluePrint, SetterMethodData setter, S accessExpr);

	void addChildToField(E codeBlock, BluePrint bluePrint, S accessExpr);

	default SimpleObjectGenerationFactory<T, E, S> getSimpleObjectGenerationFactory() {
		return GenerationFactory.<T, E, S>getInstance().getSimpleObjectGenerationFactory();
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

package org.testgen.runtime.generation.api;

import java.util.function.Consumer;

import org.testgen.runtime.classdata.model.SetterMethodData;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;
import org.testgen.runtime.generation.api.collections.CollectionGenerationFactory;
import org.testgen.runtime.generation.api.naming.NamingService;
import org.testgen.runtime.generation.api.naming.NamingServiceProvider;
import org.testgen.runtime.generation.api.simple.SimpleObjectGenerationFactory;
import org.testgen.runtime.valuetracker.blueprint.AbstractBasicCollectionBluePrint;
import org.testgen.runtime.valuetracker.blueprint.ArrayBluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.ComplexBluePrint;

/**
 * Interface for Generating Arrays for {@link ArrayBluePrint}
 */
public interface ArrayGeneration<T, E, S> extends FieldGeneration<T, ArrayBluePrint> {

	/**
	 * Generates a complete array inclusive all elements of the array. The array
	 * also gets fully initalized with his elements.
	 * 
	 * @param statementTree codeBlock where the generated code is added
	 * @param bluePrint     of the array
	 * @param signature     of the array
	 * @param isField       marks that the array is a Field of generated Class
	 */
	void createArray(E statementTree, ArrayBluePrint bluePrint, SignatureType signature, boolean isField);

	/**
	 * Generates only the complexTypes {@link BluePrint#isComplexType()} of the
	 * array ComplexTypes are: <br>
	 * - {@link ArrayBluePrint} <br>
	 * - {@link AbstractBasicCollectionBluePrint} and all his implementations <br>
	 * - {@link ComplexBluePrint} <br>
	 * 
	 * All Types of {@link SimpleBLuePrint} doesn't get generated
	 * 
	 * @param statementTree codeBlock where the generated code is added
	 * @param bluePrint     of the array
	 */
	void createComplexElements(E statementTree, ArrayBluePrint bluePrint, SignatureType signature);

	/**
	 * Add the array to a Object dependent on the {@link SetterMethodData}
	 * 
	 * @param statementTree codeBlock where the generated code is added
	 * @param bluePrint     of the array
	 * @param setter        setter of the object the collection is added to.
	 * @param isField       marks the that the collection is a Field of generated
	 *                      Class
	 * @param accessExpr    expression to access the object where to collection is
	 *                      added to
	 */
	void addArrayToObject(E statementTree, ArrayBluePrint bluePrint, SetterMethodData setter, //
			boolean isField, S accessExpr);

	/**
	 * Add the array to a Object direct by accessing a field directly
	 * 
	 * @param statementTree codeBlock where the generated code is added
	 * @param bluePrint     of the array
	 * @param isField       marks the that the collection is a Field of generated
	 *                      Class
	 * @param accessExpr    expression to access the object where to collection is
	 *                      added to
	 */
	void addArrayToField(E statementTree, ArrayBluePrint bluePrint, boolean isField, S accessExpr);

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

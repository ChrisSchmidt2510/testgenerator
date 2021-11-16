package org.testgen.runtime.generation.api;

import java.util.function.Consumer;

import org.testgen.runtime.classdata.model.SetterMethodData;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;
import org.testgen.runtime.generation.api.collections.CollectionGenerationFactory;
import org.testgen.runtime.generation.api.naming.NamingService;
import org.testgen.runtime.generation.api.naming.NamingServiceProvider;
import org.testgen.runtime.generation.api.simple.SimpleObjectGenerationFactory;
import org.testgen.runtime.generation.api.spezial.SpezialObjectGenerationFactory;
import org.testgen.runtime.valuetracker.blueprint.BasicCollectionBluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.complextypes.ArrayBluePrint;
import org.testgen.runtime.valuetracker.blueprint.complextypes.ComplexBluePrint;

/**
 * Interface for Generating Arrays from a {@link ArrayBluePrint}.
 * 
 * @param <T> Type of ClassDeclaration
 * @param <E> Type of a CodeBlock
 * @param <S> Type of a single Expression
 */
public interface ArrayGeneration<T, E, S> extends FieldGeneration<T, ArrayBluePrint> {

	/**
	 * Generates a complete array inclusive all elements of the array. The array
	 * also gets fully initialized with his elements.
	 * 
	 * @param statementTree codeBlock where the generated code is added
	 * @param bluePrint     of the array
	 * @param signature     of the array
	 * @param isField       flag if the {@link ArrayBluePrint} is a field of the
	 *                      compilationUnit
	 */
	void createArray(E statementTree, ArrayBluePrint bluePrint, SignatureType signature, boolean isField);

	/**
	 * Generates only the complexTypes {@link BluePrint#isComplexType()} of the
	 * array. <br>
	 * ComplexTypes are: <br>
	 * - {@link ArrayBluePrint} <br>
	 * - {@link BasicCollectionBluePrint} and all his implementations <br>
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
	 * @param isField       flag if the {@link ArrayBluePrint} is a field of the
	 *                      compilationUnit
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
	 * @param isField       flag if the {@link ArrayBluePrint} is a field of the
	 *                      compilationUnit
	 * @param accessExpr    expression to access the object where to collection is
	 *                      added to
	 */
	void addArrayToField(E statementTree, ArrayBluePrint bluePrint, boolean isField, S accessExpr);

	void setSimpleObjectGenerationFactory(SimpleObjectGenerationFactory<T, E, S> simpleGenerationFactory);

	void setCollectionGenerationFactory(CollectionGenerationFactory<T, E, S> collectionGenerationFactory);

	void setSpezialGenerationFactory(SpezialObjectGenerationFactory<T, E, S, BluePrint> spezialGenerationFactory);
	
	void setComplexObjectGeneration(ComplexObjectGeneration<T, E, S> complexObjectGeneration);

	void setImportCallBackHandler(Consumer<Class<?>> importCallBackHandler);

	default NamingService<E> getNamingService() {
		return NamingServiceProvider.getNamingService();
	}
}

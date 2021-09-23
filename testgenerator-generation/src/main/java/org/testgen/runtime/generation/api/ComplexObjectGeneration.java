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
import org.testgen.runtime.valuetracker.blueprint.BasicCollectionBluePrint;
import org.testgen.runtime.valuetracker.blueprint.ArrayBluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.ComplexBluePrint;

/**
 * Interface for generating a Object from a {@link ComplexBluePrint}.
 *
 * @param <T> Type of ClassDeclaration
 * @param <E> Type of a CodeBlock
 * @param <S> Type of a single Expression
 */
public interface ComplexObjectGeneration<T, E, S> extends FieldGeneration<T, ComplexBluePrint> {

	/**
	 * Generates a complete Object inclusive all of his child's. The Object gets
	 * fully initialized if possible.
	 * 
	 * @param codeBlock    where the generated code is added
	 * @param bluePrint    of the Object
	 * @param isField      flag if the {@link ComplexBluePrint} is a field of the
	 *                     compilationUnit
	 * @param classData    metadata of the Object
	 * @param calledFields all used fields from this Object
	 */
	void createObject(E codeBlock, ComplexBluePrint bluePrint, boolean isField, ClassData classData,
			Set<FieldData> calledFields);

	/**
	 * Generates the all child's of the {@link ComplexBluePrint} where the method
	 * {@link BluePrint#isComplexType()} is true.<br>
	 * 
	 * ComplexTypes are: <br>
	 * - {@link ArrayBluePrint} <br>
	 * - {@link BasicCollectionBluePrint} and all his implementations <br>
	 * - {@link ComplexBluePrint} <br>
	 * 
	 * 
	 * @implNote Normally this method is called in method
	 *           {@link this#createObject(Object, ComplexBluePrint, boolean,
	 *           ClassData, Set)}
	 * 
	 * @param codeBlock    where the generated code is added
	 * @param bluePrint    of the Object
	 * @param classData    metadata of the Object
	 * @param calledFields all used fields from this Object
	 */
	void createComplexTypes(E codeBlock, ComplexBluePrint bluePrint, ClassData classData, Set<FieldData> calledFields);

	/**
	 * Adds a child BluePrint to a object dependent on the {@link SetterMethodData}.
	 * 
	 * @param codeBlock  where the generated code is added
	 * @param bluePrint  child of the Object
	 * @param setter     metadata for a setter method
	 * @param accessExpr expression to access the parent object where to child is
	 *                   added to
	 */
	void addChildToObject(E codeBlock, BluePrint bluePrint, SetterMethodData setter, S accessExpr);

	/**
	 * Sets a child BluePrint direct to a Field.
	 * 
	 * @param codeBlock  where the generated code is added
	 * @param bluePrint  child of a Object
	 * @param accessExpr expression to access the parent object where to child is
	 *                   added to
	 */
	void addChildToField(E codeBlock, BluePrint bluePrint, S accessExpr);

	void setSimpleObjectGenerationFactory(SimpleObjectGenerationFactory<T, E, S> simpleGenerationFactory);

	void setCollectionGenerationFactory(CollectionGenerationFactory<T, E, S> collectionGenerationFactory);

	void setArrayGeneration(ArrayGeneration<T, E, S> arrayGeneration);

	void setImportCallBackHandler(Consumer<Class<?>> importCallBackHandler);

	default NamingService<E> getNamingService() {
		return NamingServiceProvider.getNamingService();
	}

}

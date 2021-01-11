package org.testgen.runtime.generation.api.collections;

import java.util.Collection;

import org.testgen.runtime.classdata.model.SetterMethodData;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;
import org.testgen.runtime.valuetracker.blueprint.AbstractBasicCollectionBluePrint;
import org.testgen.runtime.valuetracker.blueprint.ArrayBluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.ComplexBluePrint;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;

/**
 * Base interface that is used for generating Collections{@link Collection} for
 * a {@link AbstractBasicCollectionBluePrint}. If the user a want to implement a
 * CollectionGeneration only for a single or group of Types use
 * {@link CollectionGeneration}. In the standard implementation of the
 * CollectionGenerationFactory are all types of CollectionGeneration are loaded
 * with the Java SPI.
 */
public interface CollectionGenerationFactory<T, E, S> {

	/**
	 * Generates the complete {@link Collection} inclusive the elements of the
	 * collection. The collection gets also fully initalized with his elements
	 * 
	 * @param statementTree codeBlock where the generated code is added
	 * @param bluePrint     of the {@link Collection}
	 * @param signature     of the {@link Collection}
	 * @param isField       marks the that the collection is a Field of generated
	 *                      Class
	 */
	void createCollection(E statementTree, AbstractBasicCollectionBluePrint<?> bluePrint, //
			SignatureType signature, boolean isField);

	/**
	 * Generates only the complexTypes {@link BluePrint#isComplexType()} of
	 * the @param bluePrint. ComplexTypes are: <br>
	 * - {@link AbstractBasicCollectionBluePrint} and all his implementations <br>
	 * - {@link ArrayBluePrint} <br>
	 * - {@link ComplexBluePrint} <br>
	 * 
	 * All Types of {@link SimpleBluePrint} doesnt't get generated.
	 * 
	 * @param statementTree codeBlock where the generated code is added
	 * @param bluePrint     of the {@link Collection}
	 * @param signature     of the {@link Collection}
	 */
	void createComplexElements(E statementTree, AbstractBasicCollectionBluePrint<?> bluePrint, SignatureType signature);

	/**
	 * Add the collection to a Object dependent on the {@link SetterMethodData}
	 * 
	 * @param statementTree codeBlock where the generated code is added
	 * @param bluePrint     of the {@link Collection}
	 * @param isField       marks the that the collection is a Field of generated
	 *                      Class
	 * @param setter        setter of the object the collection is added to.
	 * @param accessExpr    expression to access the object where to collection is
	 *                      added to
	 */
	void addCollectionToObject(E statementTree, AbstractBasicCollectionBluePrint<?> bluePrint, boolean isField,
			SetterMethodData setter, S accessExpr);

	/**
	 * Add the collection to a Object direct by accessing a field directly
	 * 
	 * @param statementTree codeBlock where the generated code is added
	 * @param bluePrint     of the {@link Collection}
	 * @param isField       marks the that the collection is a Field of generated
	 *                      Class
	 * @param accessExpr    expression to access the object where to collection is
	 *                      added to
	 */
	void addCollectionToField(E statementTree, AbstractBasicCollectionBluePrint<?> bluePrint, boolean isField,
			S accessExpr);
}

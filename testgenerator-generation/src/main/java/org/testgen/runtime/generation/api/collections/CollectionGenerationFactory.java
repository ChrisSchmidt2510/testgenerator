package org.testgen.runtime.generation.api.collections;

import java.util.Collection;
import java.util.function.Consumer;

import org.testgen.runtime.classdata.model.SetterMethodData;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;
import org.testgen.runtime.generation.api.ArrayGeneration;
import org.testgen.runtime.generation.api.ComplexObjectGeneration;
import org.testgen.runtime.generation.api.FieldGeneration;
import org.testgen.runtime.generation.api.naming.NamingService;
import org.testgen.runtime.generation.api.naming.NamingServiceProvider;
import org.testgen.runtime.generation.api.simple.SimpleObjectGenerationFactory;
import org.testgen.runtime.generation.api.spezial.SpezialObjectGenerationFactory;
import org.testgen.runtime.valuetracker.blueprint.BasicCollectionBluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.complextypes.ArrayBluePrint;
import org.testgen.runtime.valuetracker.blueprint.complextypes.ComplexBluePrint;

/**
 * Base interface that is used for generating Collections{@link Collection} for
 * a {@link BasicCollectionBluePrint}. If the user a want to implement a
 * CollectionGeneration only for a single or a group of Types use
 * {@link CollectionGeneration}.
 * 
 * @implNote The standard implementation of the
 *           {@link CollectionGenerationFactory} includes all implementations of
 *           the {@link CollectionGeneration} using the Java SPI technology.
 * 
 * @param <T> Type of ClassDeclaration
 * @param <E> Type of a CodeBlock
 * @param <S> Type of a single Expression
 */
public interface CollectionGenerationFactory<T, E, S> extends FieldGeneration<T, BasicCollectionBluePrint<?>> {

	/**
	 * Generates the complete {@link Collection} inclusive the elements of the
	 * collection. The collection gets also fully initialized with his elements
	 * 
	 * @param codeBlock codeBlock where the generated code is added
	 * @param bluePrint of the {@link Collection}
	 * @param signature of the {@link Collection}
	 * @param isField   marks the that the collection is a Field of generated Class
	 */
	void createCollection(E codeBlock, BasicCollectionBluePrint<?> bluePrint, //
			SignatureType signature, boolean isField);

	/**
	 * Generates only the complexTypes {@link BluePrint#isComplexType()} of
	 * the @param bluePrint. ComplexTypes are: <br>
	 * - {@link BasicCollectionBluePrint} and all his implementations <br>
	 * - {@link ArrayBluePrint} <br>
	 * - {@link ComplexBluePrint} <br>
	 * 
	 * All Types of {@link SimpleBluePrint} doesnt't get generated.
	 * 
	 * @param codeBlock codeBlock where the generated code is added
	 * @param bluePrint of the {@link Collection}
	 * @param signature of the {@link Collection}
	 */
	void createComplexElements(E codeBlock, BasicCollectionBluePrint<?> bluePrint, SignatureType signature);

	/**
	 * Add the collection to a Object dependent on the {@link SetterMethodData}
	 * 
	 * @param codeBlock  codeBlock where the generated code is added
	 * @param bluePrint  of the {@link Collection}
	 * @param isField    marks the that the collection is a Field of generated Class
	 * @param setter     setter of the object the collection is added to.
	 * @param accessExpr expression to access the object where to collection is
	 *                   added to
	 */
	void addCollectionToObject(E codeBlock, BasicCollectionBluePrint<?> bluePrint, boolean isField,
			SetterMethodData setter, S accessExpr);

	/**
	 * Add the collection to a Object direct by accessing a field directly
	 * 
	 * @param codeBlock  codeBlock where the generated code is added
	 * @param bluePrint  of the {@link Collection}
	 * @param isField    marks the that the collection is a Field of generated Class
	 * @param accessExpr expression to access the object where to collection is
	 *                   added to
	 */
	void addCollectionToField(E codeBlock, BasicCollectionBluePrint<?> bluePrint, boolean isField, S accessExpr);

	void setSimpleObjectGenerationFactory(SimpleObjectGenerationFactory<T, E, S> simpleGenerationFactory);

	void setComplexObjectGeneration(ComplexObjectGeneration<T, E, S> complexObjectGeneration);
	
	void setSpezialGenerationFactory(SpezialObjectGenerationFactory<T, E, S, BluePrint> spezialGenerationFactory);

	void setArrayGeneration(ArrayGeneration<T, E, S> arrayGeneration);

	void setImportCallBackHandler(Consumer<Class<?>> importCallBackHandler);

	default NamingService<E> getNamingService() {
		return NamingServiceProvider.getNamingService();
	}
}

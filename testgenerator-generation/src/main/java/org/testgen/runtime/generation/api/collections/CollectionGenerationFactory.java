package org.testgen.runtime.generation.api.collections;

import java.util.Collection;
import java.util.function.Consumer;

import org.testgen.runtime.classdata.model.SetterMethodData;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;
import org.testgen.runtime.generation.api.ArrayGeneration;
import org.testgen.runtime.generation.api.ComplexObjectGeneration;
import org.testgen.runtime.generation.api.naming.NamingService;
import org.testgen.runtime.generation.api.naming.NamingServiceProvider;
import org.testgen.runtime.generation.api.simple.SimpleObjectGenerationFactory;
import org.testgen.runtime.valuetracker.blueprint.AbstractBasicCollectionBluePrint;
import org.testgen.runtime.valuetracker.blueprint.ArrayBluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.ComplexBluePrint;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;

/**
 * Base interface that is used for generating Collections{@link Collection} for
 * a {@link AbstractBasicCollectionBluePrint}. If the user a want to implement a
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
public interface CollectionGenerationFactory<T, E, S> {

	/**
	 * Generates the complete {@link Collection} inclusive the elements of the
	 * collection. The collection gets also fully initialized with his elements
	 * 
	 * @param codeBlock codeBlock where the generated code is added
	 * @param bluePrint of the {@link Collection}
	 * @param signature of the {@link Collection}
	 * @param isField   marks the that the collection is a Field of generated Class
	 */
	void createCollection(E codeBlock, AbstractBasicCollectionBluePrint<?> bluePrint, //
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
	 * @param codeBlock codeBlock where the generated code is added
	 * @param bluePrint of the {@link Collection}
	 * @param signature of the {@link Collection}
	 */
	void createComplexElements(E codeBlock, AbstractBasicCollectionBluePrint<?> bluePrint, SignatureType signature);

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
	void addCollectionToObject(E codeBlock, AbstractBasicCollectionBluePrint<?> bluePrint, boolean isField,
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
	void addCollectionToField(E codeBlock, AbstractBasicCollectionBluePrint<?> bluePrint, boolean isField,
			S accessExpr);

	void setSimpleObjectGenerationFactory(SimpleObjectGenerationFactory<T, E, S> simpleGenerationFactory);

	void setComplexObjectGeneration(ComplexObjectGeneration<T, E, S> complexObjectGeneration);

	void setArrayGeneration(ArrayGeneration<T, E, S> arrayGeneration);

	void setImportCallBackHandler(Consumer<Class<?>> importCallBackHandler);

	default NamingService<E> getNamingService() {
		return NamingServiceProvider.getNamingService();
	}
}
package org.testgen.runtime.generation.api.spezial;

import java.lang.reflect.Proxy;
import java.util.function.Consumer;

import org.testgen.runtime.classdata.model.descriptor.SignatureType;
import org.testgen.runtime.generation.api.ArrayGeneration;
import org.testgen.runtime.generation.api.ComplexObjectGeneration;
import org.testgen.runtime.generation.api.FieldGeneration;
import org.testgen.runtime.generation.api.collections.CollectionGenerationFactory;
import org.testgen.runtime.generation.api.naming.NamingService;
import org.testgen.runtime.generation.api.naming.NamingServiceProvider;
import org.testgen.runtime.generation.api.simple.SimpleObjectGenerationFactory;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;

/**
 * Base interface that is used for generating spezial Classes e.g. {@link Proxy}
 * or Lambda-Expressions of a {@link BluePrint}. If the user want to implement a
 * SpezialGeneration for a single or a group of spezial Types use
 * {@link SpezialObjectGeneration}.
 * 
 * @implNote The standard implementation of the
 *           {@link SpezialObjectGenerationFactory} includes all implementations
 *           of the {@link SpezialObjectGeneration} using the Java SPI
 *           technology.
 *
 * @param <T> Type of ClassDeclaration
 * @param <E> Type of a CodeBlock
 * @param <S> Type of a single Expression
 * @param <B> Type of the {@link BluePrint} implementation
 */
public interface SpezialObjectGenerationFactory<T, E, S, B extends BluePrint> extends FieldGeneration<T, B> {

	/**
	 * Generates the complete spezial Object inclusive all child's.
	 *  
	 * @param codeBlock where the generated code is added
	 * @param bluePrint of the spezial Object
	 * @param signature of the spezial Object
	 * @param isField marks that the Object is a Field of generated Class
	 */
	void createObject(E codeBlock, B bluePrint, SignatureType signature, boolean isField);

	void setSimpleObjectGenerationFactory(SimpleObjectGenerationFactory<T, E, S> simpleGenerationFactory);

	void setCollectionGenerationFactory(CollectionGenerationFactory<T, E, S> collectionGenerationFactory);

	void setArrayGenerationFactory(ArrayGeneration<T, E, S> arrayGeneration);

	void setComplexObjectGeneration(ComplexObjectGeneration<T, E, S> complexObjectGeneration);

	void setImportCallBackHandler(Consumer<Class<?>> importCallBackHandler);

	default NamingService<E> getNamingService() {
		return NamingServiceProvider.getNamingService();
	}
}

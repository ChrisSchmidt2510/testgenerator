package org.testgen.runtime.generation.api.spezial;

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

public interface SpezialObjectGenerationFactory<T, E, S, B extends BluePrint> extends FieldGeneration<T, B> {

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

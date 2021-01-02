package org.testgen.runtime.generation.api;

import java.util.function.Consumer;

import org.testgen.runtime.generation.api.collections.CollectionGenerationFactory;
import org.testgen.runtime.generation.api.simple.SimpleObjectGenerationFactory;

public class GenerationFactory<T, E, S> {

	private static final GenerationFactory<?, ?, ?> INSTANCE = new GenerationFactory<>();

	private ComplexObjectGeneration<T, E, S> complexObjectGeneration;
	private SimpleObjectGenerationFactory<T, E, S> simpleObjectGeneration;

	private CollectionGenerationFactory<T, E, S> collectionGenerationFactory;
	private ArrayGeneration<T, E, S> arrayGeneration;

	private Consumer<Class<?>> importCallBackHandler;

	private GenerationFactory() {
	}

	@SuppressWarnings("unchecked")
	public static <V, K, A> GenerationFactory<V, K, A> getInstance() {
		return (GenerationFactory<V, K, A>) INSTANCE;
	}

	public void setComplexObjectGeneration(ComplexObjectGeneration<T, E, S> objectGeneration) {
		this.complexObjectGeneration = objectGeneration;
	}

	public void setSimpleObjectGenerationFactory(SimpleObjectGenerationFactory<T, E, S> simpleObjectGenerationFactory) {
		this.simpleObjectGeneration = simpleObjectGenerationFactory;
	}

	public void setCollectionGenerationFactory(CollectionGenerationFactory<T, E, S> collectionGenerationFactory) {
		this.collectionGenerationFactory = collectionGenerationFactory;
	}

	public void setArrayGeneration(ArrayGeneration<T, E, S> arrayGeneration) {
		this.arrayGeneration = arrayGeneration;
	}

	public void setImportCallBackHandler(Consumer<Class<?>> importCallBackHandler) {
		this.importCallBackHandler = importCallBackHandler;
	}

	public ComplexObjectGeneration<T, E, S> getComplexObjectGeneration() {
		return complexObjectGeneration;
	}

	public SimpleObjectGenerationFactory<T, E, S> getSimpleObjectGenerationFactory() {
		return simpleObjectGeneration;
	}

	public CollectionGenerationFactory<T, E, S> getCollectionGenerationFactory() {
		return collectionGenerationFactory;
	}

	public ArrayGeneration<T, E, S> getArrayGeneration() {
		return arrayGeneration;
	}

	public Consumer<Class<?>> getImportCallBackHandler() {
		return importCallBackHandler;
	}

}

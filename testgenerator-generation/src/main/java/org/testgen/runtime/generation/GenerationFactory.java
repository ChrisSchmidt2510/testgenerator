package org.testgen.runtime.generation;

public class GenerationFactory<T, E> {

	private static final GenerationFactory<?, ?> INSTANCE = new GenerationFactory<>();

	private ComplexObjectGeneration<T, E> complexObjectGeneration;
	private SimpleObjectGeneration<T, E> simpleObjectGeneration;

	private CollectionGeneration<T, E> containerGeneration;
	private ArrayGeneration<T, E> arrayGeneration;

	private GenerationFactory() {
	}

	@SuppressWarnings("unchecked")
	public static <V, K> GenerationFactory<V, K> getInstance() {
		return (GenerationFactory<V, K>) INSTANCE;
	}

	public void setComplexObjectGeneration(ComplexObjectGeneration<T, E> objectGeneration) {
		this.complexObjectGeneration = objectGeneration;
	}

	public void setSimpleObjectGeneration(SimpleObjectGeneration<T, E> simpleObjectGeneration) {
		this.simpleObjectGeneration = simpleObjectGeneration;
	}

	public void setCollectionGeneration(CollectionGeneration<T, E> containerGeneration) {
		this.containerGeneration = containerGeneration;
	}

	public void setArrayGeneration(ArrayGeneration<T, E> arrayGeneration) {
		this.arrayGeneration = arrayGeneration;
	}

	public ComplexObjectGeneration<T, E> getComplexObjectGeneration() {
		return complexObjectGeneration;
	}

	public SimpleObjectGeneration<T, E> getSimpleObjectGeneration() {
		return simpleObjectGeneration;
	}

	public CollectionGeneration<T, E> getContainerGeneration() {
		return containerGeneration;
	}

	public ArrayGeneration<T, E> getArrayGeneration() {
		return arrayGeneration;
	}

}

package org.testgen.runtime.generation.api.simple;

import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;

public interface SimpleObjectGenerationFactory<T, E, S> {

	public SimpleObjectGeneration<T, E, S> of(SimpleBluePrint<?> bluePrint);

}

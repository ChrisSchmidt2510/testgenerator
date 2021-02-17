package org.testgen.runtime.generation.api.simple;

import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;

public interface SimpleObjectGeneration<T, E, S> extends SimpleObjectGenerationFactory<T, E, S> {

	public boolean canGenerateBluePrint(SimpleBluePrint<?> bluePrint);

	default int getPriority() {
		return 0;
	}

}

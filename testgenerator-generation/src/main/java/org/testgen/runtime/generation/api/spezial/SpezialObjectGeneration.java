package org.testgen.runtime.generation.api.spezial;

import org.testgen.runtime.valuetracker.blueprint.BluePrint;

public interface SpezialObjectGeneration<T,E,S, B extends BluePrint> extends SpezialObjectGenerationFactory<T, E, S, B>
{
	boolean canGenerateBluePrint(BluePrint bluePrint);
	
	void setSpezialObjectGenerationFactory(SpezialObjectGenerationFactory<T, E, S, BluePrint> spezialGenerationFactory);
	
	default int getPriority() {
		return 0;
	}
}

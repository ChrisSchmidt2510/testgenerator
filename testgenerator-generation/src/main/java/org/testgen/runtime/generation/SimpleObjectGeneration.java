package org.testgen.runtime.generation;

import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;

public interface SimpleObjectGeneration<T, E> extends FieldGeneration<T, SimpleBluePrint<?>> {

	public void createObject(E statementTree, SimpleBluePrint<?> bluePrint);

}

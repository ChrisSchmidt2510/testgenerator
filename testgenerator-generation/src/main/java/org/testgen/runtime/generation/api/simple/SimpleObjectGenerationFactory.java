package org.testgen.runtime.generation.api.simple;

import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;

public interface SimpleObjectGenerationFactory<T, E, S> {

	public void createField(T compilationUnit, SimpleBluePrint<?> bluePrint, boolean withInitalizer);

	public void createObject(E statementTree, SimpleBluePrint<?> bluePrint, boolean isField);

	public S createInlineObject(SimpleBluePrint<?> bluePrint);

}

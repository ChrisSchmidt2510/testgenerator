package org.testgen.runtime.generation.api.collections;

import org.testgen.runtime.classdata.model.FieldData;
import org.testgen.runtime.classdata.model.SetterMethodData;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;
import org.testgen.runtime.valuetracker.blueprint.AbstractBasicCollectionBluePrint;

public interface CollectionGenerationFactory<T, E, S> {

	void createCollection(E statementTree, AbstractBasicCollectionBluePrint<?> bluePrint, //
			SignatureType signature, boolean onlyCreateCollectionElements, boolean isField);

	void addCollectionToObject(E statementTree, AbstractBasicCollectionBluePrint<?> bluePrint, SetterMethodData setter, //
			String objectName);

	void addCollectionToObject(E statementTree, AbstractBasicCollectionBluePrint<?> bluePrint, FieldData field,
			String objectName);
}

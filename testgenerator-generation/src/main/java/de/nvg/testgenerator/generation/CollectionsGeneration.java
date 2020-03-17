package de.nvg.testgenerator.generation;

import com.squareup.javapoet.CodeBlock.Builder;
import com.squareup.javapoet.TypeSpec;

import de.nvg.runtime.classdatamodel.FieldData;
import de.nvg.runtime.classdatamodel.SetterMethodData;
import de.nvg.runtime.classdatamodel.SignatureData;
import de.nvg.valuetracker.blueprint.AbstractBasicCollectionBluePrint;

public interface CollectionsGeneration {

	void setComplexObjectGeneration(ComplexObjectGeneration objectGeneration);

	void createCollection(Builder code, AbstractBasicCollectionBluePrint<?> basicCollectionBP, //
			SignatureData signature, boolean onlyCreateCollectionElements, boolean isField);

	void addCollectionToObject(Builder code, AbstractBasicCollectionBluePrint<?> basicCollectionBP, SetterMethodData setter,
			String objectName);

	void addCollectionToObject(Builder code, AbstractBasicCollectionBluePrint<?> basicCollectionBP, //
			FieldData field, String objectName);

	void addFieldToClass(TypeSpec.Builder typeSpec, AbstractBasicCollectionBluePrint<?> bluePrint, SignatureData signature);

}

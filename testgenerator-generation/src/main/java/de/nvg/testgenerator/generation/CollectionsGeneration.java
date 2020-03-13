package de.nvg.testgenerator.generation;

import com.squareup.javapoet.CodeBlock.Builder;
import com.squareup.javapoet.TypeSpec;

import de.nvg.runtime.classdatamodel.FieldData;
import de.nvg.runtime.classdatamodel.SetterMethodData;
import de.nvg.runtime.classdatamodel.SignatureData;
import de.nvg.valuetracker.blueprint.BasicCollectionBluePrint;

public interface CollectionsGeneration {

	public void setComplexObjectGeneration(ComplexObjectGeneration objectGeneration);

	public void createCollection(Builder code, BasicCollectionBluePrint<?> basicCollectionBP, //
			SignatureData signature, boolean onlyCreateCollectionElements, boolean isField);

	public void addCollectionToObject(Builder code, BasicCollectionBluePrint<?> basicCollectionBP,
			SetterMethodData setter, String objectName);

	public void addCollectionToObject(Builder code, BasicCollectionBluePrint<?> basicCollectionBP, //
			FieldData field, String objectName);

	public void addFieldToClass(TypeSpec.Builder typeSpec, BasicCollectionBluePrint<?> bluePrint,
			SignatureData signature);

}

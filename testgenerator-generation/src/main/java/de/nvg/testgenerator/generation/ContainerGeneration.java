package de.nvg.testgenerator.generation;

import com.squareup.javapoet.CodeBlock.Builder;
import com.squareup.javapoet.TypeSpec;

import de.nvg.runtime.classdatamodel.FieldData;
import de.nvg.runtime.classdatamodel.SetterMethodData;
import de.nvg.runtime.classdatamodel.SignatureData;
import de.nvg.testgenerator.generation.naming.NamingService;
import de.nvg.valuetracker.blueprint.AbstractBasicCollectionBluePrint;
import de.nvg.valuetracker.blueprint.ArrayBluePrint;
import de.nvg.valuetracker.blueprint.BluePrint;

public interface ContainerGeneration {

	void setComplexObjectGeneration(ComplexObjectGeneration objectGeneration);

	void setNamingService(NamingService namingService);

	void createCollection(Builder code, AbstractBasicCollectionBluePrint<?> basicCollectionBP, //
			SignatureData signature, boolean onlyCreateCollectionElements, boolean isField);

	void createArray(Builder code, ArrayBluePrint arrayBluePrint, boolean onlyCreateElements, //
			boolean isField);

	void addContainerToObject(Builder code, BluePrint containerBP, SetterMethodData setter, //
			String objectName);

	void addContainerToObject(Builder code, BluePrint containerBP, FieldData field, String objectName);

	void addFieldToClass(TypeSpec.Builder typeSpec, BluePrint containerBP, SignatureData signature);

}

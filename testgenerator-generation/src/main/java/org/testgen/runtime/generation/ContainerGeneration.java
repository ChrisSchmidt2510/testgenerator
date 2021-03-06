package org.testgen.runtime.generation;

import org.testgen.runtime.classdata.model.FieldData;
import org.testgen.runtime.classdata.model.SetterMethodData;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;
import org.testgen.runtime.generation.naming.NamingService;
import org.testgen.runtime.valuetracker.blueprint.AbstractBasicCollectionBluePrint;
import org.testgen.runtime.valuetracker.blueprint.ArrayBluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;

import com.squareup.javapoet.CodeBlock.Builder;
import com.squareup.javapoet.TypeSpec;

public interface ContainerGeneration {

	void setComplexObjectGeneration(ComplexObjectGeneration objectGeneration);

	void setNamingService(NamingService namingService);

	void createCollection(Builder code, AbstractBasicCollectionBluePrint<?> basicCollectionBP, //
			SignatureType signature, boolean onlyCreateCollectionElements, boolean isField);

	void createArray(Builder code, ArrayBluePrint arrayBluePrint, boolean onlyCreateElements, //
			boolean isField);

	void addContainerToObject(Builder code, BluePrint containerBP, SetterMethodData setter, //
			String objectName);

	void addContainerToObject(Builder code, BluePrint containerBP, FieldData field, String objectName);

	void addFieldToClass(TypeSpec.Builder typeSpec, BluePrint containerBP, SignatureType signature);

}

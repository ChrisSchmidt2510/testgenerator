package de.nvg.testgenerator.generation;

import java.util.Collection;
import java.util.Set;

import com.squareup.javapoet.TypeSpec;

import de.nvg.runtime.classdatamodel.ClassData;
import de.nvg.runtime.classdatamodel.FieldData;
import de.nvg.valuetracker.blueprint.BluePrint;

public interface TestClassGeneration {

	public void prepareTestObject(TypeSpec.Builder typeSpec, BluePrint testObject, ClassData classData,
			Set<FieldData> calledFields);

	public void prepareMethodParameters(TypeSpec.Builder typeSpec, Collection<BluePrint> methodParameters);

	public void generateTestMethod(TypeSpec.Builder typeSpec, String methodName);

}

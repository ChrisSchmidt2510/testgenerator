package de.nvg.testgenerator.generation;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.squareup.javapoet.TypeSpec;

import de.nvg.runtime.classdatamodel.ClassData;
import de.nvg.runtime.classdatamodel.FieldData;
import de.nvg.runtime.classdatamodel.SignatureData;
import de.nvg.valuetracker.blueprint.BluePrint;

public interface TestClassGeneration {

	void prepareTestObject(TypeSpec.Builder typeSpec, BluePrint testObject, ClassData classData,
			Set<FieldData> calledFields);

	void prepareMethodParameters(TypeSpec.Builder typeSpec, Collection<BluePrint> methodParameters, //
			Map<Integer, SignatureData> methodSignature);

	void generateTestMethod(TypeSpec.Builder typeSpec, String methodName);

}

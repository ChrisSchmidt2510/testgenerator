package de.nvg.testgenerator.generation;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.testgen.runtime.classdata.model.ClassData;
import org.testgen.runtime.classdata.model.FieldData;
import org.testgen.runtime.classdata.model.SignatureData;

import com.squareup.javapoet.TypeSpec;

import de.nvg.valuetracker.blueprint.BluePrint;

public interface TestClassGeneration {

	void prepareTestObject(TypeSpec.Builder typeSpec, BluePrint testObject, ClassData classData,
			Set<FieldData> calledFields);

	void prepareMethodParameters(TypeSpec.Builder typeSpec, Collection<BluePrint> methodParameters, //
			Map<Integer, SignatureData> methodSignature);

	void prepareProxyObjects(TypeSpec.Builder typeSpec, Collection<BluePrint> proxyObjects);

	void generateTestMethod(TypeSpec.Builder typeSpec, String methodName, boolean withProxyObjects);

}

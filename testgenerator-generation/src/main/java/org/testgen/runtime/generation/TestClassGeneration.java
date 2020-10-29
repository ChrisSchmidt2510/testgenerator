package org.testgen.runtime.generation;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testgen.runtime.classdata.model.ClassData;
import org.testgen.runtime.classdata.model.FieldData;
import org.testgen.runtime.classdata.model.descriptor.DescriptorType;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.ProxyBluePrint;

import com.squareup.javapoet.TypeSpec;

public interface TestClassGeneration {

	TypeSpec.Builder createTestClass(Class<?> testClass);

	void prepareTestObject(TypeSpec.Builder typeSpec, BluePrint testObject, ClassData classData,
			Set<FieldData> calledFields);

	void prepareMethodParameters(TypeSpec.Builder typeSpec, Collection<BluePrint> methodParameters, //
			List<DescriptorType> methodTypeTable);

	void prepareProxyObjects(TypeSpec.Builder typeSpec, Map<ProxyBluePrint, List<BluePrint>> proxyObjects);

	void generateTestMethod(TypeSpec.Builder typeSpec, String methodName, boolean withProxyObjects);

}

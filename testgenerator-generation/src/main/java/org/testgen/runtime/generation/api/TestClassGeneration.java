package org.testgen.runtime.generation.api;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testgen.runtime.classdata.model.ClassData;
import org.testgen.runtime.classdata.model.FieldData;
import org.testgen.runtime.classdata.model.descriptor.DescriptorType;
import org.testgen.runtime.generation.api.naming.NamingService;
import org.testgen.runtime.generation.api.naming.NamingServiceProvider;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.ProxyBluePrint;

public interface TestClassGeneration<T, E, S> {

	T createTestClass(Class<?> testClass);

	void prepareTestObject(T classDeclaration, BluePrint testObject, ClassData classData, Set<FieldData> calledFields);

	void prepareMethodParameters(T classDeclaration, List<BluePrint> methodParameters, //
			List<DescriptorType> methodTypeTable);

	void prepareProxyObjects(T classDeclaration, Map<ProxyBluePrint, List<BluePrint>> proxyObjects);

	void generateTestMethod(T classDeclaration, String methodName, boolean withProxyObjects);

	void toFile(T classDeclaration);

	default NamingService<E> getNamingService() {
		return NamingServiceProvider.getNamingService();
	}

}

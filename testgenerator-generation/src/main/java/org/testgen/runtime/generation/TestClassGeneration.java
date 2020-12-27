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

public interface TestClassGeneration<T, E> {

	T createTestClass(Class<?> testClass);

	void prepareTestObject(T compilationUnit, BluePrint testObject, ClassData classData, Set<FieldData> calledFields);

	void prepareMethodParameters(T compilationUnit, Collection<BluePrint> methodParameters, //
			List<DescriptorType> methodTypeTable);

	void prepareProxyObjects(T compilationUnit, Map<ProxyBluePrint, List<BluePrint>> proxyObjects);

	void generateTestMethod(T compilationUnit, String methodName, boolean withProxyObjects);

	void addDocumentation(T compilationUnit);

	void toFile(T compilationUnit);

	ComplexObjectGeneration<T, E> createComplexObjectGeneration();

	SimpleObjectGeneration<T, E> createSimpleObjectGeneration();

	CollectionGeneration<T, E> createCollectionGeneration();

	ArrayGeneration<T, E> createArrayGeneration();

}

package org.testgen.runtime.generation.api;

import java.lang.reflect.Proxy;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.testgen.runtime.classdata.model.ClassData;
import org.testgen.runtime.classdata.model.FieldData;
import org.testgen.runtime.classdata.model.descriptor.BasicType;
import org.testgen.runtime.classdata.model.descriptor.DescriptorType;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;
import org.testgen.runtime.generation.api.naming.NamingService;
import org.testgen.runtime.generation.api.naming.NamingServiceProvider;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.complextypes.ProxyBluePrint;

/**
 * Interface that defines the steps to generate a unit-test.
 *
 * @param <T> Type of ClassDeclaration
 * @param <E> Type of a CodeBlock
 * @param <S> Type of a single Expression
 */
public interface TestClassGeneration<T, E, S> {

	/**
	 * Creates the compilation unit where the unit-test method is added
	 * 
	 * @param testClass original class where the test is generated for.
	 * @param the       path on the filesystem where the test is generated into
	 * @return
	 */
	T createTestClass(Class<?> testClass, Path pathForTestclass);

	/**
	 * Create an abstract syntax tree for the testObject where the method to test is
	 * included.
	 * 
	 * @param classDeclaration compilationUnit where the unit-test is added
	 * @param testObject       {@link BluePrint} of the testObject
	 * @param classData        metadata of the Object
	 * @param calledFields     all used fields from this Object
	 */
	void prepareTestObject(T classDeclaration, BluePrint testObject, ClassData classData, Set<FieldData> calledFields);

	/**
	 * Create an expression where the static test method can be called.
	 * 
	 * @param testClass original class where the test is generated for.
	 */
	void prepareTestClass(Class<?> testClass);

	/**
	 * Creates an abstract syntax tree for each method parameter.
	 * 
	 * @param classDeclaration compilationUnit where the unit-test is added
	 * @param methodParameters {@link BluePrint} of all method parameters
	 * @param methodTypeTable  list with a descriptor for each method parameter.
	 *                         This list can contain two types of elements:<br>
	 *                         - {@link BasicType} for all normal types e.g.
	 *                         {@link String}<br>
	 *                         - {@link SignatureType} for generic types e.g.
	 *                         {@link Collection}
	 */
	void prepareMethodParameters(T classDeclaration, List<BluePrint> methodParameters, //
			List<DescriptorType> methodTypeTable);

	/**
	 * If a {@link Proxy} is used during execution of the test method, the returned
	 * values from the proxy methods are stored in the value part of the
	 * proxyObjects Map.
	 * 
	 * @param classDeclaration compilationUnit where the unit-test is added
	 * @param proxies          List with all created{@link ProxyBluePrint}
	 */
	void prepareProxyObjects(T classDeclaration, List<ProxyBluePrint> proxies);

	/**
	 * Adds the new unit-test to the classDeclaration.
	 * 
	 * @param classDeclaration compilationUnit where the unit-test is added
	 * @param methodName       name of the testMethod
	 * @param isStatic         mark that the testMethod is static
	 * @param withProxyObjects {@link Proxy} is used during the method execution
	 */
	void generateTestMethod(T classDeclaration, String methodName, boolean isStatic, boolean withProxyObjects);

	/**
	 * Writes the created or modified test class back to the filesystem.
	 * 
	 * @param classDeclaration compilationUnit where the unit-test is added
	 */
	void toFile(T classDeclaration);

	default NamingService<E> getNamingService() {
		return NamingServiceProvider.getNamingService();
	}

}

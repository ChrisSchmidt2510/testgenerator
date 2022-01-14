package org.testgen.runtime.generation;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.testgen.config.TestgeneratorConfig;
import org.testgen.core.ReflectionUtil;
import org.testgen.logging.LogManager;
import org.testgen.logging.Logger;
import org.testgen.runtime.classdata.model.ClassData;
import org.testgen.runtime.classdata.model.FieldData;
import org.testgen.runtime.classdata.model.descriptor.BasicType;
import org.testgen.runtime.classdata.model.descriptor.DescriptorType;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;
import org.testgen.runtime.generation.api.GenerationHelper;
import org.testgen.runtime.generation.api.TestClassGeneration;
import org.testgen.runtime.generation.javaparser.impl.JavaParserTestClassGeneration;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.complextypes.ProxyBluePrint;
import org.testgen.runtime.valuetracker.storage.ValueStorage;

public final class GenerationEntryPoint {

	private static final Logger LOGGER = LogManager.getLogger(GenerationEntryPoint.class);

	private GenerationEntryPoint() {
	}

	/**
	 * The starting point of the generation of this test case. The generation
	 * follows the API of {@link TestClassGeneration}.
	 * 
	 * @param <T>                  Type of ClassDeclaration
	 * @param <E>                  Type of a CodeBlock
	 * @param <S>                  Type of a single Expression
	 * @param testClass            class of the test method
	 * @param isStatic             marks the test method is static
	 * @param method               name of the test method
	 * @param methodParameterTypes list with a descriptor for each method
	 *                             parameter.<br>
	 *                             This list can contain two types of elements:<br>
	 *                             - {@link BasicType} for all normal types e.g.
	 *                             {@link String}<br>
	 *                             - {@link SignatureType} for generic types e.g.
	 *                             {@link Collection}
	 */
	public static <T, E, S> void generate(Class<?> testClass, boolean isStatic, String method,
			List<DescriptorType> methodParameterTypes) {
		LOGGER.info("Starting test-generation");
		TestgeneratorConfig.setFieldTracking(false);
		TestgeneratorConfig.setProxyTracking(false);
		boolean trackingActivated = TestgeneratorConfig.traceReadFieldAccess();

		TestClassGeneration<T, E, S> testGenerator = getTestClassGenerationImplementation();

		Path path = Paths.get(TestgeneratorConfig.getPathToTestclass());

		T compilationUnit = testGenerator.createTestClass(testClass, path);

		if (isStatic)
			testGenerator.prepareTestClass(testClass);

		else {
			BluePrint testObject = ValueStorage.getInstance().getTestObject();

			ClassData classData = GenerationHelper.getClassData(testObject.getReference());
			Set<FieldData> calledFields = trackingActivated
					? GenerationHelper.getCalledFields(testObject.getReference())
					: Collections.emptySet();

			testGenerator.prepareTestObject(compilationUnit, testObject, classData, calledFields);
		}

		testGenerator.prepareMethodParameters(compilationUnit, ValueStorage.getInstance().getMethodParameters(),
				methodParameterTypes);

		List<ProxyBluePrint> proxyObjects = ValueStorage.getInstance().getProxyObjects();

		boolean withProxyObjects = !proxyObjects.isEmpty();
		if (withProxyObjects) {
			testGenerator.prepareProxyObjects(compilationUnit, proxyObjects);
		}

		testGenerator.generateTestMethod(compilationUnit, method, isStatic, withProxyObjects);

		testGenerator.toFile(compilationUnit);

		ValueStorage.getInstance().popAndResetTestData();
	}

	@SuppressWarnings("unchecked")
	private static <T, E, S> TestClassGeneration<T, E, S> getTestClassGenerationImplementation() {
		String costumTestgeneratorClass = TestgeneratorConfig.getCustomTestgeneratorClass();

		if (costumTestgeneratorClass != null) {
			Class<?> costumTestgenerator = ReflectionUtil.forName(costumTestgeneratorClass);

			ReflectionUtil.checkForInterface(costumTestgenerator, TestClassGeneration.class);

			if (ReflectionUtil.getConstructor(costumTestgenerator) == null) {
				throw new IllegalArgumentException(
						costumTestgeneratorClass + "is a invalid implementation. Defaultconstructor is needed");
			}

			return (TestClassGeneration<T, E, S>) ReflectionUtil.newInstance(costumTestgenerator);
		}

		return (TestClassGeneration<T, E, S>) new JavaParserTestClassGeneration();
	}

}

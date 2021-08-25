package org.testgen.runtime.generation;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testgen.config.TestgeneratorConfig;
import org.testgen.core.ReflectionUtil;
import org.testgen.logging.LogManager;
import org.testgen.logging.Logger;
import org.testgen.runtime.classdata.model.ClassData;
import org.testgen.runtime.classdata.model.FieldData;
import org.testgen.runtime.classdata.model.descriptor.DescriptorType;
import org.testgen.runtime.generation.api.GenerationHelper;
import org.testgen.runtime.generation.api.TestClassGeneration;
import org.testgen.runtime.generation.javaparser.impl.JavaParserTestClassGeneration;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.ProxyBluePrint;
import org.testgen.runtime.valuetracker.storage.ValueStorage;

public final class Testgenerator {

	private static final Logger LOGGER = LogManager.getLogger(Testgenerator.class);

	private Testgenerator() {
	}

	/*
	 * Einstiegspunkt fuer die Generierung der Testklasse
	 * 
	 * @Param className Name der Klasse fuer die der Test generiert wird
	 * 
	 * @Param Name der Methode fuer die ein Testfall erstellt wird
	 */
	public static <T, E, S> void generate(Class<?> testClass, String method,
			List<DescriptorType> methodParameterTypes) {
		LOGGER.info("Starting test-generation");
		TestgeneratorConfig.setFieldTracking(false);
		TestgeneratorConfig.setProxyTracking(false);
		boolean trackingActivated = TestgeneratorConfig.traceReadFieldAccess();

		TestClassGeneration<T, E, S> testGenerator = getTestClassGenerationImplementation();

		T compilationUnit = testGenerator.createTestClass(testClass);

		BluePrint testObject = ValueStorage.getInstance().getTestObject();

		ClassData classData = GenerationHelper.getClassData(testObject.getReference());
		Set<FieldData> calledFields = trackingActivated ? GenerationHelper.getCalledFields(testObject.getReference())
				: Collections.emptySet();

		testGenerator.prepareTestObject(compilationUnit, testObject, classData, calledFields);

		testGenerator.prepareMethodParameters(compilationUnit, ValueStorage.getInstance().getMethodParameters(),
				methodParameterTypes);

		Map<ProxyBluePrint, List<BluePrint>> proxyObjects = ValueStorage.getInstance().getProxyObjects();

		boolean withProxyObjects = !proxyObjects.isEmpty();
		if (withProxyObjects) {
			testGenerator.prepareProxyObjects(compilationUnit, proxyObjects);
		}

		testGenerator.generateTestMethod(compilationUnit, method, withProxyObjects);

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

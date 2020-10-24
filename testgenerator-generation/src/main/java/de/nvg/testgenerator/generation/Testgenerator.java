package de.nvg.testgenerator.generation;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
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

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec.Builder;

import de.nvg.testgenerator.generation.impl.DefaultTestClassGeneration;
import de.nvg.testgenerator.generation.impl.TestGenerationHelper;
import de.nvg.valuetracker.blueprint.BluePrint;
import de.nvg.valuetracker.blueprint.ProxyBluePrint;
import de.nvg.valuetracker.storage.ValueStorage;

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
	public static void generate(Class<?> testClass, String method, List<DescriptorType> methodParameterTypes) {
		LOGGER.info("Starting test-generation");
		TestgeneratorConfig.setFieldTracking(false);
		TestgeneratorConfig.setProxyTracking(false);
		boolean trackingActivated = TestgeneratorConfig.traceReadFieldAccess();

		TestClassGeneration testGenerator = getTestClassGenerationImplementation();

		Builder classBuilder = testGenerator.createTestClass(testClass);

		BluePrint testObject = ValueStorage.getInstance().getTestObject();

		ClassData classData = TestGenerationHelper.getClassData(testObject.getReference());
		Set<FieldData> calledFields = trackingActivated
				? TestGenerationHelper.getCalledFields(testObject.getReference())
				: Collections.emptySet();

		testGenerator.prepareTestObject(classBuilder, testObject, classData, calledFields);

		testGenerator.prepareMethodParameters(classBuilder, ValueStorage.getInstance().getMethodParameters(),
				methodParameterTypes);

		Map<ProxyBluePrint, List<BluePrint>> proxyObjects = ValueStorage.getInstance().getProxyObjects();

		boolean withProxyObjects = !proxyObjects.isEmpty();
		if (withProxyObjects) {
			testGenerator.prepareProxyObjects(classBuilder, proxyObjects);
		}

		testGenerator.generateTestMethod(classBuilder, method, withProxyObjects);

		classBuilder.addJavadoc(
				"Test generated at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))
						+ " with Testgenerator-" + Testgenerator.class.getPackage().getImplementationVersion());

		JavaFile file = JavaFile.builder(testClass.getPackage().getName(), classBuilder.build())
				.skipJavaLangImports(true).build();
		LOGGER.debug("generated Test", stream -> {
			try {
				file.writeTo(stream);
			} catch (IOException e) {
				LOGGER.error(e);
			}
		});

		ValueStorage.getInstance().popAndResetTestData();
	}

	private static TestClassGeneration getTestClassGenerationImplementation() {
		String costumTestgeneratorClass = TestgeneratorConfig.getCustomTestgeneratorClass();

		if (costumTestgeneratorClass != null) {
			Class<?> costumTestgenerator = ReflectionUtil.forName(costumTestgeneratorClass);
			if (!Arrays.stream(costumTestgenerator.getInterfaces())
					.anyMatch(i -> TestClassGeneration.class.isAssignableFrom(i))) {
				throw new IllegalArgumentException(
						costumTestgenerator + "is a invalid implementation for " + TestClassGeneration.class);
			}

			if (ReflectionUtil.getConstructor(costumTestgenerator) == null) {
				throw new IllegalArgumentException(
						costumTestgeneratorClass + "is a invalid implementation. Defaultconstructor is needed");
			}

			return (TestClassGeneration) ReflectionUtil.newInstance(costumTestgenerator);
		}

		return new DefaultTestClassGeneration();
	}

}

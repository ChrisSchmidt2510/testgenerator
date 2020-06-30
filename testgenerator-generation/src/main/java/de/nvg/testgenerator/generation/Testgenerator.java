package de.nvg.testgenerator.generation;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Modifier;

import org.testgen.core.logging.LogManager;
import org.testgen.core.logging.Logger;
import org.testgen.core.properties.RuntimeProperties;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

import de.nvg.runtime.classdatamodel.ClassData;
import de.nvg.runtime.classdatamodel.FieldData;
import de.nvg.runtime.classdatamodel.SignatureData;
import de.nvg.testgenerator.generation.impl.DefaultTestClassGeneration;
import de.nvg.testgenerator.generation.impl.TestGenerationHelper;
import de.nvg.valuetracker.blueprint.BluePrint;
import de.nvg.valuetracker.storage.ValueStorage;

public final class Testgenerator {
	private static final String TEST = "Test";

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
	public static void generate(String className, String method) {
		LOGGER.info("Starting test-generation");
		RuntimeProperties.getInstance().setFieldTracking(false);
		RuntimeProperties.getInstance().setProxyTracking(false);
		boolean trackingActivated = RuntimeProperties.getInstance().wasFieldTrackingActivated();

		TestClassGeneration testGenerator = new DefaultTestClassGeneration();

		Builder classBuilder = TypeSpec.classBuilder(getClassNameWithoutPackage(className) + TEST)
				.addModifiers(Modifier.PUBLIC);

		BluePrint testObject = ValueStorage.getInstance().getTestObject();

		ClassData classData = TestGenerationHelper.getClassData(testObject.getReference());
		Set<FieldData> calledFields = trackingActivated
				? TestGenerationHelper.getCalledFields(testObject.getReference())
				: Collections.emptySet();

		testGenerator.prepareTestObject(classBuilder, testObject, classData, calledFields);

		Map<Integer, SignatureData> methodSignature = TestGenerationHelper
				.getMethodSignature(testObject.getReference());
		testGenerator.prepareMethodParameters(classBuilder, ValueStorage.getInstance().getMethodParameters(),
				methodSignature);

		Collection<BluePrint> proxyObjects = ValueStorage.getInstance().getProxyObjects();

		boolean withProxyObjects = !proxyObjects.isEmpty();
		if (withProxyObjects) {
			testGenerator.prepareProxyObjects(classBuilder, proxyObjects);
		}

		testGenerator.generateTestMethod(classBuilder, method, withProxyObjects);

		classBuilder.addJavadoc(
				"Test generated at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))
						+ " with Testgenerator-" + Testgenerator.class.getPackage().getImplementationVersion());

		JavaFile file = JavaFile.builder(getPackageWithoutClassname(className), classBuilder.build())
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

	private static String getClassNameWithoutPackage(String className) {
		return className.substring(className.lastIndexOf('/') + 1);
	}

	private static String getPackageWithoutClassname(String className) {
		return className.substring(0, className.lastIndexOf('/')).replace('/', '.');
	}

}

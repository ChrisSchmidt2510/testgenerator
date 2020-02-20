package de.nvg.testgenerator.generation;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

import de.nvg.runtime.classdatamodel.ClassData;
import de.nvg.runtime.classdatamodel.FieldData;
import de.nvg.testgenerator.generation.impl.DefaultTestClassGeneration;
import de.nvg.testgenerator.generation.impl.TestGenerationHelper;
import de.nvg.testgenerator.logging.LogManager;
import de.nvg.testgenerator.logging.Logger;
import de.nvg.testgenerator.properties.RuntimeProperties;
import de.nvg.valuetracker.blueprint.BluePrint;
import de.nvg.valuetracker.storage.ValueStorage;

public class Testgenerator {
	private static final String TEST = "Test";

	private static final Logger LOGGER = LogManager.getLogger(Testgenerator.class);

	/*
	 * Einstiegspunkt für die Generierung der Testklasse
	 * 
	 * @Param className Name der Klasse für die der Test generiert wird
	 * 
	 * @Param Name der Methode für die ein Testfall erstellt wird
	 */
	public static void generate(String className, String method) throws IOException {
		LOGGER.info("Starting test-generation");
		RuntimeProperties.getInstance().setActivateTracking(false);

		TestClassGeneration testGenerator = new DefaultTestClassGeneration();

		Builder classBuilder = TypeSpec.classBuilder(getClassNameWithoutPackage(className) + TEST)
				.addModifiers(Modifier.PUBLIC);

		BluePrint testObject = ValueStorage.getInstance().getTestObject();
		ClassData classData = TestGenerationHelper.getClassData(testObject.getReference());
		Set<FieldData> calledFields = TestGenerationHelper.getCalledFields(testObject.getReference());

		testGenerator.prepareTestObject(classBuilder, testObject, classData, calledFields);
		testGenerator.prepareMethodParameters(classBuilder, ValueStorage.getInstance().getMethodParameters());
		testGenerator.generateTestMethod(classBuilder, method);

		classBuilder.addJavadoc(
				"Test generated at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))
						+ " with Testgenerator-1.0.0");

		JavaFile file = JavaFile.builder(getPackageWithoutClassname(className), classBuilder.build()).build();
		file.writeTo(System.out);

	}

	private static String getClassNameWithoutPackage(String className) {
		return className.substring(className.lastIndexOf("/") + 1);
	}

	private static String getPackageWithoutClassname(String className) {
		return className.substring(0, className.lastIndexOf("/")).replace('/', '.');
	}

}

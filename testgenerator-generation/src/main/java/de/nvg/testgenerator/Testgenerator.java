package de.nvg.testgenerator;

import java.util.Set;

import com.squareup.javapoet.JavaFile;

import de.nvg.runtime.classdatamodel.ClassData;
import de.nvg.runtime.classdatamodel.FieldData;
import de.nvg.testgenerator.properties.RuntimeProperties;
import de.nvg.valuetracker.blueprint.BluePrint;
import de.nvg.valuetracker.blueprint.ComplexBluePrint;
import de.nvg.valuetracker.storage.ValueStorage;

public class Testgenerator {
	private static final String FIELD_NAME_CLASS_DATA = "classData";
	private static final String FIELD_NAME_CALLED_FIELDS = "calledFields";

	/*
	 * Einstiegspunkt für die Generierung der Testklasse
	 * 
	 * @Param className Name der Klasse für die der Test generiert wird
	 * 
	 * @Param Name der Methode für die ein Testfall erstellt wird
	 */
	public static void generate(String className, String method) {
		RuntimeProperties.getInstance().setActivateTracking(false);

		generateJavaFile(className, method);
		System.out.println("BluePrints: ");
		for (BluePrint bluePrint : ValueStorage.getInstance().getBluePrints()) {

			generateBluePrint(bluePrint);

//			System.out.println("BluePrint");
//			System.out.println(bluePrint.toString());
		}
	}

	private static void generateJavaFile(String className, String method) {
		System.out.println(className);
		JavaFile javaFile = JavaFile.builder("", null).build();
	}

	private static void generateBluePrint(BluePrint bluePrint) {
		if (bluePrint.isComplexType()) {
			generateComplexBluePrint(bluePrint);
		}
	}

	private static void generateComplexBluePrint(BluePrint complexBluePrint) {
		for (BluePrint bluePrint : complexBluePrint.getPreExecuteBluePrints()) {
			if (!bluePrint.isBuild()) {
				generateBluePrint(bluePrint);
			}
		}

		if (complexBluePrint instanceof ComplexBluePrint) {

			Object reference = complexBluePrint.getReference();
			ClassData classData = getClassData(reference);
			Set<FieldData> calledFields = getCalledFields(reference);
			System.out.println("Name:" + complexBluePrint.getName());
			System.out.println("calledFields: " + calledFields);
			System.out.println("classData: " + classData);
		}
	}

	private static ClassData getClassData(Object reference) {

		return MethodHandles.getStaticFieldValue(reference.getClass(), FIELD_NAME_CLASS_DATA);
	}

	private static Set<FieldData> getCalledFields(Object reference) {
		return MethodHandles.getFieldValue(reference, FIELD_NAME_CALLED_FIELDS);
	}

}

package de.nvg.testgenerator;

import java.util.Set;

import de.nvg.runtime.classdatamodel.ClassData;
import de.nvg.runtime.classdatamodel.FieldData;
import de.nvg.valuetracker.blueprint.BluePrint;
import de.nvg.valuetracker.blueprint.ComplexBluePrint;
import de.nvg.valuetracker.storage.ValueStorage;

public class Testgenerator {
	private static final String FIELD_NAME_CLASS_DATA = "classData";
	private static final String FIELD_NAME_CALLED_FIELDS = "calledFields";

	public static void generate() {
		RuntimeProperties.getInstance().setActivateTracking(false);
		System.out.println("BluePrints: ");
		for (BluePrint bluePrint : ValueStorage.getInstance().getBluePrints()) {

			generateBluePrint(bluePrint);

//			System.out.println("BluePrint");
//			System.out.println(bluePrint.toString());
		}
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

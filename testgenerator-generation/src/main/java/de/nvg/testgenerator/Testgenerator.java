package de.nvg.testgenerator;

import de.nvg.valuetracker.blueprint.BluePrint;
import de.nvg.valuetracker.storage.ValueStorage;

public class Testgenerator {

	public static void generate() {
		RuntimeProperties.getInstance().setActivateTracking(false);
		System.out.println("BluePrints: ");
		for (BluePrint bluePrint : ValueStorage.getInstance().getBluePrints()) {

//			Object reference = bluePrint.getReference();
			System.out.println(bluePrint.toString());
		}

//		System.out.println(" \nClassData: ");
//		for (Entry<String, ClassData> entry : metaData.getClassDataMap().entrySet()) {
//
//			System.out.println(entry.getKey());
//			if (entry.getValue().getSuperClass() != null) {
//				System.out.println("Superclass: " + entry.getValue().getSuperClass().getName());
//			}
//
//			if (entry.getValue().hasDefaultConstructor()) {
//				System.out.println("Default Constructor");
//			} else if (entry.getValue().isEnum()) {
//				System.out.println("Enum");
//			} else {
//				for (Entry<Integer, FieldData> constructor : entry.getValue().getConstructorInitalizedFields()
//						.entrySet()) {
//					System.out.println("Argumentindex: " + constructor.getKey() + " Field: " + constructor.getValue());
//				}
//			}
//
//			System.out.println("");
//
//			for (Entry<FieldData, List<MethodData>> fieldEntry : entry.getValue().getFieldsUsedInMethods().entrySet()) {
//				System.out.println("Field:" + fieldEntry.getKey());
//
//				System.out.println("Methods: ");
//				for (MethodData methodData : fieldEntry.getValue()) {
//					System.out.println(methodData);
//				}
//				System.out.println("");
//
//			}
//		}

//		System.out.println("CalledFields: ");
	}

}

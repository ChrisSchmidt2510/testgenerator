package de.nvg.testgenerator.generation.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.CodeBlock.Builder;

import de.nvg.runtime.classdatamodel.ClassData;
import de.nvg.runtime.classdatamodel.ConstructorData;
import de.nvg.runtime.classdatamodel.FieldData;
import de.nvg.runtime.classdatamodel.SetterMethodData;
import de.nvg.runtime.classdatamodel.SetterType;
import de.nvg.testgenerator.generation.CollectionsGeneration;
import de.nvg.testgenerator.generation.ComplexObjectGeneration;
import de.nvg.testgenerator.logging.LogManager;
import de.nvg.testgenerator.logging.Logger;
import de.nvg.valuetracker.blueprint.BluePrint;
import de.nvg.valuetracker.blueprint.ComplexBluePrint;
import de.nvg.valuetracker.blueprint.SimpleBluePrint;

public class DefaultComplexObjectGeneration implements ComplexObjectGeneration {

	private static final Logger LOGGER = LogManager.getLogger(DefaultComplexObjectGeneration.class);

	private CollectionsGeneration collectionsGeneration;

	@Override
	public void setCollectionsGeneration(CollectionsGeneration collectionsGeneration) {
		this.collectionsGeneration = collectionsGeneration;
	}

	@Override
	public void createObject(Builder code, ComplexBluePrint bluePrint, boolean isField, ClassData classData,
			Set<FieldData> calledFields) {

		LOGGER.info("generating Complex-Object: " + bluePrint);

		createComplexTypesOfObject(code, bluePrint, classData, calledFields);

		if (classData.hasDefaultConstructor()) {

			if (isField) {
				code.addStatement(bluePrint.getName() + " = new $T()", bluePrint.getReference().getClass());
			} else {
				Class<?> type = bluePrint.getReference().getClass();
				code.addStatement("$T " + bluePrint.getName() + " = new $T()", type, type);
			}

		} else {
			StringBuilder statement = new StringBuilder();
			List<Class<?>> types = new ArrayList<>();

			statement.append(isField ? bluePrint.getName() : ("$T " + bluePrint.getName()) + " = new $T(");

			Class<? extends Object> referenceClass = bluePrint.getReference().getClass();
			types.add(referenceClass);
			types.add(referenceClass);

			ConstructorData constructor = classData.getConstructor();
			Set<Entry<Integer, FieldData>> constructorFields = constructor.getConstructorFieldIndex().entrySet();
			int index = 0;

			for (Entry<Integer, FieldData> constructorField : constructorFields) {
				index += 1;

				calledFields.remove(constructorField.getValue());

				BluePrint constructorFieldBp = bluePrint.getBluePrintForName(constructorField.getValue().getName());

				if (constructorFieldBp.isComplexBluePrint()) {
					createComplexObject(code, constructorFieldBp);

					statement.append(index == constructorFields.size() ? constructorFieldBp.getName() + ")"
							: constructorFieldBp.getName() + ",");

					types.add(constructorFieldBp.getReference().getClass());

				} else if (constructorFieldBp.isSimpleBluePrint()) {
					SimpleBluePrint<?> simpleBluePrint = constructorFieldBp.castToSimpleBluePrint();

					statement.append(index == constructorFields.size() ? simpleBluePrint.valueCreation() + ")"
							: simpleBluePrint.valueCreation() + ",");

					types.addAll(simpleBluePrint.getReferenceClasses());
				} else if (constructorFieldBp.isCollectionBluePrint()) {
					collectionsGeneration.createCollection(code, constructorFieldBp.castToCollectionBluePrint(), false,
							false);

					statement.append(index == constructorFields.size() ? constructorFieldBp.getName() + ")"
							: constructorFieldBp.getName() + ",");
				}
			}

			code.addStatement(statement.toString(), types.toArray());
		}

		addCalledFieldsToObject(code, bluePrint, classData, calledFields, bluePrint.getName());

		code.add("\n");

		bluePrint.setBuild();

	}

	private void addCalledFieldsToObject(CodeBlock.Builder code, ComplexBluePrint bluePrint, ClassData classData,
			Set<FieldData> calledFields, String objectName) {
		for (FieldData field : calledFields) {
			LOGGER.info("add Field " + field + " to Object " + objectName);

			BluePrint bpField = bluePrint.getBluePrintForName(field.getName());

			SetterMethodData setter = classData.getSetterMethodData(field);

			if (bpField.isComplexBluePrint()) {
				code.addStatement(objectName + "." + setter.getName() + "(" + bpField.getName() + ")");
			} else if (bpField.isSimpleBluePrint()) {
				SimpleBluePrint<?> simpleBluePrint = bpField.castToSimpleBluePrint();

				code.addStatement(objectName + "." + setter.getName() + "(" + simpleBluePrint.valueCreation() + ")",
						simpleBluePrint.getReferenceClasses().toArray());
			} else if (bpField.isCollectionBluePrint()) {
				collectionsGeneration.addCollectionToObject(code, bpField.castToCollectionBluePrint(), //
						setter, objectName);
			}
		}
	}

	private void createComplexTypesOfObject(Builder code, ComplexBluePrint bluePrint, ClassData classData,
			Set<FieldData> calledFields) {
		for (BluePrint bp : bluePrint.getPreExecuteBluePrints()) {
			Optional<FieldData> calledField = calledFields.stream()
					.filter(field -> field.getName().equals(bp.getName())).findAny();

			if (calledField.isPresent()) {
				if (bp.isComplexBluePrint()) {
					createComplexObject(code, bp);

				} else if (bp.isCollectionBluePrint()) {
					SetterMethodData setter = classData.getSetterMethodData(calledField.get());

					collectionsGeneration.createCollection(code, bp.castToCollectionBluePrint(),
							SetterType.COLLECTION_SETTER == setter.getType(), false);
				}
			}
		}
	}

	private void createComplexObject(CodeBlock.Builder code, BluePrint bluePrint) {
		if (bluePrint.isNotBuild()) {
			Set<FieldData> calledFields = TestGenerationHelper.getCalledFields(bluePrint.getReference());
			ClassData classData = TestGenerationHelper.getClassData(bluePrint.getReference());

			createObject(code, bluePrint.castToComplexBluePrint(), false, classData, calledFields);
		}
	}

}

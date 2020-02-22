package de.nvg.testgenerator.generation.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import de.nvg.testgenerator.properties.RuntimeProperties;
import de.nvg.valuetracker.blueprint.BasicCollectionBluePrint;
import de.nvg.valuetracker.blueprint.BluePrint;
import de.nvg.valuetracker.blueprint.ComplexBluePrint;
import de.nvg.valuetracker.blueprint.SimpleBluePrint;

public class DefaultComplexObjectGeneration implements ComplexObjectGeneration {

	private static final Logger LOGGER = LogManager.getLogger(DefaultComplexObjectGeneration.class);

	private RuntimeProperties properties = RuntimeProperties.getInstance();

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

		} else if (classData.getConstructor() != null) {
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
		} else {
			code.addStatement("//no constructor found for class: " + bluePrint.getReference().getClass().getName());
			code.addStatement("$T = null", bluePrint.getReference().getClass());

		}

		addFieldsToObject(code, bluePrint, classData, calledFields, bluePrint.getName());

		code.add("\n");

		bluePrint.setBuild();

	}

	private void addFieldsToObject(CodeBlock.Builder code, ComplexBluePrint bluePrint, ClassData classData,
			Set<FieldData> calledFields, String objectName) {
		if (properties.wasFieldTrackingActivated()) {
			for (FieldData field : calledFields) {
				LOGGER.info("add Field " + field + " to Object " + objectName);

				BluePrint bpField = bluePrint.getBluePrintForName(field.getName());

				SetterMethodData setter = classData.getSetterMethodData(field);

				if (setter == null && classData.getSuperclass() != null) {
					setter = classData.getSuperclass().getSetterMethodData(field);
				}

				addFieldToObject(code, bpField, setter, objectName);
			}
		} else {
			for (BluePrint child : bluePrint.getChildBluePrints()) {

				Class<?> descriptor = child.isCollectionBluePrint()
						? child.castToCollectionBluePrint().getInterfaceClass()
						: child.getReference().getClass();

				SetterMethodData setter = classData.getSetterMethodData(child.getName(), descriptor);

				if (setter == null && classData.getSuperclass() != null) {
					setter = classData.getSuperclass().getSetterMethodData(child.getName(), descriptor);
				}

				addFieldToObject(code, child, setter, objectName);
			}
		}
	}

	private void addFieldToObject(CodeBlock.Builder code, BluePrint bluePrint, SetterMethodData setter,
			String objectName) {
		if (setter == null) {
			StringBuilder statement = new StringBuilder();
			statement.append("//no setter found for Field: " + bluePrint.getName() + " Value: ");

			List<Class<?>> referenceClasses = getFieldValue(bluePrint, statement);

			code.addStatement(statement.toString(), referenceClasses.toArray());
		} else {
			if (bluePrint.isComplexBluePrint()) {
				code.addStatement(objectName + "." + setter.getName() + "(" + bluePrint.getName() + ")");

			} else if (bluePrint.isSimpleBluePrint()) {
				SimpleBluePrint<?> simpleBluePrint = bluePrint.castToSimpleBluePrint();

				code.addStatement(objectName + "." + setter.getName() + "(" + simpleBluePrint.valueCreation() + ")",
						simpleBluePrint.getReferenceClasses().toArray());
			} else if (bluePrint.isCollectionBluePrint()) {
				collectionsGeneration.addCollectionToObject(code, bluePrint.castToCollectionBluePrint(), //
						setter, objectName);
			}
		}
	}

	private List<Class<?>> getFieldValue(BluePrint bluePrint, StringBuilder statement) {
		if (bluePrint.isComplexType()) {
			statement.append(bluePrint.getName());

			return Arrays.asList(bluePrint.getReference().getClass());
		} else if (bluePrint.isSimpleBluePrint()) {
			SimpleBluePrint<?> simpleBluePrint = bluePrint.castToSimpleBluePrint();

			statement.append(simpleBluePrint.valueCreation());
			return simpleBluePrint.getReferenceClasses();
		}

		throw new IllegalArgumentException("No valid BluePrint" + bluePrint);
	}

	private void createComplexTypesOfObject(Builder code, ComplexBluePrint bluePrint, ClassData classData,
			Set<FieldData> calledFields) {
		for (BluePrint bp : bluePrint.getPreExecuteBluePrints()) {
			Optional<FieldData> calledField = calledFields.stream()
					.filter(field -> field.getName().equals(bp.getName())).findAny();

			if (calledField.isPresent() || !properties.wasFieldTrackingActivated()) {
				if (bp.isComplexBluePrint()) {
					createComplexObject(code, bp);

				} else if (bp.isCollectionBluePrint()) {
					BasicCollectionBluePrint<?> collection = bp.castToCollectionBluePrint();

					SetterMethodData setter = null;
					if (properties.wasFieldTrackingActivated()) {
						setter = classData.getSetterMethodData(calledField.get());
					} else {
						setter = classData.getSetterMethodData(
								new FieldData(collection.getName(), collection.getInterfaceClass().getName()));
					}

					collectionsGeneration.createCollection(code, collection,
							SetterType.COLLECTION_SETTER == setter.getType(), false);
				}
			}
		}
	}

	private void createComplexObject(CodeBlock.Builder code, BluePrint bluePrint) {
		if (bluePrint.isNotBuild()) {
			ClassData classData = TestGenerationHelper.getClassData(bluePrint.getReference());

			Set<FieldData> calledFields = Collections.emptySet();
			if (properties.wasFieldTrackingActivated()) {
				calledFields = TestGenerationHelper.getCalledFields(bluePrint.getReference());
			}

			createObject(code, bluePrint.castToComplexBluePrint(), false, classData, calledFields);
		}
	}

}
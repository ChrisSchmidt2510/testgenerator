package de.nvg.testgenerator.generation.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.testgen.core.logging.LogManager;
import org.testgen.core.logging.Logger;
import org.testgen.core.properties.RuntimeProperties;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.CodeBlock.Builder;

import de.nvg.runtime.classdatamodel.ClassData;
import de.nvg.runtime.classdatamodel.ConstructorData;
import de.nvg.runtime.classdatamodel.FieldData;
import de.nvg.runtime.classdatamodel.SetterMethodData;
import de.nvg.runtime.classdatamodel.SetterType;
import de.nvg.runtime.classdatamodel.SignatureData;
import de.nvg.testgenerator.generation.ComplexObjectGeneration;
import de.nvg.testgenerator.generation.ContainerGeneration;
import de.nvg.valuetracker.blueprint.AbstractBasicCollectionBluePrint;
import de.nvg.valuetracker.blueprint.ArrayBluePrint;
import de.nvg.valuetracker.blueprint.BluePrint;
import de.nvg.valuetracker.blueprint.ComplexBluePrint;
import de.nvg.valuetracker.blueprint.SimpleBluePrint;

public class DefaultComplexObjectGeneration implements ComplexObjectGeneration {

	private static final Logger LOGGER = LogManager.getLogger(DefaultComplexObjectGeneration.class);

	private RuntimeProperties properties = RuntimeProperties.getInstance();

	private ContainerGeneration containerGeneration;

	@Override
	public void setContainerGeneration(ContainerGeneration containerGeneration) {
		this.containerGeneration = containerGeneration;
	}

	@Override
	public void createObject(Builder code, ComplexBluePrint bluePrint, boolean isField, ClassData classData,
			Set<FieldData> calledFields) {

		LOGGER.info("generating Complex-Object: " + bluePrint);

		Set<BluePrint> usedBluePrints = new HashSet<>();

		createComplexTypesOfObject(code, bluePrint, classData, calledFields);

		List<Class<?>> types = new ArrayList<>();
		Class<?> type = bluePrint.getReference().getClass();

		StringBuilder creation = new StringBuilder();
		if (isField) {
			creation.append(bluePrint.getName());
		} else {
			creation.append("$T " + bluePrint.getName());
			types.add(type);
		}

		if (classData.isInnerClass()) {
			creation.append(" = " + getBluePrintForClassData(bluePrint, classData.getOuterClass()) + ".new "
					+ classData.getInnerClassName());
		} else {
			creation.append(" = new $T");
			types.add(type);
		}

		if (classData.hasDefaultConstructor()) {
			creation.append("()");

			code.addStatement(creation.toString(), types.toArray());
		} else if (classData.getConstructor().isNotEmpty()) {
			creation.append("(");

			ConstructorData constructor = classData.getConstructor();
			Set<Entry<Integer, FieldData>> constructorFields = constructor.getConstructorFields().entrySet();
			int index = 0;

			for (Entry<Integer, FieldData> constructorField : constructorFields) {
				index += 1;

				calledFields.remove(constructorField.getValue());

				BluePrint constructorFieldBp = bluePrint.getBluePrintForName(constructorField.getValue().getName());
				usedBluePrints.add(constructorFieldBp);

				if (constructorFieldBp.isComplexBluePrint()) {
					createComplexObject(code, constructorFieldBp);

					creation.append(index == constructorFields.size() ? (constructorFieldBp.getName() + ")")
							: (constructorFieldBp.getName() + ","));

					types.add(constructorFieldBp.getReference().getClass());

				} else if (constructorFieldBp.isSimpleBluePrint()) {
					SimpleBluePrint<?> simpleBluePrint = constructorFieldBp.castToSimpleBluePrint();

					creation.append(index == constructorFields.size() ? (simpleBluePrint.valueCreation() + ")")
							: (simpleBluePrint.valueCreation() + ","));

					types.addAll(simpleBluePrint.getReferenceClasses());
				} else if (constructorFieldBp.isCollectionBluePrint()) {

					containerGeneration.createCollection(code, constructorFieldBp.castToCollectionBluePrint(),
							constructorField.getValue().getSignature(), false, false);

					creation.append(index == constructorFields.size() ? (constructorFieldBp.getName() + ")")
							: (constructorFieldBp.getName() + ","));
				} else if (constructorFieldBp.isArrayBluePrint()) {

					containerGeneration.createArray(code, constructorFieldBp.castToArrayBluePrint(), //
							false, false);

					creation.append(index == constructorFields.size() ? (constructorFieldBp.getName() + ")")
							: (constructorFieldBp.getName() + ","));
				}
			}

			code.addStatement(creation.toString(), types.toArray());
		} else {
			code.addStatement("//no public constructor found for class: " + bluePrint.getClassNameOfReference());

			if (isField) {
				code.addStatement(bluePrint.getName() + " = null");
			} else {
				code.addStatement("$T " + bluePrint.getName() + " = null", bluePrint.getReference().getClass());
			}

		}

		addFieldsToObject(code, bluePrint, classData, calledFields, bluePrint.getName(), usedBluePrints);

		code.add("\n");

		bluePrint.setBuild();

	}

	private void addFieldsToObject(CodeBlock.Builder code, ComplexBluePrint bluePrint, ClassData classData,
			Set<FieldData> calledFields, String objectName, Set<BluePrint> usedBluePrints) {

		if (properties.wasFieldTrackingActivated()) {

			for (FieldData field : calledFields) {
				LOGGER.info("add Field " + field + " to Object " + objectName);

				BluePrint bpField = bluePrint.getBluePrintForName(field.getName());

				FieldData originalField = classData.getFieldInHierarchie(field);

				if (originalField.isPublic()) {
					addPublicFieldToObject(code, bpField, originalField, objectName);
				} else {
					SetterMethodData setter = classData.getSetterInHierarchie(field);

					addFieldToObject(code, bpField, setter, objectName);
				}
			}

		} else {
			for (BluePrint child : bluePrint.getChildBluePrints()) {

				if (!usedBluePrints.contains(child)) {

					FieldData field = child.isCollectionBluePrint()
							? classData.getCollectionFieldInHierarchie(child.getName())
							: classData.getFieldInHierarchie(child.getName(), child.getReference().getClass());

					LOGGER.info("add Field " + field + " to Object " + objectName);

					if (field.isPublic()) {
						addPublicFieldToObject(code, child, field, objectName);

					} else {
						SetterMethodData setter = classData.getSetterInHierarchie(field);
						addFieldToObject(code, child, setter, objectName);
					}
				}
			}
		}
	}

	private void addPublicFieldToObject(CodeBlock.Builder code, BluePrint bluePrint, FieldData field,
			String objectName) {
		if (bluePrint.isComplexBluePrint()) {
			code.addStatement(objectName + "." + field.getName() + "=" + bluePrint.getName());

		} else if (bluePrint.isSimpleBluePrint()) {
			SimpleBluePrint<?> simpleBluePrint = bluePrint.castToSimpleBluePrint();

			code.addStatement(objectName + "." + field.getName() + "=" + simpleBluePrint.valueCreation(),
					simpleBluePrint.getReferenceClasses().toArray());

		} else if (bluePrint.isContainerBluePrint()) {
			containerGeneration.addContainerToObject(code, bluePrint, field, objectName);
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
			} else if (bluePrint.isContainerBluePrint()) {
				containerGeneration.addContainerToObject(code, bluePrint, setter, objectName);
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

			if (calledField.isPresent() || !properties.wasFieldTrackingActivated() || //
					(classData.isInnerClass()
							&& classData.getOuterClass().getName().equals(bp.getClassNameOfReference()))) {
				if (bp.isComplexBluePrint()) {
					createComplexObject(code, bp);

				} else if (bp.isCollectionBluePrint()) {
					AbstractBasicCollectionBluePrint<?> collection = bp.castToCollectionBluePrint();

					SetterMethodData setter = null;
					SignatureData signature = null;
					if (properties.wasFieldTrackingActivated()) {
						FieldData field = classData.getFieldInHierarchie(calledField.get());
						signature = field.getSignature();
						setter = classData.getSetterInHierarchie(field);

					} else {
						FieldData field = classData.getCollectionFieldInHierarchie(collection.getName());
						signature = field.getSignature();
						setter = classData.getSetterInHierarchie(field);
					}

					containerGeneration.createCollection(code, collection, signature,
							setter != null && SetterType.COLLECTION_SETTER == setter.getType(), false);
				} else if (bp.isArrayBluePrint()) {
					ArrayBluePrint arrayBluePrint = bp.castToArrayBluePrint();

					FieldData field = classData.getFieldInHierarchie(arrayBluePrint.getName(),
							arrayBluePrint.getType());
					SetterMethodData setter = classData.getSetterInHierarchie(field);

					containerGeneration.createArray(code, arrayBluePrint,
							setter != null && SetterType.VALUE_GETTER == setter.getType(), false);
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

	private String getBluePrintForClassData(ComplexBluePrint parent, ClassData outerClass) {
		return parent.getChildBluePrints().stream()
				.filter(bp -> outerClass.getName().equals(bp.getClassNameOfReference())).map(BluePrint::getName)
				.findFirst().orElse(null);
	}

}

package org.testgen.runtime.generation.javapoet.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.testgen.config.TestgeneratorConfig;
import org.testgen.logging.LogManager;
import org.testgen.logging.Logger;
import org.testgen.runtime.classdata.model.ClassData;
import org.testgen.runtime.classdata.model.ConstructorData;
import org.testgen.runtime.classdata.model.FieldData;
import org.testgen.runtime.classdata.model.SetterMethodData;
import org.testgen.runtime.classdata.model.SetterType;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;
import org.testgen.runtime.generation.ArrayGeneration;
import org.testgen.runtime.generation.CollectionGeneration;
import org.testgen.runtime.generation.ComplexObjectGeneration;
import org.testgen.runtime.generation.naming.impl.DefaultNamingService;
import org.testgen.runtime.valuetracker.blueprint.AbstractBasicCollectionBluePrint;
import org.testgen.runtime.valuetracker.blueprint.ArrayBluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.ComplexBluePrint;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.CodeBlock.Builder;
import com.squareup.javapoet.TypeSpec;

public class DefaultComplexObjectGeneration implements ComplexObjectGeneration<TypeSpec.Builder, Builder> {

	private static final Logger LOGGER = LogManager.getLogger(DefaultComplexObjectGeneration.class);

	private CollectionGeneration<TypeSpec.Builder, Builder> collectionGeneration = getCollectionGeneration();

	private ArrayGeneration<TypeSpec.Builder, Builder> arrayGeneration = getArrayGeneration();

	private DefaultNamingService namingService;

	@Override
	public void createObject(Builder code, ComplexBluePrint bluePrint, boolean isField, ClassData classData,
			Set<FieldData> calledFields) {

		if (bluePrint.isNotBuild()) {
			LOGGER.info("generating Complex-Object: " + bluePrint);

			Set<BluePrint> usedBluePrints = new HashSet<>();

			createComplexTypesOfObject(code, bluePrint, classData, calledFields);

			List<Class<?>> types = new ArrayList<>();
			Class<?> type = bluePrint.getReference().getClass();

			StringBuilder creation = new StringBuilder();
			if (isField) {
				creation.append(namingService.getName(bluePrint));
			} else {
				creation.append("$T " + namingService.getName(bluePrint));
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
					String argumentName = namingService.getName(constructorFieldBp);

					usedBluePrints.add(constructorFieldBp);

					if (constructorFieldBp.isComplexBluePrint()) {
						createComplexObject(code, constructorFieldBp);

						creation.append(
								index == constructorFields.size() ? (argumentName + ")") : (argumentName + ","));

					} else if (constructorFieldBp.isSimpleBluePrint()) {
						SimpleBluePrint<?> simpleBluePrint = constructorFieldBp.castToSimpleBluePrint();

						creation.append(index == constructorFields.size() ? (simpleBluePrint.valueCreation() + ")")
								: (simpleBluePrint.valueCreation() + ","));

						types.addAll(simpleBluePrint.getReferenceClasses());
					} else if (constructorFieldBp.isCollectionBluePrint()) {

						collectionGeneration.createCollection(code, constructorFieldBp.castToCollectionBluePrint(),
								constructorField.getValue().getSignature(), false, false);

						creation.append(
								index == constructorFields.size() ? (argumentName + ")") : (argumentName + ","));
					} else if (constructorFieldBp.isArrayBluePrint()) {

						arrayGeneration.createArray(code, constructorFieldBp.castToArrayBluePrint(), //
								false, false);

						creation.append(
								index == constructorFields.size() ? (argumentName + ")") : (argumentName + ","));
					}
				}

				code.addStatement(creation.toString(), types.toArray());
			} else {
				code.addStatement("//TODO add initalization for class: " + bluePrint.getClassNameOfReference());

				if (isField)
					code.addStatement(namingService.getName(bluePrint) + " = null");
				else
					code.addStatement("$T " + namingService.getName(bluePrint) + " = null",
							bluePrint.getReference().getClass());

			}

			addFieldsToObject(code, bluePrint, classData, calledFields, namingService.getName(bluePrint),
					usedBluePrints);

			code.add("\n");

			bluePrint.setBuild();
		}
	}

	private void addFieldsToObject(CodeBlock.Builder code, ComplexBluePrint bluePrint, ClassData classData,
			Set<FieldData> calledFields, String objectName, Set<BluePrint> usedBluePrints) {

		if (TestgeneratorConfig.traceReadFieldAccess()) {

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
			code.addStatement(objectName + "." + field.getName() + "=" + namingService.getName(bluePrint));

		} else if (bluePrint.isSimpleBluePrint()) {
			SimpleBluePrint<?> simpleBluePrint = bluePrint.castToSimpleBluePrint();

			code.addStatement(objectName + "." + field.getName() + "=" + simpleBluePrint.valueCreation(),
					simpleBluePrint.getReferenceClasses().toArray());

		} else if (bluePrint.isCollectionBluePrint()) {
			collectionGeneration.addContainerToObject(code, bluePrint.castToCollectionBluePrint(), field, objectName);
		} else if (bluePrint.isArrayBluePrint()) {
			arrayGeneration.addContainerToObject(code, bluePrint.castToArrayBluePrint(), field, objectName);
		}
	}

	private void addFieldToObject(CodeBlock.Builder code, BluePrint bluePrint, SetterMethodData setter,
			String objectName) {
		if (setter == null) {
			StringBuilder statement = new StringBuilder();
			statement.append("//TODO no setter found for Field: " + namingService.getName(bluePrint) + " Value: ");

			List<Class<?>> referenceClasses = getFieldValue(bluePrint, statement);

			code.addStatement(statement.toString(), referenceClasses.toArray());
		} else {
			if (bluePrint.isComplexBluePrint()) {
				code.addStatement(objectName + "." + setter.getName() + "(" + namingService.getName(bluePrint) + ")");

			} else if (bluePrint.isSimpleBluePrint()) {
				SimpleBluePrint<?> simpleBluePrint = bluePrint.castToSimpleBluePrint();

				code.addStatement(objectName + "." + setter.getName() + "(" + simpleBluePrint.valueCreation() + ")",
						simpleBluePrint.getReferenceClasses().toArray());
			} else if (bluePrint.isCollectionBluePrint()) {
				collectionGeneration.addContainerToObject(code, bluePrint.castToCollectionBluePrint(), setter,
						objectName);
			} else if (bluePrint.isArrayBluePrint()) {
				arrayGeneration.addContainerToObject(code, bluePrint.castToArrayBluePrint(), setter, objectName);
			}
		}
	}

	private List<Class<?>> getFieldValue(BluePrint bluePrint, StringBuilder statement) {
		if (bluePrint.isComplexType()) {
			statement.append(namingService.getName(bluePrint));

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

			if (calledField.isPresent() || !TestgeneratorConfig.traceReadFieldAccess() || //
					(classData.isInnerClass()
							&& classData.getOuterClass().getName().equals(bp.getClassNameOfReference()))) {
				if (bp.isComplexBluePrint()) {
					createComplexObject(code, bp);

				} else if (bp.isCollectionBluePrint()) {
					AbstractBasicCollectionBluePrint<?> collection = bp.castToCollectionBluePrint();

					SetterMethodData setter = null;
					SignatureType signature = null;
					if (TestgeneratorConfig.traceReadFieldAccess()) {
						FieldData field = classData.getFieldInHierarchie(calledField.get());
						signature = field.getSignature();
						setter = classData.getSetterInHierarchie(field);

					} else {
						FieldData field = classData.getCollectionFieldInHierarchie(collection.getName());
						signature = field.getSignature();
						setter = classData.getSetterInHierarchie(field);
					}

					collectionGeneration.createCollection(code, collection, signature,
							setter != null && SetterType.COLLECTION_SETTER == setter.getType(), false);
				} else if (bp.isArrayBluePrint()) {
					ArrayBluePrint arrayBluePrint = bp.castToArrayBluePrint();

					FieldData field = classData.getFieldInHierarchie(arrayBluePrint.getName(),
							arrayBluePrint.getType());
					SetterMethodData setter = classData.getSetterInHierarchie(field);

					arrayGeneration.createArray(code, arrayBluePrint,
							setter != null && SetterType.VALUE_GETTER == setter.getType(), false);
				}
			}
		}
	}

	private void createComplexObject(CodeBlock.Builder code, BluePrint bluePrint) {
		if (bluePrint.isNotBuild()) {
			ClassData classData = TestGenerationHelper.getClassData(bluePrint.getReference());

			Set<FieldData> calledFields = Collections.emptySet();
			if (TestgeneratorConfig.traceReadFieldAccess()) {
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

	@Override
	public void createField(TypeSpec.Builder compilationUnit, ComplexBluePrint bluePrint, SignatureType signature) {
		// TODO Auto-generated method stub

	}

}

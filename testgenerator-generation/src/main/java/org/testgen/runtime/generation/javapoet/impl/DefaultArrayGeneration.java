package org.testgen.runtime.generation.javapoet.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Modifier;

import org.testgen.config.TestgeneratorConfig;
import org.testgen.logging.LogManager;
import org.testgen.logging.Logger;
import org.testgen.runtime.classdata.model.ClassData;
import org.testgen.runtime.classdata.model.FieldData;
import org.testgen.runtime.classdata.model.SetterMethodData;
import org.testgen.runtime.classdata.model.SetterType;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;
import org.testgen.runtime.generation.ArrayGeneration;
import org.testgen.runtime.generation.CollectionGeneration;
import org.testgen.runtime.generation.ComplexObjectGeneration;
import org.testgen.runtime.generation.naming.NamingService;
import org.testgen.runtime.valuetracker.blueprint.ArrayBluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.storage.ValueStorage;

import com.squareup.javapoet.CodeBlock.Builder;
import com.squareup.javapoet.TypeSpec;

public class DefaultArrayGeneration implements ArrayGeneration<TypeSpec.Builder, Builder> {

	private static final Logger LOGGER = LogManager.getLogger(DefaultArrayGeneration.class);

	private ComplexObjectGeneration<TypeSpec.Builder, Builder> objectGeneration = getComplexObjectGeneration();

	private CollectionGeneration<TypeSpec.Builder, Builder> collectionGeneration = getCollectionGeneration();
	private NamingService namingService = getNamingService();

	@Override
	public void createArray(Builder code, ArrayBluePrint array, boolean onlyCreateElements, boolean isField) {
		LOGGER.info("starting generation of Array: " + array);

		if (array.isNotBuild()) {

			for (BluePrint bluePrint : array.getElements()) {
				createComplexContainerElement(code, bluePrint, null);
			}

			if (!onlyCreateElements) {

				String arrayName = namingService.getName(array);

				if (isField) {
					StringBuilder arrayCreation = new StringBuilder(arrayName + " =");
					createArrayConstructor(array, arrayCreation, true);
					code.addStatement(arrayCreation.toString(), array.getBaseType());

				} else {

					StringBuilder arrayCreation = new StringBuilder("$T " + arrayName + " =");
					createArrayConstructor(array, arrayCreation, false);

					List<Class<?>> types = new ArrayList<>();
					types.add(array.getType());
					types.add(array.getBaseType());

					String[] arrayElements = new String[array.size()];

					BluePrint[] elements = array.getElements();

					for (int i = 0; i < elements.length; i++) {
						BluePrint bluePrint = elements[i];

						if (bluePrint.isComplexType()) {
							arrayElements[i] = namingService.getName(bluePrint);
						} else if (bluePrint.isSimpleBluePrint()) {
							SimpleBluePrint<?> simpleBluePrint = bluePrint.castToSimpleBluePrint();
							arrayElements[i] = simpleBluePrint.valueCreation();
							types.addAll(simpleBluePrint.getReferenceClasses());
						}
					}

					arrayCreation.append("{" + String.join(", ", arrayElements) + "}");

					code.addStatement(arrayCreation.toString(), types.toArray());
				}
			}
		}

		array.setBuild();
	}

	private void createArrayConstructor(ArrayBluePrint array, StringBuilder code, boolean withDimensionInit) {
		code.append(" new $T");

		int dimensions = array.getDimensions();

		if (withDimensionInit) {
			code.append("[" + array.size() + "]");
			dimensions--;
		}

		while (dimensions > 0) {
			code.append("[]");
			dimensions--;
		}
	}

	@Override
	public void createField(TypeSpec.Builder compilationUnit, ArrayBluePrint bluePrint, SignatureType signature) {
		compilationUnit.addField(bluePrint.getType(), namingService.getName(bluePrint), Modifier.PRIVATE);

	}

	@Override
	public void addContainerToObject(Builder statementTree, ArrayBluePrint arrayBP, SetterMethodData setter,
			String objectName) {
		if (SetterType.VALUE_GETTER == setter.getType()) {
			BluePrint[] elements = arrayBP.getElements();
			for (int i = 0; i < elements.length; i++) {
				BluePrint bluePrint = elements[i];

				if (bluePrint.isComplexType()) {
					statementTree.addStatement(objectName + "." + setter.getName() + "()[" + i + "] = "
							+ namingService.getName(bluePrint));
				} else if (bluePrint.isSimpleBluePrint()) {
					SimpleBluePrint<?> simpleBp = bluePrint.castToSimpleBluePrint();

					statementTree.addStatement(
							objectName + "." + setter.getName() + "()[" + i + "] = " + simpleBp.valueCreation(),
							simpleBp.getReferenceClasses().toArray());
				}
			}

		} else if (SetterType.VALUE_SETTER == setter.getType()) {
			statementTree
					.addStatement(objectName + "." + setter.getName() + "(" + namingService.getName(arrayBP) + ")");
		}

	}

	@Override
	public void addContainerToObject(Builder statementTree, ArrayBluePrint arrayBP, FieldData field,
			String objectName) {
		LOGGER.info("add Collection " + arrayBP + " to Object " + objectName);

		statementTree.addStatement(objectName + "." + field.getName() + "=" + namingService.getName(arrayBP));
	}

	private void createComplexContainerElement(Builder code, BluePrint bluePrint, SignatureType signature) {
		if (bluePrint.isComplexBluePrint() && bluePrint.isNotBuild()) {
			ClassData classData = TestGenerationHelper.getClassData(bluePrint.getReference());

			Set<FieldData> calledFields = Collections.emptySet();
			if (TestgeneratorConfig.traceReadFieldAccess()) {
				calledFields = TestGenerationHelper.getCalledFields(bluePrint.getReference());
			}

			objectGeneration.createObject(code, bluePrint.castToComplexBluePrint(),
					ValueStorage.getInstance().getMethodParameters().contains(bluePrint), classData, calledFields);

		} else if (bluePrint.isCollectionBluePrint() && bluePrint.isNotBuild()) {

			collectionGeneration.createCollection(code, bluePrint.castToCollectionBluePrint(), signature, //
					false, ValueStorage.getInstance().getMethodParameters().contains(bluePrint));
		} else if (bluePrint.isArrayBluePrint() && bluePrint.isNotBuild()) {
			createArray(code, bluePrint.castToArrayBluePrint(), false,
					ValueStorage.getInstance().getMethodParameters().contains(bluePrint));

		}
	}

}

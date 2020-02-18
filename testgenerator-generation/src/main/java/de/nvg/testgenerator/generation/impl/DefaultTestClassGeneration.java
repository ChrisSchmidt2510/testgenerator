package de.nvg.testgenerator.generation.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec.Builder;

import de.nvg.runtime.classdatamodel.ClassData;
import de.nvg.runtime.classdatamodel.ConstructorData;
import de.nvg.runtime.classdatamodel.FieldData;
import de.nvg.runtime.classdatamodel.SetterMethodData;
import de.nvg.runtime.classdatamodel.SetterType;
import de.nvg.testgenerator.generation.TestClassGeneration;
import de.nvg.testgenerator.logging.LogManager;
import de.nvg.testgenerator.logging.Logger;
import de.nvg.valuetracker.blueprint.BasicCollectionBluePrint;
import de.nvg.valuetracker.blueprint.BluePrint;
import de.nvg.valuetracker.blueprint.ComplexBluePrint;
import de.nvg.valuetracker.blueprint.SimpleBluePrint;

public class DefaultTestClassGeneration implements TestClassGeneration {
	private static final String METHOD_INIT_TESTOBJECT = "initTestobject";
	private static final String METHOD_INIT_METHOD_PARAMETER = "initMethodParameter";
	private static final String METHOD_INIT = "init";

	private static final String JUNIT_ANNOTATION_TEST = "org.junit.Test";
	private static final String JUNIT_ANNOTATION_BEFORE = "org.junit.Before";

	private static final String METHOD_TEST_START = "test";

	private static final Logger LOGGER = LogManager.getLogger(DefaultTestClassGeneration.class);

	private String testObjectName;
	private List<String> methodParameterNames = new ArrayList<>();

	private final DefaultCollectionsGeneration collectionsGeneration = new DefaultCollectionsGeneration(this);

	@Override
	public void prepareTestObject(Builder typeSpec, BluePrint testObject, ClassData classData,
			Set<FieldData> calledFields) {
		LOGGER.info("Starting generation of testobject: " + testObject.getName());

		typeSpec.addField(testObject.getReference().getClass(), testObject.getName(), Modifier.PRIVATE);
		testObjectName = testObject.getName();

		CodeBlock.Builder code = CodeBlock.builder();

		createObject(code, testObject.castToComplexBluePrint(), true, classData, calledFields);

		typeSpec.addMethod(MethodSpec.methodBuilder(METHOD_INIT_TESTOBJECT).addModifiers(Modifier.PRIVATE)
				.addCode(code.build()).build());
	}

	@Override
	public void prepareMethodParameters(Builder typeSpec, Collection<BluePrint> methodParameters) {
		CodeBlock.Builder code = CodeBlock.builder();

		for (BluePrint methodParameter : methodParameters) {
			LOGGER.info("Starting generation of method-parameter: " + methodParameter.getName());

			methodParameterNames.add(methodParameter.getName());

			if (methodParameter.isComplexBluePrint()) {
				typeSpec.addField(methodParameter.getReference().getClass(), methodParameter.getName(),
						Modifier.PRIVATE);

				ClassData classData = TestGenerationHelper.getClassData(methodParameter.getReference());
				Set<FieldData> calledFields = TestGenerationHelper.getCalledFields(methodParameter.getReference());

				createObject(code, methodParameter.castToComplexBluePrint(), true, classData, calledFields);
			} else if (methodParameter.isSimpleBluePrint()) {
				SimpleBluePrint<?> bluePrint = methodParameter.castToSimpleBluePrint();

				typeSpec.addField(methodParameter.getReference().getClass(), methodParameter.getName(),
						Modifier.PRIVATE);

				code.addStatement(methodParameter.getName() + " = " + bluePrint.valueCreation(),
						bluePrint.getReferenceClasses().toArray());
			} else if (methodParameter.isCollectionBluePrint()) {
				BasicCollectionBluePrint<?> collectionBluePrint = methodParameter.castToCollectionBluePrint();

				collectionsGeneration.addFieldToClass(typeSpec, collectionBluePrint);
				collectionsGeneration.createCollection(code, collectionBluePrint, false, true);
			}
		}

		typeSpec.addMethod(MethodSpec.methodBuilder(METHOD_INIT_METHOD_PARAMETER).addModifiers(Modifier.PRIVATE)
				.addCode(code.build()).build());

	}

	@Override
	public void generateTestMethod(Builder typeSpec, String methodName) {
		CodeBlock codeInit = CodeBlock.builder().addStatement(METHOD_INIT_TESTOBJECT + "()")
				.addStatement(METHOD_INIT_METHOD_PARAMETER + "()").build();

		typeSpec.addMethod(MethodSpec.methodBuilder(METHOD_INIT).addModifiers(Modifier.PUBLIC)
				.addAnnotation(AnnotationSpec.builder(ClassName.bestGuess(JUNIT_ANNOTATION_BEFORE)).build())
				.addCode(codeInit).build());

		CodeBlock codeTestMethod = CodeBlock.builder().addStatement(testObjectName + "." + methodName + "("
				+ methodParameterNames.stream().collect(Collectors.joining(",")) + ")").build();

		typeSpec.addMethod(MethodSpec.methodBuilder(createTestMethodName(methodName))//
				.addModifiers(Modifier.PUBLIC)
				.addAnnotation(AnnotationSpec.builder(ClassName.bestGuess(JUNIT_ANNOTATION_TEST)).build())
				.addCode(codeTestMethod).build());
	}

	void createObject(CodeBlock.Builder code, ComplexBluePrint bluePrint, boolean isField, ClassData classData,
			Set<FieldData> calledFields) {

		LOGGER.info("generating Complex-Object: " + bluePrint.getName());

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

	private void createComplexObject(CodeBlock.Builder code, BluePrint bluePrint) {
		if (bluePrint.isNotBuild()) {
			Set<FieldData> calledFields = TestGenerationHelper.getCalledFields(bluePrint.getReference());
			ClassData classData = TestGenerationHelper.getClassData(bluePrint.getReference());

			createObject(code, bluePrint.castToComplexBluePrint(), false, classData, calledFields);
		}
	}

	private String createTestMethodName(String methodName) {
		return METHOD_TEST_START
				+ methodName.replace(methodName.charAt(0), Character.toUpperCase(methodName.charAt(0)));
	}

}

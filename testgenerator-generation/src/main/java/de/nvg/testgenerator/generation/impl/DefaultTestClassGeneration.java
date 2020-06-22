package de.nvg.testgenerator.generation.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;

import org.testgen.core.logging.LogManager;
import org.testgen.core.logging.Logger;
import org.testgen.core.properties.RuntimeProperties;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec.Builder;

import de.nvg.runtime.classdatamodel.ClassData;
import de.nvg.runtime.classdatamodel.FieldData;
import de.nvg.runtime.classdatamodel.SignatureData;
import de.nvg.testgenerator.generation.ComplexObjectGeneration;
import de.nvg.testgenerator.generation.ContainerGeneration;
import de.nvg.testgenerator.generation.TestClassGeneration;
import de.nvg.valuetracker.blueprint.AbstractBasicCollectionBluePrint;
import de.nvg.valuetracker.blueprint.ArrayBluePrint;
import de.nvg.valuetracker.blueprint.BluePrint;
import de.nvg.valuetracker.blueprint.SimpleBluePrint;

public class DefaultTestClassGeneration implements TestClassGeneration {
	private static final String METHOD_INIT_TESTOBJECT = "initTestobject";
	private static final String METHOD_INIT_METHOD_PARAMETER = "initMethodParameter";
	private static final String METHOD_INIT = "init";

	private static final String JUNIT_ANNOTATION_TEST = "org.junit.Test";
	private static final String JUNIT_ANNOTATION_BEFORE = "org.junit.Before";

	private static final String METHOD_TEST_START = "test";

	private static final Logger LOGGER = LogManager.getLogger(DefaultTestClassGeneration.class);

	private final RuntimeProperties properties = RuntimeProperties.getInstance();

	private String testObjectName;
	private List<String> methodParameterNames = new ArrayList<>();

	private ContainerGeneration collectionsGeneration;
	private ComplexObjectGeneration objectGeneration;

	{
		collectionsGeneration = new DefaultContainerGeneration();
		objectGeneration = new DefaultComplexObjectGeneration();

		collectionsGeneration.setComplexObjectGeneration(objectGeneration);
		objectGeneration.setContainerGeneration(collectionsGeneration);
	}

	@Override
	public void prepareTestObject(Builder typeSpec, BluePrint testObject, ClassData classData,
			Set<FieldData> calledFields) {
		LOGGER.info("Starting generation of testobject: " + testObject);

		typeSpec.addField(testObject.getReference().getClass(), testObject.getName(), Modifier.PRIVATE);
		testObjectName = testObject.getName();

		CodeBlock.Builder code = CodeBlock.builder();

		objectGeneration.createObject(code, testObject.castToComplexBluePrint(), true, classData, calledFields);

		typeSpec.addMethod(MethodSpec.methodBuilder(METHOD_INIT_TESTOBJECT).addModifiers(Modifier.PRIVATE)
				.addCode(code.build()).build());
	}

	@Override
	public void prepareMethodParameters(Builder typeSpec, Collection<BluePrint> methodParameters,
			Map<Integer, SignatureData> methodSignature) {
		CodeBlock.Builder code = CodeBlock.builder();

		int methodParameterIndex = 1;

		for (BluePrint methodParameter : methodParameters) {
			LOGGER.info("Starting generation of method-parameter: " + methodParameter);

			methodParameterNames.add(methodParameter.getName());

			if (methodParameter.isComplexBluePrint()) {
				typeSpec.addField(methodParameter.getReference().getClass(), methodParameter.getName(),
						Modifier.PRIVATE);

				ClassData classData = TestGenerationHelper.getClassData(methodParameter.getReference());

				Set<FieldData> calledFields = Collections.emptySet();
				if (properties.wasFieldTrackingActivated()) {
					calledFields = TestGenerationHelper.getCalledFields(methodParameter.getReference());
				}

				objectGeneration.createObject(code, methodParameter.castToComplexBluePrint(), true, classData,
						calledFields);
			} else if (methodParameter.isSimpleBluePrint()) {
				SimpleBluePrint<?> bluePrint = methodParameter.castToSimpleBluePrint();

				typeSpec.addField(methodParameter.getReference().getClass(), methodParameter.getName(),
						Modifier.PRIVATE);

				code.addStatement(methodParameter.getName() + " = " + bluePrint.valueCreation(),
						bluePrint.getReferenceClasses().toArray());
			} else if (methodParameter.isCollectionBluePrint()) {
				AbstractBasicCollectionBluePrint<?> collectionBluePrint = methodParameter.castToCollectionBluePrint();

				collectionsGeneration.addFieldToClass(typeSpec, collectionBluePrint,
						methodSignature.get(methodParameterIndex));
				collectionsGeneration.createCollection(code, collectionBluePrint,
						methodSignature.get(methodParameterIndex), false, true);
			} else if (methodParameter.isArrayBluePrint()) {
				ArrayBluePrint arrayBluePrint = methodParameter.castToArrayBluePrint();

				collectionsGeneration.addFieldToClass(typeSpec, arrayBluePrint, null);
				collectionsGeneration.createArray(code, arrayBluePrint, false, true);
			}

			methodParameterIndex++;
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

		CodeBlock codeTestMethod = CodeBlock.builder()
				.addStatement(testObjectName + "." + methodName + "("
						+ methodParameterNames.stream().collect(Collectors.joining(",")) + ")")
				.addStatement("//TODO add Assertion").build();

		typeSpec.addMethod(MethodSpec.methodBuilder(createTestMethodName(methodName))//
				.addModifiers(Modifier.PUBLIC)
				.addAnnotation(AnnotationSpec.builder(ClassName.bestGuess(JUNIT_ANNOTATION_TEST)).build())
				.addCode(codeTestMethod).build());
	}

	private String createTestMethodName(String methodName) {
		return METHOD_TEST_START
				+ methodName.replace(methodName.charAt(0), Character.toUpperCase(methodName.charAt(0)));
	}

}

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

	private static final String METHOD_INIT_PROXY_OBJECTS = "initProxyObjects";
	private static final String METHOD_INIT = "init";

	private static final String JUNIT_ANNOTATION_TEST = "org.junit.Test";
	private static final String JUNIT_ANNOTATION_BEFORE = "org.junit.Before";

	private static final String METHOD_TEST_START = "test";

	private static final Logger LOGGER = LogManager.getLogger(DefaultTestClassGeneration.class);

	private final RuntimeProperties properties = RuntimeProperties.getInstance();

	private String testObjectName;
	private List<String> methodParameterNames = new ArrayList<>();

	private ContainerGeneration containerGeneration;
	private ComplexObjectGeneration objectGeneration;

	{
		containerGeneration = new DefaultContainerGeneration();
		objectGeneration = new DefaultComplexObjectGeneration();

		containerGeneration.setComplexObjectGeneration(objectGeneration);
		objectGeneration.setContainerGeneration(containerGeneration);
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
				if (properties.wasFieldTrackingActivated())
					calledFields = TestGenerationHelper.getCalledFields(methodParameter.getReference());

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

				containerGeneration.addFieldToClass(typeSpec, collectionBluePrint,
						methodSignature.get(methodParameterIndex));
				containerGeneration.createCollection(code, collectionBluePrint,
						methodSignature.get(methodParameterIndex), false, true);
			} else if (methodParameter.isArrayBluePrint()) {
				ArrayBluePrint arrayBluePrint = methodParameter.castToArrayBluePrint();

				containerGeneration.addFieldToClass(typeSpec, arrayBluePrint, null);
				containerGeneration.createArray(code, arrayBluePrint, false, true);
			}

			methodParameterIndex++;
		}

		typeSpec.addMethod(MethodSpec.methodBuilder(METHOD_INIT_METHOD_PARAMETER).addModifiers(Modifier.PRIVATE)
				.addCode(code.build()).build());

	}

	@Override
	public void prepareProxyObjects(Builder typeSpec, Collection<BluePrint> proxyObjects) {
		CodeBlock.Builder code = CodeBlock.builder();

		for (BluePrint proxyObject : proxyObjects) {
			LOGGER.info("Starting generation of proxy-object: " + proxyObject);
			code.addStatement("// generating Object for Proxy-Method " + proxyObject.getName());

			if (proxyObject.isComplexBluePrint()) {
				ClassData classData = TestGenerationHelper.getClassData(proxyObject.getReference());

				Set<FieldData> calledFields = Collections.emptySet();
				if (properties.wasFieldTrackingActivated())
					calledFields = TestGenerationHelper.getCalledFields(proxyObject.getReference());

				objectGeneration.createObject(code, proxyObject.castToComplexBluePrint(), false, classData,
						calledFields);
			} else if (proxyObject.isSimpleBluePrint()) {
				SimpleBluePrint<?> bluePrint = proxyObject.castToSimpleBluePrint();

				List<Class<?>> types = new ArrayList<>();
				types.add(bluePrint.getReference().getClass());
				types.addAll(bluePrint.getReferenceClasses());

				code.addStatement("$T " + bluePrint.getName() + " = " + bluePrint.valueCreation(), types.toArray());
			} else if (proxyObject.isCollectionBluePrint()) {
				containerGeneration.createCollection(code, proxyObject.castToCollectionBluePrint(), //
						null, false, false);
			} else if (proxyObject.isArrayBluePrint()) {
				containerGeneration.createArray(code, proxyObject.castToArrayBluePrint(), false, false);
			}
		}

		typeSpec.addMethod(MethodSpec.methodBuilder(METHOD_INIT_PROXY_OBJECTS).addModifiers(Modifier.PRIVATE)
				.addCode(code.build()).build());
	}

	@Override
	public void generateTestMethod(Builder typeSpec, String methodName, boolean withProxyObjects) {
		com.squareup.javapoet.CodeBlock.Builder codeInit = CodeBlock.builder()//
				.addStatement(METHOD_INIT_TESTOBJECT + "()")//
				.addStatement(METHOD_INIT_METHOD_PARAMETER + "()");
		if (withProxyObjects)
			codeInit.addStatement(METHOD_INIT_PROXY_OBJECTS + "()");

		typeSpec.addMethod(MethodSpec.methodBuilder(METHOD_INIT).addModifiers(Modifier.PUBLIC)
				.addAnnotation(AnnotationSpec.builder(ClassName.bestGuess(JUNIT_ANNOTATION_BEFORE)).build())
				.addCode(codeInit.build()).build());

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

package de.nvg.testgenerator.generation.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;

import org.testgen.config.TestgeneratorConfig;
import org.testgen.logging.LogManager;
import org.testgen.logging.Logger;
import org.testgen.runtime.classdata.model.ClassData;
import org.testgen.runtime.classdata.model.FieldData;
import org.testgen.runtime.classdata.model.descriptor.BasicType;
import org.testgen.runtime.classdata.model.descriptor.DescriptorType;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

import de.nvg.testgenerator.generation.ComplexObjectGeneration;
import de.nvg.testgenerator.generation.ContainerGeneration;
import de.nvg.testgenerator.generation.TestClassGeneration;
import de.nvg.testgenerator.generation.naming.NamingService;
import de.nvg.valuetracker.blueprint.AbstractBasicCollectionBluePrint;
import de.nvg.valuetracker.blueprint.ArrayBluePrint;
import de.nvg.valuetracker.blueprint.BluePrint;
import de.nvg.valuetracker.blueprint.ProxyBluePrint;
import de.nvg.valuetracker.blueprint.SimpleBluePrint;

public class DefaultTestClassGeneration implements TestClassGeneration {
	private static final String TEST = "Test";

	private static final String METHOD_INIT_TESTOBJECT = "initTestobject";
	private static final String METHOD_INIT_METHOD_PARAMETER = "initMethodParameter";
	private static final String METHOD_INIT_PROXY_OBJECTS = "initProxyObjects";
	private static final String METHOD_INIT = "init";

	private static final String JUNIT_ANNOTATION_TEST = "org.junit.Test";
	private static final String JUNIT_ANNOTATION_BEFORE = "org.junit.Before";

	private static final String METHOD_TEST_START = "test";

	private static final Logger LOGGER = LogManager.getLogger(DefaultTestClassGeneration.class);

	private String testObjectName;
	private List<String> methodParameterNames = new ArrayList<>();

	private ContainerGeneration containerGeneration;
	private ComplexObjectGeneration objectGeneration;

	private final NamingService namingService = new NamingService();

	{
		containerGeneration = new DefaultContainerGeneration();
		objectGeneration = new DefaultComplexObjectGeneration();

		containerGeneration.setComplexObjectGeneration(objectGeneration);
		containerGeneration.setNamingService(namingService);

		objectGeneration.setContainerGeneration(containerGeneration);
		objectGeneration.setNamingService(namingService);
	}

	@Override
	public Builder createTestClass(Class<?> testClass) {
		return TypeSpec.classBuilder(testClass.getSimpleName() + TEST).addModifiers(Modifier.PUBLIC);
	}

	@Override
	public void prepareTestObject(Builder typeSpec, BluePrint testObject, ClassData classData,
			Set<FieldData> calledFields) {
		LOGGER.info("Starting generation of testobject: " + testObject);

		typeSpec.addField(testObject.getReference().getClass(), namingService.getName(testObject), Modifier.PRIVATE);
		testObjectName = testObject.getName();

		CodeBlock.Builder code = CodeBlock.builder();

		objectGeneration.createObject(code, testObject.castToComplexBluePrint(), true, classData, calledFields);

		typeSpec.addMethod(MethodSpec.methodBuilder(METHOD_INIT_TESTOBJECT).addModifiers(Modifier.PRIVATE)
				.addCode(code.build()).build());
	}

	@Override
	public void prepareMethodParameters(Builder typeSpec, Collection<BluePrint> methodParameters,
			List<DescriptorType> methodParameterTypes) {
		CodeBlock.Builder code = CodeBlock.builder();

		int methodParameterIndex = 0;

		for (BluePrint methodParameter : methodParameters) {
			LOGGER.info("Starting generation of method-parameter: " + methodParameter);

			methodParameterNames.add(methodParameter.getName());

			String parameterName = namingService.getName(methodParameter);

			if (methodParameter.isComplexBluePrint()) {
				typeSpec.addField(methodParameter.getReference().getClass(), //
						parameterName, Modifier.PRIVATE);

				ClassData classData = TestGenerationHelper.getClassData(methodParameter.getReference());

				Set<FieldData> calledFields = Collections.emptySet();
				if (TestgeneratorConfig.isFieldTrackingActivated()) {
					calledFields = TestGenerationHelper.getCalledFields(methodParameter.getReference());
				}

				objectGeneration.createObject(code, methodParameter.castToComplexBluePrint(), true, classData,
						calledFields);
			} else if (methodParameter.isSimpleBluePrint()) {
				SimpleBluePrint<?> bluePrint = methodParameter.castToSimpleBluePrint();

				BasicType type = methodParameterTypes.get(methodParameterIndex).castToBasicType();

				typeSpec.addField(type.getType(), parameterName, Modifier.PRIVATE);

				code.addStatement(methodParameter.getName() + " = " + bluePrint.valueCreation(),
						bluePrint.getReferenceClasses().toArray());
			} else if (methodParameter.isCollectionBluePrint()) {
				AbstractBasicCollectionBluePrint<?> collectionBluePrint = methodParameter.castToCollectionBluePrint();

				DescriptorType type = methodParameterTypes.get(methodParameterIndex);
				SignatureType collectionType = type.isSignatureType() ? type.castToSignatureType() : null;

				containerGeneration.addFieldToClass(typeSpec, collectionBluePrint, collectionType);
				containerGeneration.createCollection(code, collectionBluePrint, collectionType, false, true);
			} else if (methodParameter.isArrayBluePrint()) {
				ArrayBluePrint arrayBluePrint = methodParameter.castToArrayBluePrint();

				containerGeneration.addFieldToClass(typeSpec, arrayBluePrint, null);
				containerGeneration.createArray(code, arrayBluePrint, false, true);
			} else if (methodParameter instanceof ProxyBluePrint) {
				typeSpec.addField(methodParameterTypes.get(methodParameterIndex).castToBasicType().getType(),
						parameterName, Modifier.PRIVATE);

				code.add("//add Initalization to " + parameterName);
				code.add(parameterName + " = null");
			}

			methodParameterIndex++;
		}

		typeSpec.addMethod(MethodSpec.methodBuilder(METHOD_INIT_METHOD_PARAMETER).addModifiers(Modifier.PRIVATE)
				.addCode(code.build()).build());

	}

	@Override
	public void prepareProxyObjects(Builder typeSpec, Map<ProxyBluePrint, List<BluePrint>> proxyObjects) {
		CodeBlock.Builder code = CodeBlock.builder();

		for (Entry<ProxyBluePrint, List<BluePrint>> proxy : proxyObjects.entrySet()) {
			ProxyBluePrint proxyBp = proxy.getKey();

			code.addStatement("//TODO add initalization for proxy $T", proxyBp.getInterfaceClass());
			code.addStatement("$T " + namingService.getName(proxyBp) + " = null", proxyBp.getInterfaceClass());
			proxyBp.setBuild();

			for (BluePrint proxyObject : proxy.getValue()) {
				LOGGER.info("Starting generation of proxy-object: " + proxyObject);
				code.addStatement("// generating Object for Proxy-Method " + proxyObject.getName());

				if (proxyObject.isComplexBluePrint()) {
					ClassData classData = TestGenerationHelper.getClassData(proxyObject.getReference());

					Set<FieldData> calledFields = Collections.emptySet();
					if (TestgeneratorConfig.traceReadFieldAccess()) {
						calledFields = TestGenerationHelper.getCalledFields(proxyObject.getReference());
					}

					objectGeneration.createObject(code, proxyObject.castToComplexBluePrint(), false, classData,
							calledFields);
				} else if (proxyObject.isSimpleBluePrint()) {
					SimpleBluePrint<?> bluePrint = proxyObject.castToSimpleBluePrint();

					List<Class<?>> types = new ArrayList<>();
					types.add(bluePrint.getReference().getClass());
					types.addAll(bluePrint.getReferenceClasses());

					code.addStatement("$T " + namingService.getName(bluePrint) + " = " + bluePrint.valueCreation(),
							types.toArray());
				} else if (proxyObject.isCollectionBluePrint()) {
					Method proxyMethod = Arrays.stream(proxyBp.getInterfaceClass().getMethods())
							.filter(method -> proxyObject.getName().equals(method.getName())).findAny().get();

					SignatureType signature = TestGenerationHelper
							.mapGenericTypeToSignature(proxyMethod.getGenericReturnType());

					containerGeneration.createCollection(code, proxyObject.castToCollectionBluePrint(), //
							signature, false, false);
				} else if (proxyObject.isArrayBluePrint()) {
					containerGeneration.createArray(code, proxyObject.castToArrayBluePrint(), false, false);
				}
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
		if (withProxyObjects) {
			codeInit.addStatement(METHOD_INIT_PROXY_OBJECTS + "()");
		}

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

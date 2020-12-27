package org.testgen.runtime.generation.javapoet.impl;

import java.io.IOException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import org.testgen.runtime.generation.ArrayGeneration;
import org.testgen.runtime.generation.CollectionGeneration;
import org.testgen.runtime.generation.ComplexObjectGeneration;
import org.testgen.runtime.generation.SimpleObjectGeneration;
import org.testgen.runtime.generation.TestClassGeneration;
import org.testgen.runtime.generation.Testgenerator;
import org.testgen.runtime.generation.naming.NamingService;
import org.testgen.runtime.generation.naming.NamingServiceProvider;
import org.testgen.runtime.valuetracker.blueprint.AbstractBasicCollectionBluePrint;
import org.testgen.runtime.valuetracker.blueprint.ArrayBluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.ProxyBluePrint;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

public class DefaultTestClassGeneration implements TestClassGeneration<Builder, CodeBlock.Builder> {
	private static final String TEST = "Test";

	private static final String METHOD_INIT_TESTOBJECT = "initTestobject";
	private static final String METHOD_INIT_METHOD_PARAMETER = "initMethodParameter";
	private static final String METHOD_INIT_PROXY_OBJECTS = "initProxyObjects";
	private static final String METHOD_INIT = "init";

	private static final String JUNIT_ANNOTATION_TEST = "org.junit.Test";
	private static final String JUNIT_ANNOTATION_BEFORE = "org.junit.Before";

	private static final String METHOD_TEST_START = "test";

	private static final Logger LOGGER = LogManager.getLogger(DefaultTestClassGeneration.class);

	private final CollectionGeneration<TypeSpec.Builder, CodeBlock.Builder> collectionGeneration = new DefaultCollectionGeneration();
	private final ArrayGeneration<TypeSpec.Builder, CodeBlock.Builder> arrayGeneration = new DefaultArrayGeneration();
	private final ComplexObjectGeneration<TypeSpec.Builder, CodeBlock.Builder> objectGeneration = new DefaultComplexObjectGeneration();
	private final NamingService namingService = NamingServiceProvider.getNamingService();

	private String testObjectName;
	private List<String> methodParameterNames = new ArrayList<>();

	private Class<?> testClass;

	@Override
	public Builder createTestClass(Class<?> testClass) {
		this.testClass = testClass;

		Builder compilationUnit = TypeSpec.classBuilder(testClass.getSimpleName() + TEST)//
				.addModifiers(Modifier.PUBLIC);
		return compilationUnit;
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
				if (TestgeneratorConfig.traceReadFieldAccess()) {
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

				collectionGeneration.createField(typeSpec, collectionBluePrint, collectionType);
				collectionGeneration.createCollection(code, collectionBluePrint, collectionType, false, true);
			} else if (methodParameter.isArrayBluePrint()) {
				ArrayBluePrint arrayBluePrint = methodParameter.castToArrayBluePrint();

				arrayGeneration.createField(typeSpec, arrayBluePrint, null);
				arrayGeneration.createArray(code, arrayBluePrint, false, true);
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

					collectionGeneration.createCollection(code, proxyObject.castToCollectionBluePrint(), //
							signature, false, false);
				} else if (proxyObject.isArrayBluePrint()) {
					arrayGeneration.createArray(code, proxyObject.castToArrayBluePrint(), false, false);
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

	@Override
	public void addDocumentation(Builder compilationUnit) {
		compilationUnit.addJavadoc(
				"Test generated at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))
						+ " with Testgenerator-" + Testgenerator.class.getPackage().getImplementationVersion());

	}

	@Override
	public void toFile(Builder compilationUnit) {
		JavaFile file = JavaFile.builder(testClass.getPackage().getName(), compilationUnit.build())
				.skipJavaLangImports(true).build();
		LOGGER.debug("generated Test", stream -> {
			try {
				file.writeTo(stream);
			} catch (IOException e) {
				LOGGER.error(e);
			}
		});

	}

	@Override
	public ComplexObjectGeneration<Builder, CodeBlock.Builder> createComplexObjectGeneration() {
		return objectGeneration;
	}

	@Override
	public SimpleObjectGeneration<Builder, CodeBlock.Builder> createSimpleObjectGeneration() {
		return null;
	}

	@Override
	public CollectionGeneration<Builder, CodeBlock.Builder> createCollectionGeneration() {
		return collectionGeneration;
	}

	@Override
	public ArrayGeneration<Builder, CodeBlock.Builder> createArrayGeneration() {
		return arrayGeneration;
	}

}

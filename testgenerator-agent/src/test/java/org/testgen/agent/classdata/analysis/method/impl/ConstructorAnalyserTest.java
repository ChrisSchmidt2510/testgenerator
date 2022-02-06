package org.testgen.agent.classdata.analysis.method.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.testgen.agent.classdata.TestHelper;
import org.testgen.agent.classdata.constants.Primitives;
import org.testgen.agent.classdata.instructions.Instructions;
import org.testgen.agent.classdata.model.ClassData;
import org.testgen.agent.classdata.model.FieldData;
import org.testgen.agent.classdata.model.MethodData;
import org.testgen.agent.classdata.model.MethodType;
import org.testgen.agent.classdata.testclasses.Adresse;
import org.testgen.agent.classdata.testclasses.Collections;

import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.MethodInfo;

public class ConstructorAnalyserTest extends TestHelper {

	private ConstructorAnalyser analyser = new ConstructorAnalyser();

	private FieldData fieldOrt = new FieldData.Builder().withDataType("java.lang.String").withName("ort").build();
	private FieldData fieldPlz = new FieldData.Builder().withDataType(Primitives.JAVA_INT).withName("plz").build();
	private FieldData fieldStrasse = new FieldData.Builder().withDataType("java.lang.String").withName("strasse")
			.build();
	private FieldData fieldHausnummer = new FieldData.Builder().withDataType(Primitives.JAVA_SHORT)
			.withName("hausnummer").build();

	@ParameterizedTest
	@MethodSource("org.testgen.agent.classdata.analysis.method.AnalysisTestDataFactory#getAdresseClassData")
	public void testNestedConstructors(ClassData classData) throws NotFoundException, BadBytecode {
		init(Adresse.class, MethodInfo.nameInit, "(Ljava/lang/String;ILjava/lang/String;)V");

		analyser.setClassData(classData);
		analyser.setClassFile(classFile);

		Assertions.assertTrue(analyser.canAnalysisBeApplied(methodInfo));
		Assertions.assertTrue(analyser.hasAnalysisMatched(methodInfo, instructions));

		Map<Integer, FieldData> result = new LinkedHashMap<>();
		result.put(0, fieldOrt);
		result.put(1, fieldPlz);
		result.put(2, fieldStrasse);

		Assertions.assertEquals(result, classData.getConstructor().getConstructorElements());
	}

	@ParameterizedTest
	@MethodSource("org.testgen.agent.classdata.analysis.method.AnalysisTestDataFactory#getAdresseClassData")
	public void testConstructorWithParameters(ClassData classData) throws NotFoundException, BadBytecode {
		init(Adresse.class, MethodInfo.nameInit, "(Ljava/lang/String;I)V");

		analyser.setClassData(classData);
		analyser.setClassFile(classFile);

		System.out.print(Instructions.printCodeArray(codeAttribute.iterator(), constantPool));

		Assertions.assertTrue(analyser.canAnalysisBeApplied(methodInfo));
		Assertions.assertTrue(analyser.hasAnalysisMatched(methodInfo, instructions));

		Map<Integer, FieldData> result = new LinkedHashMap<>();
		result.put(0, fieldOrt);
		result.put(1, fieldPlz);

		Assertions.assertEquals(result, classData.getConstructor().getConstructorElements());
	}

	@ParameterizedTest
	@MethodSource("org.testgen.agent.classdata.analysis.method.AnalysisTestDataFactory#getAdresseClassData")
	public void testConstructorWithSetters(ClassData classData) throws NotFoundException, BadBytecode {
		init(Adresse.class, MethodInfo.nameInit, "(Ljava/lang/String;ILjava/lang/String;S)V");

		classData.addMethod(new MethodData("setOrt", "(Ljava/lang/String;)V", MethodType.REFERENCE_VALUE_SETTER, false),
				fieldOrt);
		classData.addMethod(new MethodData("setPlz", "(I)V", MethodType.REFERENCE_VALUE_SETTER, false), fieldPlz);
		classData.addMethod(
				new MethodData("setStrasse", "(Ljava/lang/String;)V", MethodType.REFERENCE_VALUE_SETTER, false),
				fieldStrasse);
		classData.addMethod(new MethodData("setHausnummer", "(S)V", MethodType.REFERENCE_VALUE_SETTER, false),
				fieldHausnummer);

		analyser.setClassData(classData);
		analyser.setClassFile(classFile);

		Assertions.assertTrue(analyser.canAnalysisBeApplied(methodInfo));
		Assertions.assertTrue(analyser.hasAnalysisMatched(methodInfo, instructions));

		Map<Integer, FieldData> result = new LinkedHashMap<>();
		result.put(0, fieldOrt);
		result.put(1, fieldPlz);
		result.put(2, fieldStrasse);
		result.put(3, fieldHausnummer);

		Assertions.assertEquals(result, classData.getConstructor().getConstructorElements());
	}

	@ParameterizedTest
	@MethodSource("org.testgen.agent.classdata.analysis.method.AnalysisTestDataFactory#getCollectionsClassData")
	public void testAnalyseDefaultConstructor(ClassData classData) throws NotFoundException, BadBytecode {
		init(Collections.class, MethodInfo.nameInit);

		analyser.setClassData(classData);
		analyser.setClassFile(classFile);

		Assertions.assertTrue(analyser.canAnalysisBeApplied(methodInfo));
		Assertions.assertTrue(analyser.hasAnalysisMatched(methodInfo, instructions));

		Assertions.assertTrue(classData.hasDefaultConstructor());
	}
}

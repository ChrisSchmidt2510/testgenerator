package org.testgen.agent.classdata.analysis.method;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.testgen.agent.classdata.TestHelper;
import org.testgen.agent.classdata.model.ClassData;
import org.testgen.agent.classdata.model.MethodType;
import org.testgen.agent.classdata.testclasses.Adresse;
import org.testgen.agent.classdata.testclasses.Collections;
import org.testgen.agent.classdata.testclasses.Person;

import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.MethodInfo;

public class MethodAnalyserTest extends TestHelper {

	@ParameterizedTest
	@MethodSource("org.testgen.agent.classdata.analysis.method.AnalysisTestDataFactory#getAdresseClassData")
	public void testAnalyseGetter(ClassData classData) throws NotFoundException, BadBytecode {
		init(Adresse.class, "getStrasse");

		MethodAnalyser analyser = new MethodAnalyser(classData, classFile);

		analyser.analyseMethod(methodInfo, instructions);

		Assertions.assertEquals(MethodType.REFERENCE_VALUE_GETTER,
				classData.getMethod("getStrasse", "()Ljava/lang/String;").getKey().getMethodType());
	}

	@ParameterizedTest
	@MethodSource("org.testgen.agent.classdata.analysis.method.AnalysisTestDataFactory#getAdresseClassData")
	public void testAnalyseSetter(ClassData classData) throws NotFoundException, BadBytecode {
		init(Adresse.class, "setStrasse");

		MethodAnalyser analyser = new MethodAnalyser(classData, classFile);

		analyser.analyseMethod(methodInfo, instructions);

		Assertions.assertEquals(MethodType.REFERENCE_VALUE_SETTER,
				classData.getMethod("setStrasse", "(Ljava/lang/String;)V").getKey().getMethodType());
	}

	@ParameterizedTest
	@MethodSource("org.testgen.agent.classdata.analysis.method.AnalysisTestDataFactory#getPersonClassData")
	public void testAnalyseImmutableCollectionGetter(ClassData classData) throws NotFoundException, BadBytecode {
		init(Person.class, "getAdressen");

		MethodAnalyser analyser = new MethodAnalyser(classData, classFile);

		analyser.analyseMethod(methodInfo, instructions);

		Assertions.assertEquals(MethodType.IMMUTABLE_GETTER,
				classData.getMethod("getAdressen", "()Ljava/util/List;").getKey().getMethodType());
	}

	@ParameterizedTest
	@MethodSource("org.testgen.agent.classdata.analysis.method.AnalysisTestDataFactory#getCollectionsClassData")
	public void testAnalyseCollectionSetter(ClassData classData) throws NotFoundException, BadBytecode {
		init(Collections.class, "addCollection");

		MethodAnalyser analyser = new MethodAnalyser(classData, classFile);

		analyser.analyseMethod(methodInfo, instructions);

		Assertions.assertEquals(MethodType.COLLECTION_SETTER,
				classData.getMethod("addCollection", "(Ljava/lang/String;)V").getKey().getMethodType());
	}

	@ParameterizedTest
	@MethodSource("org.testgen.agent.classdata.analysis.method.AnalysisTestDataFactory#getCollectionsClassData")
	public void testAnalyseConstructor(ClassData classData) throws NotFoundException, BadBytecode {
		init(Collections.class, MethodInfo.nameInit);

		MethodAnalyser analyser = new MethodAnalyser(classData, classFile);

		analyser.analyseMethod(methodInfo, instructions);

		Assertions.assertTrue(classData.hasDefaultConstructor());
	}
}

package org.testgen.agent.classdata.analysis.method.impl;

import java.util.Map.Entry;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.testgen.agent.classdata.TestHelper;
import org.testgen.agent.classdata.analysis.method.MethodAnalysis;
import org.testgen.agent.classdata.model.ClassData;
import org.testgen.agent.classdata.model.FieldData;
import org.testgen.agent.classdata.model.MethodData;
import org.testgen.agent.classdata.model.MethodType;
import org.testgen.agent.classdata.testclasses.Adresse;
import org.testgen.agent.classdata.testclasses.Person;

import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;

public class NormalSetterAnalyserTest extends TestHelper {

	private MethodAnalysis analyser = new NormalSetterAnalyser();

	@ParameterizedTest
	@MethodSource("org.testgen.agent.classdata.analysis.method.AnalysisTestDataFactory#getAdresseClassData")
	public void testAnalyseGetter(ClassData classData) throws NotFoundException, BadBytecode {
		init(Adresse.class, "getStrasse");

		analyser.setClassData(classData);

		Assertions.assertFalse(analyser.canAnalysisBeApplied(methodInfo));
		Assertions.assertFalse(analyser.hasAnalysisMatched(methodInfo, instructions));
	}

	@ParameterizedTest
	@MethodSource("org.testgen.agent.classdata.analysis.method.AnalysisTestDataFactory#getAdresseClassData")
	public void testAnalyseImmutableGetter(ClassData classData) throws NotFoundException, BadBytecode {
		init(Adresse.class, "getHausnummer");

		analyser.setClassData(classData);

		Assertions.assertFalse(analyser.canAnalysisBeApplied(methodInfo));
		Assertions.assertFalse(analyser.hasAnalysisMatched(methodInfo, instructions));

	}

	@ParameterizedTest
	@MethodSource("org.testgen.agent.classdata.analysis.method.AnalysisTestDataFactory#getAdresseClassData")
	public void testAnalyseSetter(ClassData classData) throws NotFoundException, BadBytecode {
		init(Adresse.class, "setStrasse");

		analyser.setClassData(classData);
		analyser.setClassFile(classFile);

		Assertions.assertTrue(analyser.canAnalysisBeApplied(methodInfo));
		Assertions.assertTrue(analyser.hasAnalysisMatched(methodInfo, instructions));

		Entry<MethodData, FieldData> methodEntry = classData.getMethod("setStrasse", "(Ljava/lang/String;)V");
		Assertions.assertNotNull(methodEntry);

		MethodData method = methodEntry.getKey();
		Assertions.assertEquals(MethodType.REFERENCE_VALUE_SETTER, method.getMethodType());
		Assertions.assertFalse(method.isStatic());

		FieldData fieldData = methodEntry.getValue();

		Assertions.assertEquals("strasse", fieldData.getName());
		Assertions.assertEquals("java.lang.String", fieldData.getDataType());

	}

	@ParameterizedTest
	@MethodSource("org.testgen.agent.classdata.analysis.method.AnalysisTestDataFactory#getPersonClassData")
	public void testAnalyseCollectionSetter(ClassData classData) throws NotFoundException, BadBytecode {
		init(Person.class, "addAdresse");

		analyser.setClassData(classData);

		Assertions.assertTrue(analyser.canAnalysisBeApplied(methodInfo));
		Assertions.assertFalse(analyser.hasAnalysisMatched(methodInfo, instructions));
	}

	@ParameterizedTest
	@MethodSource("org.testgen.agent.classdata.analysis.method.AnalysisTestDataFactory#getPersonClassData")
	public void testAnalyseImmutableCollectionGetter(ClassData classData) throws NotFoundException, BadBytecode {
		init(Person.class, "getAdressen");

		analyser.setClassData(classData);

		Assertions.assertFalse(analyser.canAnalysisBeApplied(methodInfo));
		Assertions.assertFalse(analyser.hasAnalysisMatched(methodInfo, instructions));
	}

}

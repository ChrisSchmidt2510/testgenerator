package org.testgen.agent.classdata.analysis.impl;

import java.util.Map.Entry;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.testgen.agent.classdata.TestHelper;
import org.testgen.agent.classdata.analysis.MethodAnalysis2;
import org.testgen.agent.classdata.model.ClassData;
import org.testgen.agent.classdata.model.FieldData;
import org.testgen.agent.classdata.model.MethodData;
import org.testgen.agent.classdata.model.MethodType;
import org.testgen.agent.classdata.model.SignatureData;
import org.testgen.agent.classdata.testclasses.Adresse;
import org.testgen.agent.classdata.testclasses.Person;

import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;

public class ImmutableCollectionGetterAnalyserTest extends TestHelper {

	private MethodAnalysis2 analyser = new ImmutableCollectionGetterAnalyser();

	@ParameterizedTest
	@MethodSource("org.testgen.agent.classdata.analysis.impl.AnalysisTestDataFactory#getAdresseClassData")
	public void testAnalyseGetter(ClassData classData) throws NotFoundException, BadBytecode {
		init(Adresse.class, "getStrasse");

		analyser.setClassData(classData);

		Assertions.assertFalse(analyser.canAnalysisBeApplied(methodInfo));
		Assertions.assertFalse(analyser.hasAnalysisMatched(methodInfo, instructions));
	}

	@ParameterizedTest
	@MethodSource("org.testgen.agent.classdata.analysis.impl.AnalysisTestDataFactory#getAdresseClassData")
	public void testAnalyseImmutableGetter(ClassData classData) throws NotFoundException, BadBytecode {
		init(Adresse.class, "getHausnummer");

		analyser.setClassData(classData);

		Assertions.assertFalse(analyser.canAnalysisBeApplied(methodInfo));
		Assertions.assertFalse(analyser.hasAnalysisMatched(methodInfo, instructions));
	}

	@ParameterizedTest
	@MethodSource("org.testgen.agent.classdata.analysis.impl.AnalysisTestDataFactory#getAdresseClassData")
	public void testAnalyseSetter(ClassData classData) throws NotFoundException, BadBytecode {
		init(Adresse.class, "setStrasse");

		analyser.setClassData(classData);

		Assertions.assertFalse(analyser.canAnalysisBeApplied(methodInfo));
		Assertions.assertFalse(analyser.hasAnalysisMatched(methodInfo, instructions));
	}

	@ParameterizedTest
	@MethodSource("org.testgen.agent.classdata.analysis.impl.AnalysisTestDataFactory#getPersonClassData")
	public void testAnalyseCollectionSetter(ClassData classData) throws NotFoundException, BadBytecode {
		init(Person.class, "addAdresse");

		analyser.setClassData(classData);

		Assertions.assertFalse(analyser.canAnalysisBeApplied(methodInfo));
		Assertions.assertFalse(analyser.hasAnalysisMatched(methodInfo, instructions));
	}

	@ParameterizedTest
	@MethodSource("org.testgen.agent.classdata.analysis.impl.AnalysisTestDataFactory#getPersonClassData")
	public void testAnalyseImmutableCollectionGetter(ClassData classData) throws NotFoundException, BadBytecode {
		init(Person.class, "getAdressen");

		analyser.setClassData(classData);

		Assertions.assertTrue(analyser.canAnalysisBeApplied(methodInfo));
		Assertions.assertTrue(analyser.hasAnalysisMatched(methodInfo, instructions));

		Entry<MethodData, FieldData> methodEntry = classData.getMethod("getAdressen", "()Ljava/util/List;");
		Assertions.assertNotNull(methodEntry);

		MethodData method = methodEntry.getKey();
		Assertions.assertEquals(MethodType.IMMUTABLE_GETTER, method.getMethodType());
		Assertions.assertFalse(method.isStatic());

		FieldData field = methodEntry.getValue();
		Assertions.assertEquals("adressen", field.getName());
		Assertions.assertEquals("java.util.List", field.getDataType());

		SignatureData signature = new SignatureData("Ljava/util/List;");
		signature.addSubType(new SignatureData("Lorg/testgen/agent/classdata/testclasses/Adresse;"));

		Assertions.assertEquals(signature, field.getSignature());
	}

}

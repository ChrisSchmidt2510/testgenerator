package org.testgen.agent.classdata.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.Serializable;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.testgen.agent.classdata.testclasses.Adresse;
import org.testgen.agent.classdata.testclasses.Person;

public class ClassDataTest {

	private static Stream<Arguments> testIsSerializable() {
		ClassData classData = new ClassData(Person.class.getName());

		ClassData classData1 = new ClassData(Person.class.getName());
		classData1.addInterface(Serializable.class.getName());

		ClassData classData2 = new ClassData(Adresse.class.getName());
		classData2.setSuperClass(classData1);

		return Stream.of(Arguments.of(classData, false), //
				Arguments.of(classData1, true), //
				Arguments.of(classData2, true));
	}

	@ParameterizedTest(name = "ClassData {0} is Serializable: {1}")
	@MethodSource
	public void testIsSerializable(ClassData classData, boolean expected) {
		assertEquals(expected, classData.isSerializable());
	}

}

package org.testgen.agent.transformer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testgen.agent.AgentException;
import org.testgen.agent.classdata.constants.JVMTypes;
import org.testgen.agent.classdata.constants.JavaTypes;
import org.testgen.agent.classdata.instructions.Instruction;
import org.testgen.agent.classdata.instructions.Instructions;
import org.testgen.agent.classdata.testclasses.Adresse;
import org.testgen.agent.classdata.testclasses.ValueTrackerTransformerHelper;
import org.testgen.config.TestgeneratorConfig;
import org.testgen.core.TestgeneratorConstants;
import org.testgen.runtime.classdata.model.descriptor.BasicType;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;
import org.testgen.runtime.valuetracker.ObjectValueTracker;
import org.testgen.runtime.valuetracker.blueprint.Type;
import org.testgen.runtime.valuetracker.storage.ValueStorage;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;

public class ValueTrackerTransformerTest {

	private static Stream<Arguments> testModifyClassFileExceptions() throws NotFoundException {
		ClassPool classPool = ClassPool.getDefault();

		CtClass classAdresse = classPool.get(Adresse.class.getName());
		CtClass classValueTracker = classPool.get(ValueTrackerTransformerHelper.class.getName());

		String valueTrackerName = ValueTrackerTransformerHelper.class.getName().replace(".", "/");

		return Stream.of(
				Arguments.of(Adresse.class.getName().replace(".", "/"), MethodInfo.nameInit, "(Ljava/lang/String;I)V",
						classAdresse, "constructors are currently not supported"),
				Arguments.of(Adresse.class.getName().replace(".", "/"), MethodInfo.nameClinit, "()V", classAdresse,
						"static initializers are currently not supported"),
				Arguments.of(valueTrackerName, "negativeTest", "()V", classValueTracker,
						"negativeTest ()V need to be public or package"),
				Arguments.of(valueTrackerName, "negativeTest2", "()V", classValueTracker,
						"negativeTest2 ()V need to be public or package"),
				Arguments.of(valueTrackerName, "negativeTest3", "()V", classValueTracker,
						"negativeTest3 ()V can`t be abstract"));
	}

	@ParameterizedTest
	@MethodSource
	public void testModifyClassFileExceptions(String testClassName, String name, String descriptor, CtClass clazz,
			String exceptionText) {
		try (MockedStatic<TestgeneratorConfig> mock = Mockito.mockStatic(TestgeneratorConfig.class)) {
			mock.when(TestgeneratorConfig::getClassName).thenReturn(testClassName);
			mock.when(TestgeneratorConfig::getMethodName).thenReturn(name);
			mock.when(TestgeneratorConfig::getMethodDescriptor).thenReturn(descriptor);

			ValueTrackerTransformer transformer = new ValueTrackerTransformer();

			assertThrows(AgentException.class,
					() -> transformer.modifyClassFile(clazz.getName().replace(".", "/"), clazz), exceptionText);
		}

	}

	private static Stream<Arguments> testModifyClassFileSuccessfully() throws NotFoundException {
		ClassPool classPool = ClassPool.getDefault();
		CtClass classAdresse = classPool.get(Adresse.class.getName());
		CtClass classValueTracker = classPool.get(ValueTrackerTransformerHelper.class.getName());

		return Stream.of(
				Arguments.of(Adresse.class.getName().replace(".", "/"), "setStrasse", "(Ljava/lang/String;)V",
						classAdresse),
				Arguments.of(ValueTrackerTransformerHelper.class.getName().replace(".", "/"), "positiveTest", "()V",
						classValueTracker));
	}

	@ParameterizedTest
	@MethodSource
	public void testModifyClassFileSuccessfully(String testClassName, String name, String descriptor, CtClass clazz)
			throws NotFoundException {
		try (MockedStatic<TestgeneratorConfig> mock = Mockito.mockStatic(TestgeneratorConfig.class)) {
			mock.when(TestgeneratorConfig::getClassName).thenReturn(testClassName);
			mock.when(TestgeneratorConfig::getMethodName).thenReturn(name);
			mock.when(TestgeneratorConfig::getMethodDescriptor).thenReturn(descriptor);

			ValueTrackerTransformer transformer = new ValueTrackerTransformer();
			assertTrue(transformer.modifyClassFile(clazz.getName().replace(".", "/"), clazz));
		}
	}

	private static Stream<Arguments> testAddValueTrackingToMethod() throws NotFoundException {
		ClassPool classPool = ClassPool.getDefault();

		CtClass classHelper = classPool.get(ValueTrackerTransformerHelper.class.getName());

		return Stream.of(
				Arguments.of(classHelper, "addPrimitiveToList", "(Ljava/util/List;I)V", true,
						expectedInstructionsAddPrimitiveToList()),
				Arguments.of(classHelper, "isBefore", "(Ljava/time/LocalDate;Ljava/time/LocalDate;)Z", false,
						expectedInstructonsIsBefore()));
	}

	private static List<Instruction> expectedInstructionsAddPrimitiveToList() {
		List<Instruction> instructions = new ArrayList<>();

		instructions.add(new Instruction.Builder().withCodeArrayIndex(0)//
				.withOpcode(Opcode.NEW).withClassRef(JavaTypes.ARRAYLIST).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(3)//
				.withOpcode(Opcode.DUP).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(4)//
				.withOpcode(Opcode.INVOKESPECIAL).withClassRef(JavaTypes.ARRAYLIST).withName(MethodInfo.nameInit)
				.withType("()V").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(7)//
				.withOpcode(Opcode.PUTSTATIC).withClassRef(ValueTrackerTransformerHelper.class.getName())
				.withName(TestgeneratorConstants.FIELDNAME_METHOD_PARAMETER_TABLE).withType(JVMTypes.LIST).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(10)//
				.withOpcode(Opcode.INVOKESTATIC).withClassRef(ValueStorage.class.getName()).withName("getInstance")
				.withType("()Lorg/testgen/runtime/valuetracker/storage/ValueStorage;").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(13)//
				.withOpcode(Opcode.INVOKEVIRTUAL).withClassRef(ValueStorage.class.getName()).withName("pushNewTestData")
				.withType("()V").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(16)//
				.withOpcode(Opcode.INVOKESTATIC).withClassRef(ObjectValueTracker.class.getName())
				.withName("getInstance").withType("()Lorg/testgen/runtime/valuetracker/ObjectValueTracker;").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(19).withOpcode(Opcode.ASTORE_3).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(20).withOpcode(Opcode.GETSTATIC)
				.withClassRef(ValueTrackerTransformerHelper.class.getName())
				.withName(TestgeneratorConstants.FIELDNAME_METHOD_PARAMETER_TABLE).withType(JVMTypes.LIST).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(23).withOpcode(Opcode.NEW)
				.withClassRef(SignatureType.class.getName()).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(26).withOpcode(Opcode.DUP).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(27).withOpcode(Opcode.LDC)
				.withType(JavaTypes.CLASS).withConstantValue(JavaTypes.INTEGER).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(29).withOpcode(Opcode.INVOKESPECIAL)
				.withClassRef(SignatureType.class.getName()).withName(MethodInfo.nameInit)
				.withType("(Ljava/lang/Class;)V").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(32).withOpcode(Opcode.ASTORE)
				.withLocalVariableIndex(4).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(34).withOpcode(Opcode.NEW)
				.withClassRef(SignatureType.class.getName()).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(37).withOpcode(Opcode.DUP).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(38).withOpcode(Opcode.LDC)
				.withType(JavaTypes.CLASS).withConstantValue(JavaTypes.LIST).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(40).withOpcode(Opcode.INVOKESPECIAL)
				.withClassRef(SignatureType.class.getName()).withName(MethodInfo.nameInit)
				.withType("(Ljava/lang/Class;)V").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(43).withOpcode(Opcode.ASTORE)
				.withLocalVariableIndex(5).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(45).withOpcode(Opcode.ALOAD)
				.withLocalVariableIndex(5).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(47).withOpcode(Opcode.ALOAD)
				.withLocalVariableIndex(4).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(49).withOpcode(Opcode.INVOKEVIRTUAL)
				.withClassRef(SignatureType.class.getName()).withName("addSubType")
				.withType("(Lorg/testgen/runtime/classdata/model/descriptor/SignatureType;)V").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(52).withOpcode(Opcode.ALOAD)
				.withLocalVariableIndex(5).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(54).withOpcode(Opcode.INVOKEINTERFACE)
				.withClassRef(JavaTypes.LIST).withName(JavaTypes.COLLECTION_METHOD_ADD)
				.withType("(Ljava/lang/Object;)Z").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(59).withOpcode(Opcode.POP).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(60).withOpcode(Opcode.ALOAD_3).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(61).withOpcode(Opcode.ALOAD_1).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(62).withOpcode(Opcode.LDC)
				.withType(JavaTypes.STRING).withConstantValue("values").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(64).withOpcode(Opcode.GETSTATIC)
				.withClassRef(Type.class.getName()).withName(Type.METHOD_PARAMETER.name())
				.withType("Lorg/testgen/runtime/valuetracker/blueprint/Type;").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(67).withOpcode(Opcode.INVOKEVIRTUAL)
				.withClassRef(ObjectValueTracker.class.getName()).withName("track")
				.withType("(Ljava/lang/Object;Ljava/lang/String;Lorg/testgen/runtime/valuetracker/blueprint/Type;)V")
				.build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(70).withOpcode(Opcode.GETSTATIC)
				.withClassRef(ValueTrackerTransformerHelper.class.getName())
				.withName(TestgeneratorConstants.FIELDNAME_METHOD_PARAMETER_TABLE).withType(JVMTypes.LIST).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(73).withOpcode(Opcode.GETSTATIC)
				.withClassRef(JavaTypes.INTEGER).withName(JVMTypes.WRAPPER_CLASSES_FIELD_TYPE).withType(JVMTypes.CLASS)
				.build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(76).withOpcode(Opcode.INVOKESTATIC)
				.withClassRef(BasicType.class.getName()).withName("of")
				.withType("(Ljava/lang/Class;)Lorg/testgen/runtime/classdata/model/descriptor/BasicType;").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(79).withOpcode(Opcode.INVOKEINTERFACE)
				.withClassRef(JavaTypes.LIST).withName(JavaTypes.COLLECTION_METHOD_ADD)
				.withType("(Ljava/lang/Object;)Z").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(84).withOpcode(Opcode.POP).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(85).withOpcode(Opcode.ALOAD_3).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(86).withOpcode(Opcode.ILOAD_2).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(87).withOpcode(Opcode.INVOKESTATIC)
				.withClassRef(JavaTypes.INTEGER).withName(JVMTypes.WRAPPER_METHOD_VALUE_OF)
				.withType(JVMTypes.INTEGER_METHOD_VALUE_OF_DESC).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(90).withOpcode(Opcode.LDC)
				.withType(JavaTypes.STRING).withConstantValue("anotherValue").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(92).withOpcode(Opcode.GETSTATIC)
				.withClassRef(Type.class.getName()).withName(Type.METHOD_PARAMETER.name())
				.withType("Lorg/testgen/runtime/valuetracker/blueprint/Type;").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(95).withOpcode(Opcode.INVOKEVIRTUAL)
				.withClassRef(ObjectValueTracker.class.getName()).withName("track")
				.withType("(Ljava/lang/Object;Ljava/lang/String;Lorg/testgen/runtime/valuetracker/blueprint/Type;)V")
				.build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(98).withOpcode(Opcode.ALOAD_3).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(99).withOpcode(Opcode.ALOAD_0).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(100).withOpcode(Opcode.LDC)
				.withType(JavaTypes.STRING).withConstantValue("valueTrackerTransformerHelper").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(102).withOpcode(Opcode.GETSTATIC)
				.withClassRef(Type.class.getName()).withName(Type.TESTOBJECT.name())
				.withType("Lorg/testgen/runtime/valuetracker/blueprint/Type;").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(105).withOpcode(Opcode.INVOKEVIRTUAL)
				.withClassRef(ObjectValueTracker.class.getName()).withName("track")
				.withType("(Ljava/lang/Object;Ljava/lang/String;Lorg/testgen/runtime/valuetracker/blueprint/Type;)V")
				.build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(108).withOpcode(Opcode.ICONST_1).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(109).withOpcode(Opcode.INVOKESTATIC)
				.withClassRef(TestgeneratorConfig.class.getName()).withName("setProxyTracking").withType("(Z)V")
				.build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(112).withOpcode(Opcode.ICONST_1).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(113).withOpcode(Opcode.INVOKESTATIC)
				.withClassRef(TestgeneratorConfig.class.getName()).withName("setFieldTracking").withType("(Z)V")
				.build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(116).withOpcode(Opcode.ALOAD_1).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(117).withOpcode(Opcode.ILOAD_2).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(118).withOpcode(Opcode.INVOKESTATIC)
				.withClassRef(JavaTypes.INTEGER).withName(JVMTypes.WRAPPER_METHOD_VALUE_OF)
				.withType(JVMTypes.INTEGER_METHOD_VALUE_OF_DESC).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(121).withOpcode(Opcode.INVOKEINTERFACE)
				.withClassRef(JavaTypes.LIST).withName(JavaTypes.COLLECTION_METHOD_ADD)
				.withType("(Ljava/lang/Object;)Z").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(126).withOpcode(Opcode.POP).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(127).withOpcode(Opcode.RETURN).build());

		return instructions;
	}

	private static List<Instruction> expectedInstructonsIsBefore() {
		List<Instruction> instructions = new ArrayList<>();

		instructions.add(new Instruction.Builder().withCodeArrayIndex(0)//
				.withOpcode(Opcode.NEW).withClassRef(JavaTypes.ARRAYLIST).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(3)//
				.withOpcode(Opcode.DUP).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(4)//
				.withOpcode(Opcode.INVOKESPECIAL).withClassRef(JavaTypes.ARRAYLIST).withName(MethodInfo.nameInit)
				.withType("()V").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(7)//
				.withOpcode(Opcode.PUTSTATIC).withClassRef(ValueTrackerTransformerHelper.class.getName())
				.withName(TestgeneratorConstants.FIELDNAME_METHOD_PARAMETER_TABLE).withType(JVMTypes.LIST).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(10)//
				.withOpcode(Opcode.INVOKESTATIC).withClassRef(ValueStorage.class.getName()).withName("getInstance")
				.withType("()Lorg/testgen/runtime/valuetracker/storage/ValueStorage;").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(13)//
				.withOpcode(Opcode.INVOKEVIRTUAL).withClassRef(ValueStorage.class.getName()).withName("pushNewTestData")
				.withType("()V").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(16)//
				.withOpcode(Opcode.INVOKESTATIC).withClassRef(ObjectValueTracker.class.getName())
				.withName("getInstance").withType("()Lorg/testgen/runtime/valuetracker/ObjectValueTracker;").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(19).withOpcode(Opcode.ASTORE_2).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(20).withOpcode(Opcode.GETSTATIC)
				.withClassRef(ValueTrackerTransformerHelper.class.getName())
				.withName(TestgeneratorConstants.FIELDNAME_METHOD_PARAMETER_TABLE).withType(JVMTypes.LIST).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(23).withOpcode(Opcode.LDC)
				.withType(JavaTypes.CLASS).withConstantValue(JavaTypes.LOCALDATE).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(25).withOpcode(Opcode.INVOKESTATIC)
				.withClassRef(BasicType.class.getName()).withName("of")
				.withType("(Ljava/lang/Class;)Lorg/testgen/runtime/classdata/model/descriptor/BasicType;").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(28).withOpcode(Opcode.INVOKEINTERFACE)
				.withClassRef(JavaTypes.LIST).withName("add").withType("(Ljava/lang/Object;)Z").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(33).withOpcode(Opcode.POP).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(34).withOpcode(Opcode.ALOAD_2).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(35).withOpcode(Opcode.ALOAD_0).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(36).withOpcode(Opcode.LDC)
				.withType(JavaTypes.STRING).withConstantValue("first").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(38).withOpcode(Opcode.GETSTATIC)
				.withClassRef(Type.class.getName()).withName(Type.METHOD_PARAMETER.name())
				.withType("Lorg/testgen/runtime/valuetracker/blueprint/Type;").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(41).withOpcode(Opcode.INVOKEVIRTUAL)
				.withClassRef(ObjectValueTracker.class.getName()).withName("track")
				.withType("(Ljava/lang/Object;Ljava/lang/String;Lorg/testgen/runtime/valuetracker/blueprint/Type;)V")
				.build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(44).withOpcode(Opcode.GETSTATIC)
				.withClassRef(ValueTrackerTransformerHelper.class.getName())
				.withName(TestgeneratorConstants.FIELDNAME_METHOD_PARAMETER_TABLE).withType(JVMTypes.LIST).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(47).withOpcode(Opcode.LDC)
				.withType(JavaTypes.CLASS).withConstantValue(JavaTypes.LOCALDATE).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(49).withOpcode(Opcode.INVOKESTATIC)
				.withClassRef(BasicType.class.getName()).withName("of")
				.withType("(Ljava/lang/Class;)Lorg/testgen/runtime/classdata/model/descriptor/BasicType;").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(52).withOpcode(Opcode.INVOKEINTERFACE)
				.withClassRef(JavaTypes.LIST).withName("add").withType("(Ljava/lang/Object;)Z").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(57).withOpcode(Opcode.POP).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(58).withOpcode(Opcode.ALOAD_2).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(59).withOpcode(Opcode.ALOAD_1).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(60).withOpcode(Opcode.LDC)
				.withType(JavaTypes.STRING).withConstantValue("second").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(62).withOpcode(Opcode.GETSTATIC)
				.withClassRef(Type.class.getName()).withName(Type.METHOD_PARAMETER.name())
				.withType("Lorg/testgen/runtime/valuetracker/blueprint/Type;").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(65).withOpcode(Opcode.INVOKEVIRTUAL)
				.withClassRef(ObjectValueTracker.class.getName()).withName("track")
				.withType("(Ljava/lang/Object;Ljava/lang/String;Lorg/testgen/runtime/valuetracker/blueprint/Type;)V")
				.build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(68).withOpcode(Opcode.ICONST_1).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(69).withOpcode(Opcode.INVOKESTATIC)
				.withClassRef(TestgeneratorConfig.class.getName()).withName("setProxyTracking").withType("(Z)V")
				.build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(72).withOpcode(Opcode.ALOAD_0).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(73).withOpcode(Opcode.ALOAD_1).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(74).withOpcode(Opcode.INVOKEVIRTUAL)
				.withClassRef(LocalDate.class.getName()).withName("isBefore")
				.withType("(Ljava/time/chrono/ChronoLocalDate;)Z").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(77).withOpcode(Opcode.IRETURN).build());

		return instructions;
	}

	@ParameterizedTest
	@MethodSource
	public void testAddValueTrackingToMethod(CtClass clazz, String method, String descriptor, boolean withFieldTracking,
			List<Instruction> expectedInstructions) throws BadBytecode, NotFoundException {
		try (MockedStatic<TestgeneratorConfig> mock = Mockito.mockStatic(TestgeneratorConfig.class)) {
			mock.when(TestgeneratorConfig::getMethodName).thenReturn(method);
			mock.when(TestgeneratorConfig::getMethodDescriptor).thenReturn(descriptor);
			mock.when(TestgeneratorConfig::traceReadFieldAccess).thenReturn(withFieldTracking);

			MethodInfo methodInfo = clazz.getMethod(method, descriptor).getMethodInfo();

			ValueTrackerTransformer transformer = new ValueTrackerTransformer();
			transformer.codeAttribute = methodInfo.getCodeAttribute();

			transformer.addValueTrackingToMethod(clazz, methodInfo);

			assertEquals(expectedInstructions, Instructions.getAllInstructions(methodInfo));
		}
	}

}

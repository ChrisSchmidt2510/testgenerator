package org.testgen.agent.classdata.modification;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.PrintStream;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testgen.agent.classdata.constants.JVMTypes;
import org.testgen.agent.classdata.constants.JavaTypes;
import org.testgen.agent.classdata.instructions.Instruction;
import org.testgen.agent.classdata.instructions.Instructions;
import org.testgen.agent.classdata.testclasses.ValueTrackerTransformerHelper;
import org.testgen.config.TestgeneratorConfig;
import org.testgen.core.TestgeneratorConstants;
import org.testgen.runtime.generation.GenerationEntryPoint;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.ExceptionTable;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;

public class TestGenerationAdderTest {

	private static Stream<Arguments> testAddTestgenerationToMethod() throws NotFoundException {
		ClassPool classPool = ClassPool.getDefault();

		CtClass classValueTracker = classPool.get(ValueTrackerTransformerHelper.class.getName());

		ConstPool constantPool = classValueTracker.getClassFile().getConstPool();

		return Stream.of(Arguments.of(classValueTracker,
				classValueTracker.getMethod("addPrimitiveToList", "(Ljava/util/List;I)V").getMethodInfo(),
				expectedInstructionsAddPrimitiveToList(), expectedExceptionTableAddPrimitivesToList(constantPool)),
				Arguments.of(classValueTracker,
						classValueTracker.getMethod("cutString", "(Ljava/lang/String;)Ljava/lang/String;")
								.getMethodInfo(),
						expectedInstructionsCutString(), expectedExceptionTableCutString(constantPool)),
				Arguments.of(classValueTracker,
						classValueTracker.getMethod("finallyClause", "()Ljava/time/LocalDate;").getMethodInfo(),
						expectedInstructionsFinallyClause(), expectedExceptionTableFinallyClause(constantPool)));
	}

	private static ExceptionTable expectedExceptionTableAddPrimitivesToList(ConstPool constantPool) {
		ExceptionTable exceptionTable = new ExceptionTable(constantPool);
		exceptionTable.add(0, 11, 14, 0);

		return exceptionTable;
	}

	private static List<Instruction> expectedInstructionsAddPrimitiveToList() {
		List<Instruction> instructions = new ArrayList<>();

		instructions.add(new Instruction.Builder().withCodeArrayIndex(0).withOpcode(Opcode.ALOAD_1).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(1).withOpcode(Opcode.ILOAD_2).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(2).withOpcode(Opcode.INVOKESTATIC)
				.withClassRef(JavaTypes.INTEGER).withName(JVMTypes.WRAPPER_METHOD_VALUE_OF)
				.withType(JVMTypes.INTEGER_METHOD_VALUE_OF_DESC).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(5).withOpcode(Opcode.INVOKEINTERFACE)
				.withClassRef(JavaTypes.LIST).withName(JavaTypes.COLLECTION_METHOD_ADD)
				.withType("(Ljava/lang/Object;)Z").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(10).withOpcode(Opcode.POP).build());
		instructions
				.add(new Instruction.Builder().withCodeArrayIndex(11).withOpcode(Opcode.GOTO).withOffset(17).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(14).withOpcode(Opcode.ASTORE_3).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(15).withOpcode(Opcode.LDC)
				.withType(JavaTypes.CLASS).withConstantValue(ValueTrackerTransformerHelper.class.getName()).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(17).withOpcode(Opcode.ICONST_0).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(18).withOpcode(Opcode.LDC)
				.withType(JavaTypes.STRING).withConstantValue("addPrimitiveToList").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(20).withOpcode(Opcode.GETSTATIC)
				.withClassRef(ValueTrackerTransformerHelper.class.getName())
				.withName(TestgeneratorConstants.FIELDNAME_METHOD_PARAMETER_TABLE).withType(JVMTypes.LIST).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(23).withOpcode(Opcode.INVOKESTATIC)
				.withClassRef(GenerationEntryPoint.class.getName()).withName("generate")
				.withType("(Ljava/lang/Class;ZLjava/lang/String;Ljava/util/List;)V").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(26).withOpcode(Opcode.ALOAD_3).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(27).withOpcode(Opcode.ATHROW).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(28).withOpcode(Opcode.LDC)
				.withType(JavaTypes.CLASS).withConstantValue(ValueTrackerTransformerHelper.class.getName()).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(30).withOpcode(Opcode.ICONST_0).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(31).withOpcode(Opcode.LDC)
				.withType(JavaTypes.STRING).withConstantValue("addPrimitiveToList").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(33).withOpcode(Opcode.GETSTATIC)
				.withClassRef(ValueTrackerTransformerHelper.class.getName())
				.withName(TestgeneratorConstants.FIELDNAME_METHOD_PARAMETER_TABLE).withType(JVMTypes.LIST).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(36).withOpcode(Opcode.INVOKESTATIC)
				.withClassRef(GenerationEntryPoint.class.getName()).withName("generate")
				.withType("(Ljava/lang/Class;ZLjava/lang/String;Ljava/util/List;)V").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(39).withOpcode(Opcode.RETURN).build());

		return instructions;
	}

	private static ExceptionTable expectedExceptionTableCutString(ConstPool constantPool) {
		ExceptionTable exceptionTable = new ExceptionTable(constantPool);
		exceptionTable.add(0, 19, 50, 0);
		exceptionTable.add(32, 36, 50, 0);

		return exceptionTable;
	}

	private static List<Instruction> expectedInstructionsCutString() {
		List<Instruction> instructions = new ArrayList<>();

		instructions.add(new Instruction.Builder().withCodeArrayIndex(0).withOpcode(Opcode.ALOAD_1).build());
		instructions.add(
				new Instruction.Builder().withCodeArrayIndex(1).withOpcode(Opcode.IFNONNULL).withOffset(11).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(4).withOpcode(Opcode.NEW)
				.withClassRef(NullPointerException.class.getName()).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(7).withOpcode(Opcode.DUP).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(8).withOpcode(Opcode.INVOKESPECIAL)
				.withClassRef(NullPointerException.class.getName()).withName(MethodInfo.nameInit).withType("()V")
				.build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(11).withOpcode(Opcode.ATHROW).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(12).withOpcode(Opcode.ALOAD_1).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(13).withOpcode(Opcode.INVOKEVIRTUAL)
				.withClassRef(JavaTypes.STRING).withName("isEmpty").withType("()Z").build());
		instructions
				.add(new Instruction.Builder().withCodeArrayIndex(16).withOpcode(Opcode.IFEQ).withOffset(16).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(19).withOpcode(Opcode.LDC)
				.withType(JavaTypes.CLASS).withConstantValue(ValueTrackerTransformerHelper.class.getName()).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(21).withOpcode(Opcode.ICONST_0).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(22).withOpcode(Opcode.LDC)
				.withType(JavaTypes.STRING).withConstantValue("cutString").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(24).withOpcode(Opcode.GETSTATIC)
				.withClassRef(ValueTrackerTransformerHelper.class.getName())
				.withName(TestgeneratorConstants.FIELDNAME_METHOD_PARAMETER_TABLE).withType(JVMTypes.LIST).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(27).withOpcode(Opcode.INVOKESTATIC)
				.withClassRef(GenerationEntryPoint.class.getName()).withName("generate")
				.withType("(Ljava/lang/Class;ZLjava/lang/String;Ljava/util/List;)V").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(30).withOpcode(Opcode.ALOAD_1).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(31).withOpcode(Opcode.ARETURN).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(32).withOpcode(Opcode.ALOAD_1).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(33).withOpcode(Opcode.INVOKEVIRTUAL)
				.withClassRef(JavaTypes.STRING).withName("trim").withType("()Ljava/lang/String;").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(36).withOpcode(Opcode.ASTORE_3).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(37).withOpcode(Opcode.LDC)
				.withType(JavaTypes.CLASS).withConstantValue(ValueTrackerTransformerHelper.class.getName()).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(39).withOpcode(Opcode.ICONST_0).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(40).withOpcode(Opcode.LDC)
				.withType(JavaTypes.STRING).withConstantValue("cutString").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(42).withOpcode(Opcode.GETSTATIC)
				.withClassRef(ValueTrackerTransformerHelper.class.getName())
				.withName(TestgeneratorConstants.FIELDNAME_METHOD_PARAMETER_TABLE).withType(JVMTypes.LIST).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(45).withOpcode(Opcode.INVOKESTATIC)
				.withClassRef(GenerationEntryPoint.class.getName()).withName("generate")
				.withType("(Ljava/lang/Class;ZLjava/lang/String;Ljava/util/List;)V").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(48).withOpcode(Opcode.ALOAD_3).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(49).withOpcode(Opcode.ARETURN).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(50).withOpcode(Opcode.ASTORE_2).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(51).withOpcode(Opcode.LDC)
				.withType(JavaTypes.CLASS).withConstantValue(ValueTrackerTransformerHelper.class.getName()).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(53).withOpcode(Opcode.ICONST_0).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(54).withOpcode(Opcode.LDC)
				.withType(JavaTypes.STRING).withConstantValue("cutString").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(56).withOpcode(Opcode.GETSTATIC)
				.withClassRef(ValueTrackerTransformerHelper.class.getName())
				.withName(TestgeneratorConstants.FIELDNAME_METHOD_PARAMETER_TABLE).withType(JVMTypes.LIST).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(59).withOpcode(Opcode.INVOKESTATIC)
				.withClassRef(GenerationEntryPoint.class.getName()).withName("generate")
				.withType("(Ljava/lang/Class;ZLjava/lang/String;Ljava/util/List;)V").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(62).withOpcode(Opcode.ALOAD_2).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(63).withOpcode(Opcode.ATHROW).build());

		return instructions;
	}

	private static ExceptionTable expectedExceptionTableFinallyClause(ConstPool constantPool) {
		ExceptionTable exceptionTable = new ExceptionTable(constantPool);
		exceptionTable.add(0, 6, 27, 0);

		return exceptionTable;
	}

	private static List<Instruction> expectedInstructionsFinallyClause() {
		List<Instruction> instructions = new ArrayList<>();
		instructions.add(new Instruction.Builder().withCodeArrayIndex(0).withOpcode(Opcode.LDC)
				.withType(JavaTypes.STRING).withConstantValue("2021.12.31").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(2).withOpcode(Opcode.INVOKESTATIC)
				.withClassRef(LocalDate.class.getName()).withName("parse")
				.withType("(Ljava/lang/CharSequence;)Ljava/time/LocalDate;").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(5).withOpcode(Opcode.ASTORE_1).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(6).withOpcode(Opcode.GETSTATIC)
				.withClassRef(System.class.getName()).withName("out").withType("Ljava/io/PrintStream;").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(9).withOpcode(Opcode.LDC)
				.withType(JavaTypes.STRING).withConstantValue("Hello World").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(11).withOpcode(Opcode.INVOKEVIRTUAL)
				.withClassRef(PrintStream.class.getName()).withName("println").withType("(Ljava/lang/String;)V")
				.build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(14).withOpcode(Opcode.LDC)
				.withType(JavaTypes.CLASS).withConstantValue(ValueTrackerTransformerHelper.class.getName()).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(16).withOpcode(Opcode.ICONST_0).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(17).withOpcode(Opcode.LDC)
				.withType(JavaTypes.STRING).withConstantValue("finallyClause").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(19).withOpcode(Opcode.GETSTATIC)
				.withClassRef(ValueTrackerTransformerHelper.class.getName())
				.withName(TestgeneratorConstants.FIELDNAME_METHOD_PARAMETER_TABLE).withType(JVMTypes.LIST).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(22).withOpcode(Opcode.INVOKESTATIC)
				.withClassRef(GenerationEntryPoint.class.getName()).withName("generate")
				.withType("(Ljava/lang/Class;ZLjava/lang/String;Ljava/util/List;)V").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(25).withOpcode(Opcode.ALOAD_1).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(26).withOpcode(Opcode.ARETURN).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(27).withOpcode(Opcode.ASTORE_2).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(28).withOpcode(Opcode.LDC)
				.withType(JavaTypes.CLASS).withConstantValue(ValueTrackerTransformerHelper.class.getName()).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(30).withOpcode(Opcode.ICONST_0).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(31).withOpcode(Opcode.LDC)
				.withType(JavaTypes.STRING).withConstantValue("finallyClause").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(33).withOpcode(Opcode.GETSTATIC)
				.withClassRef(ValueTrackerTransformerHelper.class.getName())
				.withName(TestgeneratorConstants.FIELDNAME_METHOD_PARAMETER_TABLE).withType(JVMTypes.LIST).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(36).withOpcode(Opcode.INVOKESTATIC)
				.withClassRef(GenerationEntryPoint.class.getName()).withName("generate")
				.withType("(Ljava/lang/Class;ZLjava/lang/String;Ljava/util/List;)V").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(39).withOpcode(Opcode.GETSTATIC)
				.withClassRef(System.class.getName()).withName("out").withType("Ljava/io/PrintStream;").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(42).withOpcode(Opcode.LDC)
				.withType(JavaTypes.STRING).withConstantValue("Hello World").build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(44).withOpcode(Opcode.INVOKEVIRTUAL)
				.withClassRef(PrintStream.class.getName()).withName("println").withType("(Ljava/lang/String;)V")
				.build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(47).withOpcode(Opcode.ALOAD_2).build());
		instructions.add(new Instruction.Builder().withCodeArrayIndex(48).withOpcode(Opcode.ATHROW).build());

		return instructions;
	}

	@ParameterizedTest
	@MethodSource
	public void testAddTestgenerationToMethod(CtClass clazz, MethodInfo method, List<Instruction> expectedInstructions,
			ExceptionTable expectedExceptionTable) throws BadBytecode {

		try (MockedStatic<TestgeneratorConfig> mock = Mockito.mockStatic(TestgeneratorConfig.class)) {
			mock.when(TestgeneratorConfig::getMethodName).thenReturn(method.getName());

			CodeAttribute codeAttribute = method.getCodeAttribute();

			ExceptionTable exceptionTable = codeAttribute.getExceptionTable();

			TestGenerationAdder testGenerationAdder = new TestGenerationAdder(clazz, codeAttribute,
					Modifier.isStatic(method.getAccessFlags()));
			testGenerationAdder.addTestgenerationToMethod(method);

			assertEquals(expectedInstructions, Instructions.getAllInstructions(method));
			compareExceptionTable(expectedExceptionTable, exceptionTable);
		}
	}

	private void compareExceptionTable(ExceptionTable expectedExceptionTable, ExceptionTable exceptionTable) {
		assertEquals(expectedExceptionTable.size(), exceptionTable.size());

		for (int i = 0; i < expectedExceptionTable.size(); i++) {
			assertEquals(expectedExceptionTable.startPc(i), exceptionTable.startPc(i));
			assertEquals(expectedExceptionTable.endPc(i), exceptionTable.endPc(i));
			assertEquals(expectedExceptionTable.handlerPc(i), exceptionTable.handlerPc(i));
			assertEquals(expectedExceptionTable.catchType(i), exceptionTable.catchType(i));
		}
	}
	// method with finally clause
}

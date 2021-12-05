package org.testgen.agent.classdata.instructions.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.testgen.agent.classdata.TestHelper;
import org.testgen.agent.classdata.constants.JavaTypes;
import org.testgen.agent.classdata.constants.Primitives;
import org.testgen.agent.classdata.instructions.Instruction;
import org.testgen.agent.classdata.instructions.filter.ForwardInstructionFilter.OperandStack;

import javassist.bytecode.Opcode;

public class ForwardInstructionFilterTest extends TestHelper {

	private static Stream<Arguments> testIsDoubleOrLong() {
		return Stream.of(//
				Arguments.of(Primitives.JAVA_DOUBLE, true),
				Arguments.of(Primitives.JVM_DOUBLE, true),//
				Arguments.of(Primitives.JAVA_LONG, true),//
				Arguments.of(Primitives.JVM_LONG, true),//
				Arguments.of(Primitives.JAVA_INT, false),//
				Arguments.of(null, false));
	}

	@ParameterizedTest(name = "{0} is a double or long: {1}")
	@MethodSource
	public void testIsDoubleOrLong(String type, boolean result) {
		assertEquals(result, ForwardInstructionFilter.isDoubleOrLong(type));
	}
	
	private static Stream<Arguments> testGetArrayComponentType(){
		return Stream.of(Arguments.of(Opcode.IALOAD, Primitives.JAVA_INT),
				Arguments.of(Opcode.LALOAD, Primitives.JAVA_LONG),//
				Arguments.of(Opcode.FALOAD, Primitives.JAVA_FLOAT),//
				Arguments.of(Opcode.DALOAD, Primitives.JAVA_DOUBLE),//
				Arguments.of(Opcode.AALOAD, JavaTypes.OBJECT),//
				Arguments.of(Opcode.BALOAD, Primitives.JAVA_BYTE),//
				Arguments.of(Opcode.CALOAD, Primitives.JAVA_CHAR),//
				Arguments.of(Opcode.SALOAD, Primitives.JAVA_SHORT));
	}

	@ParameterizedTest
	@MethodSource
	public void testGetArrayComponentType(int opcode, String returnType) {
		assertEquals(returnType, ForwardInstructionFilter.getArrayComponentType(opcode));
	}
	
	@Test
	public void testGetArrayComponentTypeException() {
		assertThrows(IllegalArgumentException.class, () -> ForwardInstructionFilter.getArrayComponentType(Opcode.ALOAD_0), "invalid array load instruction");
	}
	
	private static Stream<Arguments> testPushLoadInstructionOnStack(){
		return Stream.of(Arguments.of(Opcode.ALOAD, JavaTypes.OBJECT),//
				Arguments.of(Opcode.ALOAD_0, JavaTypes.OBJECT),//
				Arguments.of(Opcode.ALOAD_1, JavaTypes.OBJECT),//
				Arguments.of(Opcode.ALOAD_2, JavaTypes.OBJECT),//
				Arguments.of(Opcode.ALOAD_3, JavaTypes.OBJECT),//
				
				Arguments.of(Opcode.ILOAD, Primitives.JAVA_INT),//
				Arguments.of(Opcode.ILOAD_0, Primitives.JAVA_INT),//
				Arguments.of(Opcode.ILOAD_1, Primitives.JAVA_INT),//
				Arguments.of(Opcode.ILOAD_2, Primitives.JAVA_INT),//
				Arguments.of(Opcode.ILOAD_3, Primitives.JAVA_INT),//
				Arguments.of(Opcode.ICONST_0, Primitives.JAVA_INT),//
				Arguments.of(Opcode.ICONST_1, Primitives.JAVA_INT),//
				Arguments.of(Opcode.ICONST_2, Primitives.JAVA_INT),//
				Arguments.of(Opcode.ICONST_3, Primitives.JAVA_INT),//
				Arguments.of(Opcode.ICONST_4, Primitives.JAVA_INT),//
				Arguments.of(Opcode.ICONST_5, Primitives.JAVA_INT),//
				Arguments.of(Opcode.ICONST_M1, Primitives.JAVA_INT),//
				Arguments.of(Opcode.BIPUSH, Primitives.JAVA_INT),//
				Arguments.of(Opcode.SIPUSH, Primitives.JAVA_INT),//
				
				Arguments.of(Opcode.FLOAD, Primitives.JAVA_FLOAT),//
				Arguments.of(Opcode.FLOAD_0, Primitives.JAVA_FLOAT),//
				Arguments.of(Opcode.FLOAD_1, Primitives.JAVA_FLOAT),//
				Arguments.of(Opcode.FLOAD_2, Primitives.JAVA_FLOAT),//
				Arguments.of(Opcode.FLOAD_3, Primitives.JAVA_FLOAT),//
				Arguments.of(Opcode.FCONST_0, Primitives.JAVA_FLOAT),//
				Arguments.of(Opcode.FCONST_1, Primitives.JAVA_FLOAT),//
				Arguments.of(Opcode.FCONST_2, Primitives.JAVA_FLOAT),//
				
				Arguments.of(Opcode.DLOAD, Primitives.JAVA_DOUBLE),//
				Arguments.of(Opcode.DLOAD_0, Primitives.JAVA_DOUBLE),//
				Arguments.of(Opcode.DLOAD_1, Primitives.JAVA_DOUBLE),//
				Arguments.of(Opcode.DLOAD_2, Primitives.JAVA_DOUBLE),//
				Arguments.of(Opcode.DLOAD_3, Primitives.JAVA_DOUBLE),//
				Arguments.of(Opcode.DCONST_0, Primitives.JAVA_DOUBLE),//
				Arguments.of(Opcode.DCONST_1, Primitives.JAVA_DOUBLE),//
				
				Arguments.of(Opcode.LLOAD, Primitives.JAVA_LONG),//
				Arguments.of(Opcode.LLOAD_0, Primitives.JAVA_LONG),//
				Arguments.of(Opcode.LLOAD_1, Primitives.JAVA_LONG),//
				Arguments.of(Opcode.LLOAD_2, Primitives.JAVA_LONG),//
				Arguments.of(Opcode.LLOAD_3, Primitives.JAVA_LONG),//
				Arguments.of(Opcode.LCONST_0, Primitives.JAVA_LONG),//
				Arguments.of(Opcode.LCONST_1, Primitives.JAVA_LONG)
				);
	}
	
	@ParameterizedTest
	@MethodSource
	public void testPushLoadInstructionOnStack(int opcode, String resultType) {
		Instruction instruction = new Instruction.Builder().withOpcode(opcode).build();
		
		OperandStack operandStack = new OperandStack();
		
		ForwardInstructionFilter.pushLoadInstructionOnStack(instruction, operandStack);
		assertEquals(resultType, operandStack.get(0));
	}
	
	
	
	
}

package org.testgen.agent.classdata.instructions;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javassist.bytecode.Opcode;

public class InstructionsTest {

	@Test
	public void testGetMethodParams() {
		List<String> paramsWithPrimitivesAtEnd = Instructions.getMethodParams("(Ljava/lang/String;IZC)V");
		Assertions.assertEquals(Arrays.asList("Ljava/lang/String;", "I", "Z", "C"), paramsWithPrimitivesAtEnd);

		List<String> paramsWithPrimitivesAtStart = Instructions.getMethodParams("(IZCLjava/lang/String;)V");
		Assertions.assertEquals(Arrays.asList("I", "Z", "C", "Ljava/lang/String;"), paramsWithPrimitivesAtStart);

		List<String> paramsWithoutPrimitives = Instructions.getMethodParams("(Ljava/lang/String;Ljava/util/List;)V");
		Assertions.assertEquals(Arrays.asList("Ljava/lang/String;", "Ljava/util/List;"), paramsWithoutPrimitives);

		List<String> paramsWithOnlyPrimitives = Instructions.getMethodParams("(IIDJ)V");
		Assertions.assertEquals(Arrays.asList("I", "I", "D", "J"), paramsWithOnlyPrimitives);

		List<String> paramsWithPrimitiveArray = Instructions.getMethodParams("([ILjava/lang/String;Z)");
		Assertions.assertEquals(Arrays.asList("[I", "Ljava/lang/String;", "Z"), paramsWithPrimitiveArray);

		List<String> paramsWithMultiDimArray = Instructions.getMethodParams("([[[JLjava/util/List;I)");
		Assertions.assertEquals(Arrays.asList("[[[J", "Ljava/util/List;", "I"), paramsWithMultiDimArray);

		List<String> paramsWithReferenceArray = Instructions.getMethodParams("([[Ljava/lang/String;Ljava/util/List;)");
		Assertions.assertEquals(Arrays.asList("[[Ljava/lang/String;", "Ljava/util/List;"), paramsWithReferenceArray);
	}

	@Test
	public void testIsPrimitiveCast() {
		Assertions.assertTrue(Instructions.isPrimitiveCast(new Instruction.Builder().withOpcode(Opcode.I2B).build()));
	}

}

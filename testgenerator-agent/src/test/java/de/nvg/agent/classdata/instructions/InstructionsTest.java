package de.nvg.agent.classdata.instructions;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import javassist.bytecode.Opcode;

public class InstructionsTest {

	@Test
	public void testGetMethodParams() {
		List<String> paramsWithPrimitivesAtEnd = Instructions.getMethodParams("(Ljava/lang/String;IZC)V");
		Assert.assertEquals(Arrays.asList("Ljava/lang/String;", "I", "Z", "C"), paramsWithPrimitivesAtEnd);

		List<String> paramsWithPrimitivesAtStart = Instructions.getMethodParams("(IZCLjava/lang/String;)V");
		Assert.assertEquals(Arrays.asList("I", "Z", "C", "Ljava/lang/String;"), paramsWithPrimitivesAtStart);

		List<String> paramsWithoutPrimitives = Instructions.getMethodParams("(Ljava/lang/String;Ljava/util/List;)V");
		Assert.assertEquals(Arrays.asList("Ljava/lang/String;", "Ljava/util/List;"), paramsWithoutPrimitives);

		List<String> paramsWithOnlyPrimitives = Instructions.getMethodParams("(IIDJ)V");
		Assert.assertEquals(Arrays.asList("I", "I", "D", "J"), paramsWithOnlyPrimitives);
	}

	@Test
	public void testIsPrimitiveCast() {
		Assert.assertTrue(Instructions.isPrimitiveCast(new Instruction.Builder().withOpcode(Opcode.I2B).build()));
	}

}

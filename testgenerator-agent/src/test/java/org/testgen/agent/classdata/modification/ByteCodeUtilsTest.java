package org.testgen.agent.classdata.modification;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ByteCodeUtilsTest {

	@Test
	public void testCnvDescriptorToJvmName() {
		assertEquals("java/util/List", BytecodeUtils.cnvDescriptorToJvmName("Ljava/util/List;"));
		assertEquals("java/util/Integer", BytecodeUtils.cnvDescriptorToJvmName("Ljava/util/Integer;"));
	}

}
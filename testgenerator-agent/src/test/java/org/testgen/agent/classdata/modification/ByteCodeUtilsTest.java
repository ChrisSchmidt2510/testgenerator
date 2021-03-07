package org.testgen.agent.classdata.modification;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ByteCodeUtilsTest {

	@Test
	public void testCnvDescriptorToJvmName() {
		Assertions.assertEquals("java/util/List", BytecodeUtils.cnvDescriptorToJvmName("Ljava/util/List;"));
		Assertions.assertEquals("java/util/Integer", BytecodeUtils.cnvDescriptorToJvmName("Ljava/util/Integer;"));
	}

}

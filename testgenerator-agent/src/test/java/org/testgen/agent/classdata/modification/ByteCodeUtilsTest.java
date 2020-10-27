package org.testgen.agent.classdata.modification;

import org.junit.Assert;
import org.junit.Test;

public class ByteCodeUtilsTest {

	@Test
	public void testCnvDescriptorToJvmName() {
		Assert.assertEquals("java/util/List", BytecodeUtils.cnvDescriptorToJvmName("Ljava/util/List;"));
		Assert.assertEquals("java/util/Integer", BytecodeUtils.cnvDescriptorToJvmName("Ljava/util/Integer;"));
	}

}

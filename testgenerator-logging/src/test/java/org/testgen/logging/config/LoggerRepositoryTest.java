package org.testgen.logging.config;

import org.junit.Assert;
import org.junit.Test;

public class LoggerRepositoryTest {

	@Test
	public void testCountEqualTokens() {
		Assert.assertEquals((Integer) 3,
				LoggerRepository.countEqualTokens("org.testgen.agent", "org.testgen.agent.classdata.model.ClassData"));
	
		Assert.assertEquals((Integer) 0, LoggerRepository.countEqualTokens("org.testgen.agent.classdata.modification", "org.testgen.agent.ClassTransformerAgent"));
		
		Assert.assertEquals((Integer) 0, LoggerRepository.countEqualTokens("ort.testgen.agent.transformer", "org.testgen.agent.classData.model.ClassData"));
	}

}

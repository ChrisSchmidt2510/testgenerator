package org.testgen.logging.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class LoggerRepositoryTest {

	@Test
	public void testCountEqualTokens() {
		assertEquals((Integer) 3,
				LoggerRepository.countEqualTokens("org.testgen.agent", "org.testgen.agent.classdata.model.ClassData"));
	
		assertEquals((Integer) 0, LoggerRepository.countEqualTokens("org.testgen.agent.classdata.modification", "org.testgen.agent.ClassTransformerAgent"));
		
		assertEquals((Integer) 0, LoggerRepository.countEqualTokens("ort.testgen.agent.transformer", "org.testgen.agent.classData.model.ClassData"));
	}

}

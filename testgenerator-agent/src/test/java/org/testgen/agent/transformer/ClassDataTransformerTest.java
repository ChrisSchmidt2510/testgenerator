package org.testgen.agent.transformer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Month;
import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testgen.agent.classdata.model.ClassDataStorage;
import org.testgen.agent.classdata.testclasses.Adresse;
import org.testgen.agent.classdata.testclasses.BlObject;
import org.testgen.config.TestgeneratorConfig;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

public class ClassDataTransformerTest {

	private static Stream<Arguments> testModifyClassFile() throws NotFoundException {
		ClassPool classPool = ClassPool.getDefault();

		CtClass classValueTransformer = classPool.get(ValueTrackerTransformer.class.getName());
		CtClass classMonth = classPool.get(Month.class.getName());
		CtClass classIllegalArgumentException = classPool.get(IllegalArgumentException.class.getName());
		CtClass classClassTransformer = classPool.get(ClassTransformer.class.getName());
		CtClass classAdresse = classPool.get(Adresse.class.getName());
		CtClass classBlObject = classPool.get(BlObject.class.getName());

		return Stream.of(Arguments.of(classValueTransformer.getName().replace(".", "/"), classValueTransformer, true),
				Arguments.of(classAdresse.getName().replace(".", "/"), classAdresse, true),
				Arguments.of(classBlObject.getName().replace(".", "/"), classBlObject, true),
				Arguments.of(classMonth.getName().replace(".", "/"), classMonth, false),
				Arguments.of(classIllegalArgumentException.getName().replace(".", "/"), classIllegalArgumentException,
						false),
				Arguments.of(classClassTransformer.getName().replace(".", "/"), classClassTransformer, false));
	}

	@ParameterizedTest
	@MethodSource
	public void testModifyClassFile(String className, CtClass ctClass, boolean result) {
		try (MockedStatic<TestgeneratorConfig> mock = Mockito.mockStatic(TestgeneratorConfig.class)) {
			mock.when(TestgeneratorConfig::getClassNames)
					.thenReturn(Arrays.asList(Adresse.class.getName().replace(".", "/")));
			mock.when(TestgeneratorConfig::getBlPackages)
					.thenReturn(Arrays.asList("org/testgen/agent/transformer", "java/time", "java/lang"));

			ClassDataStorage.getInstance().addSuperclassToLoad(BlObject.class.getName());

			ClassDataTransformer transformer = new ClassDataTransformer();
			assertEquals(result, transformer.modifyClassFile(className, ctClass));
		}
	}

}

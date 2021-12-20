package org.testgen.config.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.testgen.config.DefinedArguments;

public class ArgumentParserTest {

	@Test
	public void testParseAgentProperties() {
		String args = "-ClassName=de/nvg/app/BusinessLogik" //
				+ "-MethodName=changePassword"
				+ "-MethodDescriptor=(Lde/nvg/bl/Account;Ljava/lang/String;)Ljava/lang/String;" //
				+ "-BlPackage=de/nvg/bl,de/nvg/logic" //
				+ "-BlPackageJarDestination=D:\\git\\testgenerator\\javaagent-sample-app\\target" //
				+ "-TraceReadFieldAccess"//
				+ "-PrintClassFilesDir=D:\\";

		ArgumentParser argParser = new ArgumentParser(args, DefinedArguments.getArguments());
		assertEquals("de/nvg/app/BusinessLogik",
				argParser.getArgumentValue(DefinedArguments.ARG_CLASS_NAME));
		assertEquals("changePassword", argParser.getArgumentValue(DefinedArguments.ARG_METHOD_NAME));
		assertEquals("(Lde/nvg/bl/Account;Ljava/lang/String;)Ljava/lang/String;",
				argParser.getArgumentValue(DefinedArguments.ARG_METHOD_DESC));
		assertEquals(Arrays.asList("de/nvg/bl", "de/nvg/logic"),
				argParser.getArgumentValues(DefinedArguments.ARG_BL_PACKAGE));
		assertEquals(Arrays.asList("D:\\git\\testgenerator\\javaagent-sample-app\\target"),
				argParser.getArgumentValues(DefinedArguments.ARG_BL_PACKGE_JAR_DEST));
		assertTrue(argParser.hasArgument(DefinedArguments.ARG_TRACE_READ_FIELD_ACCESS));
		assertEquals("D:\\", argParser.getArgumentValue(DefinedArguments.ARG_PRINT_CLASSFILES_DIR));
	}

}

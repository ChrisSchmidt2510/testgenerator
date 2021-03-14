package org.testgen.config.parser;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
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
		Assertions.assertEquals("de/nvg/app/BusinessLogik",
				argParser.getArgumentValue(DefinedArguments.ARG_CLASS_NAME));
		Assertions.assertEquals("changePassword", argParser.getArgumentValue(DefinedArguments.ARG_METHOD_NAME));
		Assertions.assertEquals("(Lde/nvg/bl/Account;Ljava/lang/String;)Ljava/lang/String;",
				argParser.getArgumentValue(DefinedArguments.ARG_METHOD_DESC));
		Assertions.assertEquals(Arrays.asList("de/nvg/bl", "de/nvg/logic"),
				argParser.getArgumentValues(DefinedArguments.ARG_BL_PACKAGE));
		Assertions.assertEquals(Arrays.asList("D:\\git\\testgenerator\\javaagent-sample-app\\target"),
				argParser.getArgumentValues(DefinedArguments.ARG_BL_PACKGE_JAR_DEST));
		Assertions.assertTrue(argParser.hasArgument(DefinedArguments.ARG_TRACE_READ_FIELD_ACCESS));
		Assertions.assertEquals("D:\\", argParser.getArgumentValue(DefinedArguments.ARG_PRINT_CLASSFILES_DIR));
	}

}

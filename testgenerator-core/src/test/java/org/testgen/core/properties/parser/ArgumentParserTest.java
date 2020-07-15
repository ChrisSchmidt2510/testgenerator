package org.testgen.core.properties.parser;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.testgen.core.properties.DefinedArguments;

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
		Assert.assertEquals("de/nvg/app/BusinessLogik", argParser.getArgumentValue(DefinedArguments.ARG_CLASS_NAME));
		Assert.assertEquals("changePassword", argParser.getArgumentValue(DefinedArguments.ARG_METHOD_NAME));
		Assert.assertEquals("(Lde/nvg/bl/Account;Ljava/lang/String;)Ljava/lang/String;",
				argParser.getArgumentValue(DefinedArguments.ARG_METHOD_DESC));
		Assert.assertEquals(Arrays.asList("de/nvg/bl", "de/nvg/logic"),
				argParser.getArgumentValues(DefinedArguments.ARG_BL_PACKAGE));
		Assert.assertEquals(Arrays.asList("D:\\git\\testgenerator\\javaagent-sample-app\\target"),
				argParser.getArgumentValues(DefinedArguments.ARG_BL_PACKGE_JAR_DEST));
		Assert.assertTrue(argParser.hasArgument(DefinedArguments.ARG_TRACE_READ_FIELD_ACCESS));
		Assert.assertEquals("D:\\", argParser.getArgumentValue(DefinedArguments.ARG_PRINT_CLASSFILES_DIR));
	}

}

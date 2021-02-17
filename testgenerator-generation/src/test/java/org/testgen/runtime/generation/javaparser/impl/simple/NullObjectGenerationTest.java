package org.testgen.runtime.generation.javaparser.impl.simple;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.testgen.runtime.generation.api.naming.NamingServiceProvider;
import org.testgen.runtime.generation.api.simple.SimpleObjectGeneration;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.NullBluePrint.NullBluePrintFactory;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.printer.PrettyPrinterConfiguration;

public class NullObjectGenerationTest {

	private Set<Class<?>> imports = new HashSet<>();

	private SimpleObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> simpleObjectGeneration = new NullObjectGeneration();

	private NullBluePrintFactory factory = new NullBluePrintFactory();

	@Before
	public void init() {
		simpleObjectGeneration.setImportCallBackHandler(imports::add);
	}

	@After
	public void cleanUp() {
		imports.clear();

		NamingServiceProvider.getNamingService().clearFields();
	}

	@Test
	public void testCreateField() {
		PrettyPrinterConfiguration config = new PrettyPrinterConfiguration();
		config.setPrintJavadoc(true);

		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", null);

		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		String expectedValueWithInit = "/**\r\n" + //
				" * TODO set correct Type for value\r\n" + //
				" */\r\n" + //
				"private Object value = null;";

		simpleObjectGeneration.createField(cu, bluePrint, true);
		Assert.assertEquals(expectedValueWithInit, cu.getFields().get(0).toString(config));

		String expectedValueWithoutInit = "/**\r\n" + //
				" * TODO set correct Type for value\r\n" + //
				" */\r\n" + //
				"private Object value;";

		simpleObjectGeneration.createField(cu, bluePrint, false);
		Assert.assertEquals(expectedValueWithoutInit, cu.getFields().get(1).toString(config));
	}

	@Test
	public void testCreateObject() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", null);

		BlockStmt block = new BlockStmt();

		simpleObjectGeneration.createObject(block, bluePrint, true);

		String expectedField = "// TODO set correct Type for value\r\n" //
				+ "this.value = null;";

		Assert.assertEquals(expectedField, block.getStatement(0).toString());

		String expectedLocal = "// TODO set correct Type for value\r\n" //
				+ "Object value = null;";

		simpleObjectGeneration.createObject(block, bluePrint, false);
		Assert.assertEquals(expectedLocal, block.getStatement(2).toString());
	}

	@Test
	public void testCreateInlineObject() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", null);

		Assert.assertEquals("null", simpleObjectGeneration.createInlineExpression(bluePrint).toString());
	}

}

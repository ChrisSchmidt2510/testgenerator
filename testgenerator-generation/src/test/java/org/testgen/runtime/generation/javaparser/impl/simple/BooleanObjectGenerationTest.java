package org.testgen.runtime.generation.javaparser.impl.simple;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.testgen.runtime.generation.api.GenerationFactory;
import org.testgen.runtime.generation.api.naming.NamingServiceProvider;
import org.testgen.runtime.generation.api.simple.SimpleObjectGeneration;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.BooleanBluePrint.BooleanBluePrintFactory;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;

public class BooleanObjectGenerationTest {

	private Set<Class<?>> imports = new HashSet<>();

	private SimpleObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> simpleObjectGeneration;

	private BooleanBluePrintFactory factory = new BooleanBluePrintFactory();

	@Before
	public void init() {
		GenerationFactory.<ClassOrInterfaceDeclaration, BlockStmt, Expression>getInstance()
				.setImportCallBackHandler(imports::add);

		simpleObjectGeneration = new BooleanObjectGeneration();
	}

	@After
	public void cleanUp() {
		imports.clear();

		NamingServiceProvider.getNamingService().clearFields();
	}

	@Test
	public void testCreateField() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", false);

		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		simpleObjectGeneration.createField(cu, bluePrint, true);

		Assert.assertEquals("private boolean value = false;", cu.getFields().get(0).toString());

		simpleObjectGeneration.createField(cu, bluePrint, false);

		Assert.assertEquals("private boolean value;", cu.getFields().get(1).toString());
	}

	@Test
	public void testCreateObject() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", false);

		BlockStmt blockStmt = new BlockStmt();

		simpleObjectGeneration.createObject(blockStmt, bluePrint, false);
		Assert.assertEquals("boolean value = false;", blockStmt.getStatement(0).toString());

		simpleObjectGeneration.createObject(blockStmt, bluePrint, true);
		Assert.assertEquals("this.value = false;", blockStmt.getStatement(1).toString());

	}

	@Test
	public void testCreateInlineObject() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("boolean", true);

		Assert.assertEquals("true", simpleObjectGeneration.createInlineExpression(bluePrint).toString());
	}
}

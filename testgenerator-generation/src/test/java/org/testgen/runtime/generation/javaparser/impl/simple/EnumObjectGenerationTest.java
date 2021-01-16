package org.testgen.runtime.generation.javaparser.impl.simple;

import java.time.Month;
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
import org.testgen.runtime.valuetracker.blueprint.simpletypes.EnumBluePrint.EnumBluePrintFactory;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;

public class EnumObjectGenerationTest {
	private Set<Class<?>> imports = new HashSet<>();

	private SimpleObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> simpleObjectGeneration;

	private EnumBluePrintFactory factory = new EnumBluePrintFactory();

	@Before
	public void init() {
		GenerationFactory.<ClassOrInterfaceDeclaration, BlockStmt, Expression>getInstance()
				.setImportCallBackHandler(imports::add);

		simpleObjectGeneration = new EnumObjectGeneration();
	}

	@After
	public void cleanUp() {
		imports.clear();

		NamingServiceProvider.getNamingService().clearFields();
	}

	@Test
	public void testCreateField() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("month", Month.DECEMBER);

		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		simpleObjectGeneration.createField(cu, bluePrint, true);
		Assert.assertEquals("private Month month = Month.DECEMBER;", cu.getFields().get(0).toString());

		simpleObjectGeneration.createField(cu, bluePrint, false);
		Assert.assertEquals("private Month month;", cu.getFields().get(1).toString());
	}

	@Test
	public void testCreateObject() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("month", Month.JULY);

		BlockStmt block = new BlockStmt();

		simpleObjectGeneration.createObject(block, bluePrint, true);
		Assert.assertEquals("this.month = Month.JULY;", block.getStatement(0).toString());

		simpleObjectGeneration.createObject(block, bluePrint, false);
		Assert.assertEquals("Month month = Month.JULY;", block.getStatement(1).toString());
	}

	@Test
	public void testCreateInlineObject() {

		SimpleBluePrint<?> bluePrint = factory.createBluePrint("month", Month.JANUARY);

		Assert.assertEquals("Month.JANUARY", simpleObjectGeneration.createInlineExpression(bluePrint).toString());
		Assert.assertTrue(imports.contains(Month.class));
	}
}

package org.testgen.runtime.generation.javaparser.impl.simpleGeneration;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.testgen.runtime.generation.api.GenerationFactory;
import org.testgen.runtime.generation.api.naming.NamingServiceProvider;
import org.testgen.runtime.generation.api.simple.SimpleObjectGeneration;
import org.testgen.runtime.generation.javaparser.impl.simple.BooleanObjectGeneration;
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

	@Before
	public void init() {
		GenerationFactory.<ClassOrInterfaceDeclaration, BlockStmt, Expression>getInstance()
				.setImportCallBackHandler(imports::add);

		simpleObjectGeneration = new BooleanObjectGeneration();
	}

	@After
	public void cleanUp() {
		imports.clear();

		NamingServiceProvider.getNamingService().clearFieldNames();
	}

	@Test
	public void testCreateField() {
		BooleanBluePrintFactory factory = new BooleanBluePrintFactory();

		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", false, null).castToSimpleBluePrint();

		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		simpleObjectGeneration.createField(cu, bluePrint, true);

		Assert.assertEquals("private boolean value = false;", cu.getFields().get(0).toString());

		simpleObjectGeneration.createField(cu, bluePrint, false);

		Assert.assertEquals("private boolean value;", cu.getFields().get(1).toString());
	}

	@Test
	public void testCreateObject() {
		BooleanBluePrintFactory factory = new BooleanBluePrintFactory();

		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", false, null).castToSimpleBluePrint();

		BlockStmt blockStmt = new BlockStmt();

		simpleObjectGeneration.createObject(blockStmt, bluePrint, false);
		Assert.assertEquals("boolean value = false;", blockStmt.getStatement(0).toString());

		simpleObjectGeneration.createObject(blockStmt, bluePrint, true);
		Assert.assertEquals("this.value = false;", blockStmt.getStatement(1).toString());

	}

	@Test
	public void testCreateInlineObject() {
		BooleanBluePrintFactory factory = new BooleanBluePrintFactory();

		SimpleBluePrint<?> bluePrint = factory.createBluePrint("boolean", true, null).castToSimpleBluePrint();

		Assert.assertEquals("true", simpleObjectGeneration.createInlineObject(bluePrint).toString());
	}
}

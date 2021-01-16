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
import org.testgen.runtime.valuetracker.blueprint.simpletypes.CharacterBluePrint.CharacterBluePrintFactory;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;

public class CharacterObjectGenerationTest {

	private Set<Class<?>> imports = new HashSet<>();

	private SimpleObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> simpleObjectGeneration;

	private CharacterBluePrintFactory factory = new CharacterBluePrintFactory();

	@Before
	public void init() {
		GenerationFactory.<ClassOrInterfaceDeclaration, BlockStmt, Expression>getInstance()
				.setImportCallBackHandler(imports::add);

		simpleObjectGeneration = new CharacterObjectGeneration();
	}

	@After
	public void cleanUp() {
		imports.clear();

		NamingServiceProvider.getNamingService().clearFields();
	}

	@Test
	public void testCreateField() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", 'S');

		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		simpleObjectGeneration.createField(cu, bluePrint, true);
		Assert.assertEquals("private char value = 'S';", cu.getFields().get(0).toString());

		simpleObjectGeneration.createField(cu, bluePrint, false);
		Assert.assertEquals("private char value;", cu.getFields().get(1).toString());
	}

	@Test
	public void testCreateObject() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", 'm');

		BlockStmt block = new BlockStmt();

		simpleObjectGeneration.createObject(block, bluePrint, true);
		Assert.assertEquals("this.value = 'm';", block.getStatement(0).toString());

		simpleObjectGeneration.createObject(block, bluePrint, false);
		Assert.assertEquals("char value = 'm';", block.getStatement(1).toString());
	}

	@Test
	public void testCreateInlineObject() {

		SimpleBluePrint<?> bluePrint = factory.createBluePrint("char", 'c');

		Assert.assertEquals("'c'", simpleObjectGeneration.createInlineExpression(bluePrint).toString());
	}

}

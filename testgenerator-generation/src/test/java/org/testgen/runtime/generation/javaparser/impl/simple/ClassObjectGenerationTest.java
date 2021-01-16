package org.testgen.runtime.generation.javaparser.impl.simple;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.testgen.runtime.generation.api.GenerationFactory;
import org.testgen.runtime.generation.api.naming.NamingServiceProvider;
import org.testgen.runtime.generation.api.simple.SimpleObjectGeneration;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.ClassBluePrint.ClassBluePrintFactory;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;

public class ClassObjectGenerationTest {

	private Set<Class<?>> imports = new HashSet<>();

	private SimpleObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> simpleObjectGeneration;

	private ClassBluePrintFactory factory = new ClassBluePrintFactory();

	@Before
	public void init() {
		GenerationFactory.<ClassOrInterfaceDeclaration, BlockStmt, Expression>getInstance()
				.setImportCallBackHandler(imports::add);

		simpleObjectGeneration = new ClassObjectGeneration();
	}

	@After
	public void cleanUp() {
		imports.clear();

		NamingServiceProvider.getNamingService().clearFields();
	}

	@Test
	public void testCreateField() {

		SimpleBluePrint<?> bluePrint = factory.createBluePrint("clazz", String.class);

		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		simpleObjectGeneration.createField(cu, bluePrint, true);

		Assert.assertEquals("private Class<?> clazz = String.class;", cu.getFields().get(0).toString());

		simpleObjectGeneration.createField(cu, bluePrint, false);

		Assert.assertEquals("private Class<?> clazz;", cu.getFields().get(1).toString());
	}

	@Test
	public void testCreateObject() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("clazz", String.class);

		BlockStmt block = new BlockStmt();

		simpleObjectGeneration.createObject(block, bluePrint, true);

		Assert.assertEquals("this.clazz = String.class;", block.getStatement(0).toString());

		simpleObjectGeneration.createObject(block, bluePrint, false);

		Assert.assertEquals("Class<?> clazz = String.class;", block.getStatement(1).toString());

	}

	@Test
	public void testCreateInlineObject() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("Class", List.class);

		Assert.assertEquals("List.class", simpleObjectGeneration.createInlineExpression(bluePrint).toString());
		Assert.assertTrue(imports.contains(List.class));
	}
}

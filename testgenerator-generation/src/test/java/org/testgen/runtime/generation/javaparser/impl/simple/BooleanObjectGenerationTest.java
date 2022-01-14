package org.testgen.runtime.generation.javaparser.impl.simple;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

	private SimpleObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> simpleObjectGeneration = new BooleanObjectGeneration();

	private BooleanBluePrintFactory factory = new BooleanBluePrintFactory();

	@BeforeEach
	public void init() {
		simpleObjectGeneration.setImportCallBackHandler(imports::add);

	}

	@AfterEach
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

		assertEquals("private boolean value = false;", cu.getFields().get(0).toString());

		simpleObjectGeneration.createField(cu, bluePrint, false);

		assertEquals("private boolean value;", cu.getFields().get(1).toString());
	}

	@Test
	public void testCreateObject() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", false);

		BlockStmt blockStmt = new BlockStmt();

		simpleObjectGeneration.createObject(blockStmt, bluePrint, false);
		assertEquals("boolean value = false;", blockStmt.getStatement(0).toString());

		simpleObjectGeneration.createObject(blockStmt, bluePrint, true);
		assertEquals("this.value = false;", blockStmt.getStatement(2).toString());

	}

	@Test
	public void testCreateInlineObject() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("boolean", true);

		assertEquals("true", simpleObjectGeneration.createInlineExpression(bluePrint).toString());
	}
}

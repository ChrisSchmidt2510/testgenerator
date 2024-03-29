package org.testgen.runtime.generation.javaparser.impl.simple;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testgen.runtime.generation.api.naming.NamingServiceProvider;
import org.testgen.runtime.generation.api.simple.SimpleObjectGeneration;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.JavaDateBluePrint.JavaDateBluePrintFactory;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;

public class DateObjectGenerationTest {

	private Set<Class<?>> imports = new HashSet<>();

	private SimpleObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> simpleObjectGeneration = new DateObjectGeneration();

	private JavaDateBluePrintFactory factory = new JavaDateBluePrintFactory();

	@BeforeEach
	public void init() {
		simpleObjectGeneration.setImportCallBackHandler(imports::add);
	}

	@AfterEach
	public void cleanUp() {
		imports.clear();

		NamingServiceProvider.getNamingService().clearFields();
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testCreateFieldSqlDate() {

		SimpleBluePrint<?> bluePrint = factory.createBluePrint("date", new java.sql.Date(2020 - 1900, 10 - 1, 25));

		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		simpleObjectGeneration.createField(cu, bluePrint, true);

		assertEquals("private Date date = new Date(2020 - 1900, 10 - 1, 25);", cu.getFields().get(0).toString());

		simpleObjectGeneration.createField(cu, bluePrint, false);

		assertEquals("private Date date;", cu.getFields().get(1).toString());

		assertTrue(imports.contains(java.sql.Date.class));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testCreateFieldDate() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("date", new Date(2020 - 1900, 12 - 1, 24));

		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		simpleObjectGeneration.createField(cu, bluePrint, true);

		assertEquals("private Date date = new Date(2020 - 1900, 12 - 1, 24);", cu.getFields().get(0).toString());

		simpleObjectGeneration.createField(cu, bluePrint, false);

		assertEquals("private Date date;", cu.getFields().get(1).toString());

		assertTrue(imports.contains(Date.class));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testCreateObjectSqlDate() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("date", new java.sql.Date(2020 - 1900, 10 - 1, 25));

		BlockStmt block = new BlockStmt();

		simpleObjectGeneration.createObject(block, bluePrint, true);

		assertEquals("this.date = new Date(2020 - 1900, 10 - 1, 25);", block.getStatement(0).toString());

		simpleObjectGeneration.createObject(block, bluePrint, false);

		assertEquals("Date date = new Date(2020 - 1900, 10 - 1, 25);", block.getStatement(2).toString());

		assertTrue(imports.contains(java.sql.Date.class));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testCreateObjectDate() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("date", new Date(2020 - 1900, 12 - 1, 24));

		BlockStmt block = new BlockStmt();

		simpleObjectGeneration.createObject(block, bluePrint, true);
		assertEquals("this.date = new Date(2020 - 1900, 12 - 1, 24);", block.getStatement(0).toString());

		simpleObjectGeneration.createObject(block, bluePrint, false);
		assertEquals("Date date = new Date(2020 - 1900, 12 - 1, 24);", block.getStatement(2).toString());

		assertTrue(imports.contains(Date.class));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testCreateInlineObjectSqlDate() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("date", new java.sql.Date(2020 - 1900, 10 - 1, 25));

		assertEquals("new Date(2020 - 1900, 10 - 1, 25)",
				simpleObjectGeneration.createInlineExpression(bluePrint).toString());
		assertTrue(imports.contains(java.sql.Date.class));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testCreateInlineObjectDate() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("date", new Date(2020 - 1900, 12 - 1, 24));

		assertEquals("new Date(2020 - 1900, 12 - 1, 24)",
				simpleObjectGeneration.createInlineExpression(bluePrint).toString());

		SimpleBluePrint<?> dateTime = factory.createBluePrint("date", new Date(2020 - 1900, 10 - 1, 31, 7, 8));
		assertEquals("new Date(2020 - 1900, 10 - 1, 31, 7, 8)",
				simpleObjectGeneration.createInlineExpression(dateTime).toString());

		SimpleBluePrint<?> dateTimeWithSeconds = factory.createBluePrint("date",
				new Date(2020 - 1900, 10 - 1, 31, 7, 8, 55));
		assertEquals("new Date(2020 - 1900, 10 - 1, 31, 7, 8, 55)",
				simpleObjectGeneration.createInlineExpression(dateTimeWithSeconds).toString());

		assertTrue(imports.contains(Date.class));
	}
}

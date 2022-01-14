package org.testgen.runtime.generation.javaparser.impl.simple;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testgen.runtime.generation.api.naming.NamingServiceProvider;
import org.testgen.runtime.generation.api.simple.SimpleObjectGeneration;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.NumberBluePrint.NumberBluePrintFactory;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;

public class NumberObjectGenerationTest {

	private Set<Class<?>> imports = new HashSet<>();

	private SimpleObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> simpleObjectGeneration = new NumberObjectGeneration();

	private NumberBluePrintFactory factory = new NumberBluePrintFactory();

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
	public void testCreateFieldInteger() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", 7);

		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		simpleObjectGeneration.createField(cu, bluePrint, true);
		assertEquals("private int value = 7;", cu.getFields().get(0).toString());

		simpleObjectGeneration.createField(cu, bluePrint, false);
		assertEquals("private int value;", cu.getFields().get(1).toString());
	}

	@Test
	public void testCreateFieldByte() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", (byte) 127);

		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		simpleObjectGeneration.createField(cu, bluePrint, true);
		assertEquals("private byte value = (byte) 127;", cu.getFields().get(0).toString());

		simpleObjectGeneration.createField(cu, bluePrint, false);
		assertEquals("private byte value;", cu.getFields().get(1).toString());
	}

	@Test
	public void testCreateFieldShort() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", (short) 255);

		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		simpleObjectGeneration.createField(cu, bluePrint, true);
		assertEquals("private short value = (short) 255;", cu.getFields().get(0).toString());

		simpleObjectGeneration.createField(cu, bluePrint, false);
		assertEquals("private short value;", cu.getFields().get(1).toString());
	}

	@Test
	public void testCreateFieldFloat() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", 10.15f);

		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		simpleObjectGeneration.createField(cu, bluePrint, true);
		assertEquals("private float value = 10.15f;", cu.getFields().get(0).toString());

		simpleObjectGeneration.createField(cu, bluePrint, false);
		assertEquals("private float value;", cu.getFields().get(1).toString());
	}

	@Test
	public void testCreateFieldDouble() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", 35.49872);

		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		simpleObjectGeneration.createField(cu, bluePrint, true);
		assertEquals("private double value = 35.49872;", cu.getFields().get(0).toString());

		simpleObjectGeneration.createField(cu, bluePrint, false);
		assertEquals("private double value;", cu.getFields().get(1).toString());
	}

	@Test
	public void testCreateFieldLong() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", 5_000_000L);

		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		simpleObjectGeneration.createField(cu, bluePrint, true);
		assertEquals("private long value = 5000000;", cu.getFields().get(0).toString());

		simpleObjectGeneration.createField(cu, bluePrint, false);
		assertEquals("private long value;", cu.getFields().get(1).toString());
	}

	@Test
	public void testCreateFieldBigDecimal() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", BigDecimal.ONE.setScale(3));

		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		simpleObjectGeneration.createField(cu, bluePrint, true);
		assertEquals("private BigDecimal value = BigDecimal.valueOf(1.0).setScale(3);",
				cu.getFields().get(0).toString());

		simpleObjectGeneration.createField(cu, bluePrint, false);
		assertEquals("private BigDecimal value;", cu.getFields().get(1).toString());
	}

	@Test
	public void testCreateObjectInteger() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", 312);

		BlockStmt block = new BlockStmt();

		simpleObjectGeneration.createObject(block, bluePrint, true);
		assertEquals("this.value = 312;", block.getStatement(0).toString());

		simpleObjectGeneration.createObject(block, bluePrint, false);
		assertEquals("int value = 312;", block.getStatement(2).toString());
	}

	@Test
	public void testCreateObjectByte() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", (byte) 15);

		BlockStmt block = new BlockStmt();

		simpleObjectGeneration.createObject(block, bluePrint, true);
		assertEquals("this.value = (byte) 15;", block.getStatement(0).toString());

		simpleObjectGeneration.createObject(block, bluePrint, false);
		assertEquals("byte value = (byte) 15;", block.getStatement(2).toString());
	}

	@Test
	public void testCreateObjectShort() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", (short) 113);

		BlockStmt block = new BlockStmt();

		simpleObjectGeneration.createObject(block, bluePrint, true);
		assertEquals("this.value = (short) 113;", block.getStatement(0).toString());

		simpleObjectGeneration.createObject(block, bluePrint, false);
		assertEquals("short value = (short) 113;", block.getStatement(2).toString());
	}

	@Test
	public void testCreateObjectFloat() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", 170.837f);

		BlockStmt block = new BlockStmt();

		simpleObjectGeneration.createObject(block, bluePrint, true);
		assertEquals("this.value = 170.837f;", block.getStatement(0).toString());

		simpleObjectGeneration.createObject(block, bluePrint, false);
		assertEquals("float value = 170.837f;", block.getStatement(2).toString());
	}

	@Test
	public void testCreateObjectDouble() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", 35.49872);

		BlockStmt block = new BlockStmt();

		simpleObjectGeneration.createObject(block, bluePrint, true);
		assertEquals("this.value = 35.49872;", block.getStatement(0).toString());

		simpleObjectGeneration.createObject(block, bluePrint, false);
		assertEquals("double value = 35.49872;", block.getStatement(2).toString());
	}

	@Test
	public void testCreateObjectLong() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", 987654321L);

		BlockStmt block = new BlockStmt();

		simpleObjectGeneration.createObject(block, bluePrint, true);
		assertEquals("this.value = 987654321;", block.getStatement(0).toString());

		simpleObjectGeneration.createObject(block, bluePrint, false);
		assertEquals("long value = 987654321;", block.getStatement(2).toString());
	}

	@Test
	public void testCreateObjectBigDecimal() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value",
				BigDecimal.valueOf(799663.33333333).setScale(8));

		BlockStmt block = new BlockStmt();

		simpleObjectGeneration.createObject(block, bluePrint, true);
		assertEquals("this.value = BigDecimal.valueOf(799663.33333333).setScale(8);",
				block.getStatement(0).toString());

		simpleObjectGeneration.createObject(block, bluePrint, false);
		assertEquals("BigDecimal value = BigDecimal.valueOf(799663.33333333).setScale(8);",
				block.getStatement(2).toString());
	}

	@Test
	public void testCreateInlineObjectInteger() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("Integer", 25);

		assertEquals("25", simpleObjectGeneration.createInlineExpression(bluePrint).toString());
	}

	@Test
	public void testCreateInlineObjectByte() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("Byte", (byte) 127);

		assertEquals("(byte) 127", simpleObjectGeneration.createInlineExpression(bluePrint).toString());
	}

	@Test
	public void testCreateInlineObjectShort() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("short", (short) 255);

		assertEquals("(short) 255", simpleObjectGeneration.createInlineExpression(bluePrint).toString());
	}

	@Test
	public void testCreateInlineObjectFloat() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("float", 5.12f);

		assertEquals("5.12f", simpleObjectGeneration.createInlineExpression(bluePrint).toString());
	}

	@Test
	public void testCreateInlineObjectDouble() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("double", 5.1872);

		assertEquals("5.1872", simpleObjectGeneration.createInlineExpression(bluePrint).toString());
	}

	@Test
	public void testCreateInlineObjectLong() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("long", 1_000_000L);

		assertEquals("1000000", simpleObjectGeneration.createInlineExpression(bluePrint).toString());
	}

	@Test
	public void testCreateInlineObjectBigDecimal() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("BigDecimal", BigDecimal.TEN.setScale(3));

		assertEquals("BigDecimal.valueOf(10.0).setScale(3)",
				simpleObjectGeneration.createInlineExpression(bluePrint).toString());
		assertTrue(imports.contains(BigDecimal.class));
	}
}

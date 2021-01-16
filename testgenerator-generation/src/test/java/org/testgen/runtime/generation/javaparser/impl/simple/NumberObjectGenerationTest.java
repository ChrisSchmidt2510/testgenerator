package org.testgen.runtime.generation.javaparser.impl.simple;

import java.math.BigDecimal;
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
import org.testgen.runtime.valuetracker.blueprint.simpletypes.NumberBluePrint.NumberBluePrintFactory;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;

public class NumberObjectGenerationTest {

	private Set<Class<?>> imports = new HashSet<>();

	private SimpleObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> simpleObjectGeneration;

	private NumberBluePrintFactory factory = new NumberBluePrintFactory();

	@Before
	public void init() {
		GenerationFactory.<ClassOrInterfaceDeclaration, BlockStmt, Expression>getInstance()
				.setImportCallBackHandler(imports::add);

		simpleObjectGeneration = new NumberObjectGeneration();
	}

	@After
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
		Assert.assertEquals("private int value = 7;", cu.getFields().get(0).toString());

		simpleObjectGeneration.createField(cu, bluePrint, false);
		Assert.assertEquals("private int value;", cu.getFields().get(1).toString());
	}

	@Test
	public void testCreateFieldByte() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", (byte) 127);

		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		simpleObjectGeneration.createField(cu, bluePrint, true);
		Assert.assertEquals("private byte value = (byte) 127;", cu.getFields().get(0).toString());

		simpleObjectGeneration.createField(cu, bluePrint, false);
		Assert.assertEquals("private byte value;", cu.getFields().get(1).toString());
	}

	@Test
	public void testCreateFieldShort() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", (short) 255);

		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		simpleObjectGeneration.createField(cu, bluePrint, true);
		Assert.assertEquals("private short value = (short) 255;", cu.getFields().get(0).toString());

		simpleObjectGeneration.createField(cu, bluePrint, false);
		Assert.assertEquals("private short value;", cu.getFields().get(1).toString());
	}

	@Test
	public void testCreateFieldFloat() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", 10.15f);

		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		simpleObjectGeneration.createField(cu, bluePrint, true);
		Assert.assertEquals("private float value = 10.15f;", cu.getFields().get(0).toString());

		simpleObjectGeneration.createField(cu, bluePrint, false);
		Assert.assertEquals("private float value;", cu.getFields().get(1).toString());
	}

	@Test
	public void testCreateFieldDouble() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", 35.49872);

		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		simpleObjectGeneration.createField(cu, bluePrint, true);
		Assert.assertEquals("private double value = 35.49872;", cu.getFields().get(0).toString());

		simpleObjectGeneration.createField(cu, bluePrint, false);
		Assert.assertEquals("private double value;", cu.getFields().get(1).toString());
	}

	@Test
	public void testCreateFieldLong() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", 5_000_000L);

		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		simpleObjectGeneration.createField(cu, bluePrint, true);
		Assert.assertEquals("private long value = 5000000L;", cu.getFields().get(0).toString());

		simpleObjectGeneration.createField(cu, bluePrint, false);
		Assert.assertEquals("private long value;", cu.getFields().get(1).toString());
	}

	@Test
	public void testCreateFieldBigDecimal() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", BigDecimal.ONE.setScale(3));

		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		simpleObjectGeneration.createField(cu, bluePrint, true);
		Assert.assertEquals("private BigDecimal value = BigDecimal.valueOf(1.0).setScale(3);",
				cu.getFields().get(0).toString());

		simpleObjectGeneration.createField(cu, bluePrint, false);
		Assert.assertEquals("private BigDecimal value;", cu.getFields().get(1).toString());
	}

	@Test
	public void testCreateObjectInteger() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", 312);

		BlockStmt block = new BlockStmt();

		simpleObjectGeneration.createObject(block, bluePrint, true);
		Assert.assertEquals("this.value = 312;", block.getStatement(0).toString());

		simpleObjectGeneration.createObject(block, bluePrint, false);
		Assert.assertEquals("int value = 312;", block.getStatement(1).toString());
	}

	@Test
	public void testCreateObjectByte() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", (byte) 15);

		BlockStmt block = new BlockStmt();

		simpleObjectGeneration.createObject(block, bluePrint, true);
		Assert.assertEquals("this.value = (byte) 15;", block.getStatement(0).toString());

		simpleObjectGeneration.createObject(block, bluePrint, false);
		Assert.assertEquals("byte value = (byte) 15;", block.getStatement(1).toString());
	}

	@Test
	public void testCreateObjectShort() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", (short) 113);

		BlockStmt block = new BlockStmt();

		simpleObjectGeneration.createObject(block, bluePrint, true);
		Assert.assertEquals("this.value = (short) 113;", block.getStatement(0).toString());

		simpleObjectGeneration.createObject(block, bluePrint, false);
		Assert.assertEquals("short value = (short) 113;", block.getStatement(1).toString());
	}

	@Test
	public void testCreateObjectFloat() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", 170.837f);

		BlockStmt block = new BlockStmt();

		simpleObjectGeneration.createObject(block, bluePrint, true);
		Assert.assertEquals("this.value = 170.837f;", block.getStatement(0).toString());

		simpleObjectGeneration.createObject(block, bluePrint, false);
		Assert.assertEquals("float value = 170.837f;", block.getStatement(1).toString());
	}

	@Test
	public void testCreateObjectDouble() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", 35.49872);

		BlockStmt block = new BlockStmt();

		simpleObjectGeneration.createObject(block, bluePrint, true);
		Assert.assertEquals("this.value = 35.49872;", block.getStatement(0).toString());

		simpleObjectGeneration.createObject(block, bluePrint, false);
		Assert.assertEquals("double value = 35.49872;", block.getStatement(1).toString());
	}

	@Test
	public void testCreateObjectLong() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", 987654321L);

		BlockStmt block = new BlockStmt();

		simpleObjectGeneration.createObject(block, bluePrint, true);
		Assert.assertEquals("this.value = 987654321;", block.getStatement(0).toString());

		simpleObjectGeneration.createObject(block, bluePrint, false);
		Assert.assertEquals("long value = 987654321;", block.getStatement(1).toString());
	}

	@Test
	public void testCreateObjectBigDecimal() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value",
				BigDecimal.valueOf(799663.33333333).setScale(8));

		BlockStmt block = new BlockStmt();

		simpleObjectGeneration.createObject(block, bluePrint, true);
		Assert.assertEquals("this.value = BigDecimal.valueOf(799663.33333333).setScale(8);",
				block.getStatement(0).toString());

		simpleObjectGeneration.createObject(block, bluePrint, false);
		Assert.assertEquals("BigDecimal value = BigDecimal.valueOf(799663.33333333).setScale(8);",
				block.getStatement(1).toString());
	}

	@Test
	public void testCreateInlineObjectInteger() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("Integer", 25);

		Assert.assertEquals("25", simpleObjectGeneration.createInlineExpression(bluePrint).toString());
	}

	@Test
	public void testCreateInlineObjectByte() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("Byte", (byte) 127);

		Assert.assertEquals("(byte) 127", simpleObjectGeneration.createInlineExpression(bluePrint).toString());
	}

	@Test
	public void testCreateInlineObjectShort() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("short", (short) 255);

		Assert.assertEquals("(short) 255", simpleObjectGeneration.createInlineExpression(bluePrint).toString());
	}

	@Test
	public void testCreateInlineObjectFloat() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("float", 5.12f);

		Assert.assertEquals("5.12f", simpleObjectGeneration.createInlineExpression(bluePrint).toString());
	}

	@Test
	public void testCreateInlineObjectDouble() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("double", 5.1872);

		Assert.assertEquals("5.1872", simpleObjectGeneration.createInlineExpression(bluePrint).toString());
	}

	@Test
	public void testCreateInlineObjectLong() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("long", 1_000_000L);

		Assert.assertEquals("1000000L", simpleObjectGeneration.createInlineExpression(bluePrint).toString());
	}

	@Test
	public void testCreateInlineObjectBigDecimal() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("BigDecimal", BigDecimal.TEN.setScale(3));

		Assert.assertEquals("BigDecimal.valueOf(10.0).setScale(3)",
				simpleObjectGeneration.createInlineExpression(bluePrint).toString());
		Assert.assertTrue(imports.contains(BigDecimal.class));
	}
}

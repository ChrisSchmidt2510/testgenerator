package org.testgen.runtime.generation.javaparser.impl.simple;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
import org.testgen.runtime.valuetracker.blueprint.simpletypes.LocalDateBluePrint.LocalDateBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.LocalDateTimeBluePrint.LocalDateTimeBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.LocalTimeBluePrint.LocalTimeBluePrintFactory;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;

public class LocalDateTimeObjectGenerationTest {
	private Set<Class<?>> imports = new HashSet<>();

	private SimpleObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> simpleObjectGeneration;

	private LocalDateBluePrintFactory dateFactory = new LocalDateBluePrintFactory();
	private LocalTimeBluePrintFactory timeFactory = new LocalTimeBluePrintFactory();
	private LocalDateTimeBluePrintFactory dateTimeFactory = new LocalDateTimeBluePrintFactory();

	@Before
	public void init() {
		GenerationFactory.<ClassOrInterfaceDeclaration, BlockStmt, Expression>getInstance()
				.setImportCallBackHandler(imports::add);

		simpleObjectGeneration = new LocalDateTimeObjectGeneration();
	}

	@After
	public void cleanUp() {
		imports.clear();

		NamingServiceProvider.getNamingService().clearFields();
	}

	@Test
	public void testCreateFieldLocalDate() {
		SimpleBluePrint<?> bluePrint = dateFactory.createBluePrint("localDate", //
				LocalDate.of(2020, Month.DECEMBER, 31));

		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		simpleObjectGeneration.createField(cu, bluePrint, true);
		Assert.assertEquals("private LocalDate localDate = LocalDate.of(2020, Month.DECEMBER, 31);",
				cu.getFields().get(0).toString());

		simpleObjectGeneration.createField(cu, bluePrint, false);
		Assert.assertEquals("private LocalDate localDate;", cu.getFields().get(1).toString());
	}

	@Test
	public void testCreateFieldLocalTime() {
		SimpleBluePrint<?> bluePrint = timeFactory.createBluePrint("localTime", //
				LocalTime.of(17, 15));

		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		simpleObjectGeneration.createField(cu, bluePrint, true);
		Assert.assertEquals("private LocalTime localTime = LocalTime.of(17, 15);", cu.getFields().get(0).toString());

		simpleObjectGeneration.createField(cu, bluePrint, false);
		Assert.assertEquals("private LocalTime localTime;", cu.getFields().get(1).toString());
	}

	@Test
	public void testCreateFieldLocalDateTime() {
		SimpleBluePrint<?> bluePrint = dateTimeFactory.createBluePrint("localDateTime",
				LocalDateTime.of(LocalDate.of(1998, Month.OCTOBER, 25), LocalTime.of(2, 18)));

		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		simpleObjectGeneration.createField(cu, bluePrint, true);
		Assert.assertEquals(
				"private LocalDateTime localDateTime = LocalDateTime.of(LocalDate.of(1998, Month.OCTOBER, 25), LocalTime.of(2, 18));",
				cu.getFields().get(0).toString());

		simpleObjectGeneration.createField(cu, bluePrint, false);
		Assert.assertEquals("private LocalDateTime localDateTime;", cu.getFields().get(1).toString());
	}

	@Test
	public void testCreateObjectLocalDate() {
		SimpleBluePrint<?> bluePrint = dateFactory.createBluePrint("localDate", //
				LocalDate.of(2020, Month.OCTOBER, 31));

		BlockStmt block = new BlockStmt();

		simpleObjectGeneration.createObject(block, bluePrint, true);
		Assert.assertEquals("this.localDate = LocalDate.of(2020, Month.OCTOBER, 31);",
				block.getStatement(0).toString());

		simpleObjectGeneration.createObject(block, bluePrint, false);
		Assert.assertEquals("LocalDate localDate = LocalDate.of(2020, Month.OCTOBER, 31);",
				block.getStatement(1).toString());
	}

	@Test
	public void testCreateObjectLocalTime() {
		SimpleBluePrint<?> bluePrint = timeFactory.createBluePrint("localTime", //
				LocalTime.of(17, 15));

		BlockStmt block = new BlockStmt();

		simpleObjectGeneration.createObject(block, bluePrint, true);
		Assert.assertEquals("this.localTime = LocalTime.of(17, 15);", block.getStatement(0).toString());

		simpleObjectGeneration.createObject(block, bluePrint, false);
		Assert.assertEquals("LocalTime localTime = LocalTime.of(17, 15);", block.getStatement(1).toString());
	}

	@Test
	public void testCreateObjectLocalDateTime() {
		SimpleBluePrint<?> bluePrint = dateTimeFactory.createBluePrint("localDateTime",
				LocalDateTime.of(LocalDate.of(1998, Month.OCTOBER, 25), LocalTime.of(2, 18)));

		BlockStmt block = new BlockStmt();

		simpleObjectGeneration.createObject(block, bluePrint, true);
		Assert.assertEquals(
				"this.localDateTime = LocalDateTime.of(LocalDate.of(1998, Month.OCTOBER, 25), LocalTime.of(2, 18));",
				block.getStatement(0).toString());

		simpleObjectGeneration.createObject(block, bluePrint, false);
		Assert.assertEquals(
				"LocalDateTime localDateTime = LocalDateTime.of(LocalDate.of(1998, Month.OCTOBER, 25), LocalTime.of(2, 18));",
				block.getStatement(1).toString());
	}

	@Test
	public void testCreateInlineObjectLocalDate() {
		SimpleBluePrint<?> bluePrint = dateFactory.createBluePrint("localDate", //
				LocalDate.of(2020, Month.DECEMBER, 31));

		Assert.assertEquals("LocalDate.of(2020, Month.DECEMBER, 31)",
				simpleObjectGeneration.createInlineObject(bluePrint).toString());
		Assert.assertTrue(imports.contains(LocalDate.class));
		Assert.assertTrue(imports.contains(Month.class));
	}

	@Test
	public void testCreateInlineObjectLocalTime() {
		SimpleBluePrint<?> bluePrint = timeFactory.createBluePrint("localTime", //
				LocalTime.of(17, 15));

		Assert.assertEquals("LocalTime.of(17, 15)", //
				simpleObjectGeneration.createInlineObject(bluePrint).toString());

		SimpleBluePrint<?> time = timeFactory.createBluePrint("localTime", //
				LocalTime.of(23, 7, 28));

		Assert.assertEquals("LocalTime.of(23, 7, 28)", //
				simpleObjectGeneration.createInlineObject(time).toString());

		Assert.assertTrue(imports.contains(LocalTime.class));
	}

	@Test
	public void testCreateInlineObjectLocalDateTime() {
		SimpleBluePrint<?> bluePrint = dateTimeFactory.createBluePrint("localDateTime",
				LocalDateTime.of(LocalDate.of(1998, Month.OCTOBER, 25), LocalTime.of(2, 18)));

		Assert.assertEquals("LocalDateTime.of(LocalDate.of(1998, Month.OCTOBER, 25), LocalTime.of(2, 18))",
				simpleObjectGeneration.createInlineObject(bluePrint).toString());

		SimpleBluePrint<?> withSeconds = dateTimeFactory.createBluePrint("localDateTime",
				LocalDateTime.of(LocalDate.of(2001, Month.SEPTEMBER, 11), LocalTime.of(15, 7, 25)));

		Assert.assertEquals("LocalDateTime.of(LocalDate.of(2001, Month.SEPTEMBER, 11), LocalTime.of(15, 7, 25))",
				simpleObjectGeneration.createInlineObject(withSeconds).toString());

		Assert.assertTrue(imports.contains(LocalDateTime.class));
		Assert.assertTrue(imports.contains(LocalDate.class));
		Assert.assertTrue(imports.contains(LocalTime.class));
		Assert.assertTrue(imports.contains(Month.class));
	}
}

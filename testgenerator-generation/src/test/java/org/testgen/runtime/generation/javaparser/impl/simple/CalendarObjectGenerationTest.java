package org.testgen.runtime.generation.javaparser.impl.simple;

import java.util.GregorianCalendar;
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
import org.testgen.runtime.valuetracker.blueprint.simpletypes.CalendarBluePrint.CalendarBluePrintFactory;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;

public class CalendarObjectGenerationTest {

	private Set<Class<?>> imports = new HashSet<>();

	private SimpleObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> simpleObjectGeneration;

	private CalendarBluePrintFactory factory = new CalendarBluePrintFactory();

	@Before
	public void init() {
		GenerationFactory.<ClassOrInterfaceDeclaration, BlockStmt, Expression>getInstance()
				.setImportCallBackHandler(imports::add);

		simpleObjectGeneration = new CalendarObjectGeneration();
	}

	@After
	public void cleanUp() {
		imports.clear();

		NamingServiceProvider.getNamingService().clearFields();
	}

	@Test
	public void testCreateField() {
		SimpleBluePrint<?> dateBp = factory.createBluePrint("calendar", new GregorianCalendar(2020, 10 - 1, 25));

		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		simpleObjectGeneration.createField(cu, dateBp, true);
		Assert.assertEquals("private GregorianCalendar calendar = new GregorianCalendar(2020, 10 - 1, 25);",
				cu.getFields().get(0).toString());

		simpleObjectGeneration.createField(cu, dateBp, false);
		Assert.assertEquals("private GregorianCalendar calendar;", cu.getFields().get(1).toString());

		Assert.assertTrue(imports.contains(GregorianCalendar.class));
	}

	@Test
	public void testCreateObject() {
		SimpleBluePrint<?> dateBp = factory.createBluePrint("calendar", new GregorianCalendar(2020, 10 - 1, 25));

		BlockStmt block = new BlockStmt();

		simpleObjectGeneration.createObject(block, dateBp, true);
		Assert.assertEquals("this.calendar = new GregorianCalendar(2020, 10 - 1, 25);",
				block.getStatement(0).toString());

		simpleObjectGeneration.createObject(block, dateBp, false);
		Assert.assertEquals("GregorianCalendar calendar = new GregorianCalendar(2020, 10 - 1, 25);",
				block.getStatement(1).toString());
	}

	@Test
	public void testCreateInlineObject() {
		SimpleBluePrint<?> dateBp = factory.createBluePrint("calendar", new GregorianCalendar(2020, 10 - 1, 25));

		Assert.assertEquals("new GregorianCalendar(2020, 10 - 1, 25)",
				simpleObjectGeneration.createInlineObject(dateBp).toString());

		SimpleBluePrint<?> dateTimeBp = factory.createBluePrint("calendar",
				new GregorianCalendar(2020, 10 - 1, 25, 12, 17));
		Assert.assertEquals("new GregorianCalendar(2020, 10 - 1, 25, 12, 17)",
				simpleObjectGeneration.createInlineObject(dateTimeBp).toString());

		SimpleBluePrint<?> dateTimeWithSecondsBp = factory.createBluePrint("calendar",
				new GregorianCalendar(2020, 10 - 1, 25, 12, 17, 35));
		Assert.assertEquals("new GregorianCalendar(2020, 10 - 1, 25, 12, 17, 35)",
				simpleObjectGeneration.createInlineObject(dateTimeWithSecondsBp).toString());

		Assert.assertTrue(imports.contains(GregorianCalendar.class));
	}

}

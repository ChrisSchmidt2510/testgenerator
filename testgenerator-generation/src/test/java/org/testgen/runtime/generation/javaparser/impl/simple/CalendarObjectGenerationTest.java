package org.testgen.runtime.generation.javaparser.impl.simple;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

	private SimpleObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> simpleObjectGeneration = new CalendarObjectGeneration();

	private CalendarBluePrintFactory factory = new CalendarBluePrintFactory();

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
		SimpleBluePrint<?> dateBp = factory.createBluePrint("calendar", new GregorianCalendar(2020, 10 - 1, 25));

		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		simpleObjectGeneration.createField(cu, dateBp, true);
		assertEquals("private GregorianCalendar calendar = new GregorianCalendar(2020, 10 - 1, 25);",
				cu.getFields().get(0).toString());

		simpleObjectGeneration.createField(cu, dateBp, false);
		assertEquals("private GregorianCalendar calendar;", cu.getFields().get(1).toString());

		assertTrue(imports.contains(GregorianCalendar.class));
	}

	@Test
	public void testCreateObject() {
		SimpleBluePrint<?> dateBp = factory.createBluePrint("calendar", new GregorianCalendar(2020, 10 - 1, 25));

		BlockStmt block = new BlockStmt();

		simpleObjectGeneration.createObject(block, dateBp, true);
		assertEquals("this.calendar = new GregorianCalendar(2020, 10 - 1, 25);",
				block.getStatement(0).toString());

		simpleObjectGeneration.createObject(block, dateBp, false);
		assertEquals("GregorianCalendar calendar = new GregorianCalendar(2020, 10 - 1, 25);",
				block.getStatement(2).toString());
	}

	@Test
	public void testCreateInlineObject() {
		SimpleBluePrint<?> dateBp = factory.createBluePrint("calendar", new GregorianCalendar(2020, 10 - 1, 25));

		assertEquals("new GregorianCalendar(2020, 10 - 1, 25)",
				simpleObjectGeneration.createInlineExpression(dateBp).toString());

		SimpleBluePrint<?> dateTimeBp = factory.createBluePrint("calendar",
				new GregorianCalendar(2020, 10 - 1, 25, 12, 17));
		assertEquals("new GregorianCalendar(2020, 10 - 1, 25, 12, 17)",
				simpleObjectGeneration.createInlineExpression(dateTimeBp).toString());

		SimpleBluePrint<?> dateTimeWithSecondsBp = factory.createBluePrint("calendar",
				new GregorianCalendar(2020, 10 - 1, 25, 12, 17, 35));
		assertEquals("new GregorianCalendar(2020, 10 - 1, 25, 12, 17, 35)",
				simpleObjectGeneration.createInlineExpression(dateTimeWithSecondsBp).toString());

		assertTrue(imports.contains(GregorianCalendar.class));
	}

}

package org.testgen.runtime.generation.javaparser.impl.simple;

import static org.junit.Assert.fail;

import java.time.LocalTime;
import java.time.Month;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Assert;
import org.junit.Test;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.BooleanBluePrint.BooleanBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.CalendarBluePrint.CalendarBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.CharacterBluePrint.CharacterBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.ClassBluePrint.ClassBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.EnumBluePrint.EnumBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.JavaDateBluePrint.JavaDateBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.LocalTimeBluePrint.LocalTimeBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.NullBluePrint.NullBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.NumberBluePrint.NumberBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.StringBluePrint.StringBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.XMLGregorianCalendarBluePrint.XMLGregorianCalendarBluePrintFactory;

public class JavaParserSimpleObjectGenerationFactoryTest {

	private JavaParserSimpleObjectGenerationFactory factory = new JavaParserSimpleObjectGenerationFactory();

	@Test
	public void testSimpleGenerationBoolean() {
		BooleanBluePrintFactory booleanFactory = new BooleanBluePrintFactory();
		SimpleBluePrint<?> bluePrint = booleanFactory.createBluePrint("value", false, null).castToSimpleBluePrint();

		Assert.assertTrue(factory.of(bluePrint) instanceof BooleanObjectGeneration);
	}

	@Test
	public void testSimpleGenerationCalendar() {
		CalendarBluePrintFactory bluePrintFactory = new CalendarBluePrintFactory();
		SimpleBluePrint<?> bluePrint = bluePrintFactory
				.createBluePrint("value", new GregorianCalendar(2021, 1 - 1, 2), null).castToSimpleBluePrint();

		Assert.assertTrue(factory.of(bluePrint) instanceof CalendarObjectGeneration);
	}

	@Test
	public void testSimpleGenerationCharacter() {
		CharacterBluePrintFactory bluePrintFactory = new CharacterBluePrintFactory();
		SimpleBluePrint<?> bluePrint = bluePrintFactory.createBluePrint("value", 'C', null).castToSimpleBluePrint();

		Assert.assertTrue(factory.of(bluePrint) instanceof DateObjectGeneration);
	}

	@Test
	public void testSimpleGenerationClass() {
		ClassBluePrintFactory bluePrintFactory = new ClassBluePrintFactory();
		SimpleBluePrint<?> bluePrint = bluePrintFactory.createBluePrint("value", String.class, null)
				.castToSimpleBluePrint();

		Assert.assertTrue(factory.of(bluePrint) instanceof ClassObjectGeneration);
	}

	@Test
	public void testSimpleGenerationDate() {
		JavaDateBluePrintFactory bluePrintFactory = new JavaDateBluePrintFactory();
		@SuppressWarnings("deprecation")
		SimpleBluePrint<?> bluePrint = bluePrintFactory.createBluePrint("value", new Date(2021 - 1900, 1 - 1, 1), null)
				.castToSimpleBluePrint();

		Assert.assertTrue(factory.of(bluePrint) instanceof ClassObjectGeneration);
	}

	@Test
	public void testSimpleGenerationEnum() {
		EnumBluePrintFactory bluePrintFactory = new EnumBluePrintFactory();
		SimpleBluePrint<?> bluePrint = bluePrintFactory.createBluePrint("value", Month.JANUARY, null)
				.castToSimpleBluePrint();

		Assert.assertTrue(factory.of(bluePrint) instanceof EnumObjectGeneration);
	}

	@Test
	public void testSimpleGenerationLocalDateTime() {
		LocalTimeBluePrintFactory bluePrintFactory = new LocalTimeBluePrintFactory();
		SimpleBluePrint<?> bluePrint = bluePrintFactory.createBluePrint("value", LocalTime.of(12, 15), null)
				.castToSimpleBluePrint();

		Assert.assertTrue(factory.of(bluePrint) instanceof LocalDateTimeObjectGeneration);
	}

	@Test
	public void testSimpleGenerationNull() {
		NullBluePrintFactory bluePrintFactory = new NullBluePrintFactory();
		SimpleBluePrint<?> bluePrint = bluePrintFactory.createBluePrint("value", null, null).castToSimpleBluePrint();

		Assert.assertTrue(factory.of(bluePrint) instanceof NullObjectGeneration);
	}

	@Test
	public void testSimpleGenerationNumber() {
		NumberBluePrintFactory bluePrintFactory = new NumberBluePrintFactory();
		SimpleBluePrint<?> bluePrint = bluePrintFactory.createBluePrint("value", 5, null).castToSimpleBluePrint();

		Assert.assertTrue(factory.of(bluePrint) instanceof NumberObjectGeneration);
	}

	@Test
	public void testSimpleGenerationString() {
		StringBluePrintFactory bluePrintFactory = new StringBluePrintFactory();
		SimpleBluePrint<?> bluePrint = bluePrintFactory.createBluePrint("value", "Test", null).castToSimpleBluePrint();

		Assert.assertTrue(factory.of(bluePrint) instanceof StringObjectGeneration);
	}

	@Test
	public void testSimpleGenerationXmlGregorianCalender() {
		XMLGregorianCalendar calendar = null;

		try {
			calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar(2020, 12 - 1, 31));
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		XMLGregorianCalendarBluePrintFactory bluePrintFactory = new XMLGregorianCalendarBluePrintFactory();
		SimpleBluePrint<?> bluePrint = bluePrintFactory.createBluePrint("value", calendar, null)
				.castToSimpleBluePrint();

		Assert.assertTrue(factory.of(bluePrint) instanceof XmlGregorianCalendarObjectGeneration);
	}
}

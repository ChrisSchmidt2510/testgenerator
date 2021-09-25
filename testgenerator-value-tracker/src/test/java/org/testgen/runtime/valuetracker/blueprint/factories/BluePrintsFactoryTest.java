package org.testgen.runtime.valuetracker.blueprint.factories;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Optional;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Assert;
import org.junit.Test;
import org.testgen.runtime.valuetracker.blueprint.complextypes.ArrayBluePrint.ArrayBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.complextypes.ComplexBluePrint.ComplexBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.complextypes.LambdaExpressionBluePrint.LambdaExpressionBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.complextypes.ProxyBluePrint.ProxyBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.complextypes.ProxyBluePrintTest.ProxyTest;
import org.testgen.runtime.valuetracker.blueprint.complextypes.collections.CollectionBluePrint.CollectionBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.complextypes.collections.MapBluePrint.MapBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.BooleanBluePrint.BooleanBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.CalendarBluePrint.CalendarBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.CharacterBluePrint.CharacterBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.ClassBluePrint.ClassBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.EnumBluePrint.EnumBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.JavaDateBluePrint.JavaDateBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.LocalDateBluePrint.LocalDateBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.LocalDateTimeBluePrint.LocalDateTimeBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.LocalTimeBluePrint.LocalTimeBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.NullBluePrint.NullBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.NumberBluePrint.NumberBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.StringBluePrint.StringBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.XMLGregorianCalendarBluePrint.XMLGregorianCalendarBluePrintFactory;

public class BluePrintsFactoryTest {

	private BluePrintsFactory factory = new BluePrintsFactory();

	@Test
	public void testBooleanBluePrintFactory() {
		Optional<BluePrintFactory> bluePrintFactory = factory.getBluePrintFactory(false);

		Assert.assertTrue(bluePrintFactory.get() instanceof BooleanBluePrintFactory);
	}

	@Test
	public void testCalendarBluePrintFactory() {
		Optional<BluePrintFactory> bluePrintFactory = factory.getBluePrintFactory(new GregorianCalendar());

		Assert.assertTrue(bluePrintFactory.get() instanceof CalendarBluePrintFactory);
	}

	@Test
	public void testCharacterBluePrintFactory() {
		Optional<BluePrintFactory> bluePrintFactory = factory.getBluePrintFactory('c');

		Assert.assertTrue(bluePrintFactory.get() instanceof CharacterBluePrintFactory);
	}

	@Test
	public void testClassBluePrintFactory() {
		Optional<BluePrintFactory> bluePrintFactory = factory.getBluePrintFactory(Integer.class);

		Assert.assertTrue(bluePrintFactory.get() instanceof ClassBluePrintFactory);
	}

	@Test
	public void testEnumBluePrintFactory() {
		Optional<BluePrintFactory> bluePrintFactory = factory.getBluePrintFactory(Month.APRIL);

		Assert.assertTrue(bluePrintFactory.get() instanceof EnumBluePrintFactory);
	}

	@Test
	public void testDateBluePrintFactory() {
		Optional<BluePrintFactory> bluePrintFactory = factory.getBluePrintFactory(new Date());

		Assert.assertTrue(bluePrintFactory.get() instanceof JavaDateBluePrintFactory);
	}

	@Test
	public void testLocalDateBluePrintFactory() {
		Optional<BluePrintFactory> bluePrintFactory = factory.getBluePrintFactory(LocalDate.now());

		Assert.assertTrue(bluePrintFactory.get() instanceof LocalDateBluePrintFactory);
	}

	@Test
	public void testLocalDateTimeBluePrintFactory() {
		Optional<BluePrintFactory> bluePrintFactory = factory.getBluePrintFactory(LocalDateTime.now());

		Assert.assertTrue(bluePrintFactory.get() instanceof LocalDateTimeBluePrintFactory);
	}

	@Test
	public void testLocalTimeBluePrintFactory() {
		Optional<BluePrintFactory> bluePrintFactory = factory.getBluePrintFactory(LocalTime.now());

		Assert.assertTrue(bluePrintFactory.get() instanceof LocalTimeBluePrintFactory);
	}

	@Test
	public void testNullBluePrintFactory() {
		Optional<BluePrintFactory> bluePrintFactory = factory.getBluePrintFactory(null);

		Assert.assertTrue(bluePrintFactory.get() instanceof NullBluePrintFactory);
	}

	@Test
	public void testNumberBluePrintFactory() {
		Optional<BluePrintFactory> bluePrintFactory = factory.getBluePrintFactory(5);

		Assert.assertTrue(bluePrintFactory.get() instanceof NumberBluePrintFactory);
	}

	@Test
	public void testStringBluePrintFactory() {
		Optional<BluePrintFactory> bluePrintFactory = factory.getBluePrintFactory("Hello World");

		Assert.assertTrue(bluePrintFactory.get() instanceof StringBluePrintFactory);
	}

	@Test
	public void testXMLGregorianCalenderBluePrintFactory() throws DatatypeConfigurationException {
		XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(2020, 12, 24, 23, 55, 12,
				600, 60);

		Optional<BluePrintFactory> bluePrintFactory = factory.getBluePrintFactory(calendar);

		Assert.assertTrue(bluePrintFactory.get() instanceof XMLGregorianCalendarBluePrintFactory);
	}

	@Test
	public void testCollectionBluePrintFactory() {
		Optional<BluePrintFactory> bluePrintFactory = factory.getBluePrintFactory(new ArrayList<>());

		Assert.assertTrue(bluePrintFactory.get() instanceof CollectionBluePrintFactory);
	}

	@Test
	public void testMapBluePrintFactory() {
		Optional<BluePrintFactory> bluePrintFactory = factory.getBluePrintFactory(new HashMap<>());

		Assert.assertTrue(bluePrintFactory.get() instanceof MapBluePrintFactory);
	}

	@Test
	public void testArrayBluePrintFactory() {
		Optional<BluePrintFactory> bluePrintFactory = factory.getBluePrintFactory(new int[5]);

		Assert.assertTrue(bluePrintFactory.get() instanceof ArrayBluePrintFactory);
	}

	@Test
	public void testComplexBluePrintFactory() {
		Optional<BluePrintFactory> bluePrintFactory = factory.getBluePrintFactory(new Object());

		Assert.assertTrue(bluePrintFactory.get() instanceof ComplexBluePrintFactory);
	}

	@Test
	public void testLambdaExpressionBluePrintFactory() {
		Runnable run = () -> System.out.print("hello");

		Optional<BluePrintFactory> bluePrintFactory = factory.getBluePrintFactory(run);

		Assert.assertTrue(bluePrintFactory.get() instanceof LambdaExpressionBluePrintFactory);
	}

	@Test
	public void testProxyBluePrintFactory() {
		Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[]{ProxyTest.class}, new InvocationHandler() {
			
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				// TODO Auto-generated method stub
				return "hello";
			}
		});
		
		Optional<BluePrintFactory> bluePrintFactory = factory.getBluePrintFactory(proxy);
		
		Assert.assertTrue(bluePrintFactory.get() instanceof ProxyBluePrintFactory);
	}
}

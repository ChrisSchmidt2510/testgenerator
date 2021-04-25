package org.testgen.runtime.valuetracker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Assert;
import org.junit.Test;
import org.testgen.runtime.valuetracker.blueprint.ArrayBluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.ComplexBluePrint;
import org.testgen.runtime.valuetracker.blueprint.DateBluePrint;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.TimeBluePrint;
import org.testgen.runtime.valuetracker.blueprint.collections.CollectionBluePrint;
import org.testgen.runtime.valuetracker.blueprint.collections.MapBluePrint;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.CalendarBluePrint;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.JavaDateBluePrint;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.LocalDateTimeBluePrint;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.NumberBluePrint.NumberBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.StringBluePrint.StringBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.XMLGregorianCalendarBluePrint;
import org.testgen.runtime.valuetracker.testobjects.Adresse;
import org.testgen.runtime.valuetracker.testobjects.Person;
import org.testgen.runtime.valuetracker.testobjects.Person.Geschlecht;

public class ObjectValueTrackerTest {

	public enum TestValue {
		FIRST_VALUE, SECOND_VALUE;
	}

	private final ObjectValueTracker valueTracker = ObjectValueTracker.getInstance();

	@Test
	public void testTrackBoolean() {
		BluePrint bluePrint = valueTracker.trackValues(false, "boolean");

		Assert.assertTrue(bluePrint.isSimpleBluePrint());

		SimpleBluePrint<?> simpleBluePrint = bluePrint.castToSimpleBluePrint();
		Assert.assertEquals("boolean", simpleBluePrint.getName());
		Assert.assertEquals("false", simpleBluePrint.valueCreation());
	}

	@Test
	public void testTrackCalendar() {
		BluePrint bluePrint = valueTracker.trackValues(new GregorianCalendar(2020, 12 - 1, 31), "calendar");

		Assert.assertTrue(bluePrint.isSimpleBluePrint());

		CalendarBluePrint calendarBP = (CalendarBluePrint) bluePrint;
		Assert.assertEquals("calendar", calendarBP.getName());
		Assert.assertEquals(2020, calendarBP.getYear());
		Assert.assertEquals(11, calendarBP.getMonth());
		Assert.assertEquals(31, calendarBP.getDay());

		Assert.assertTrue(calendarBP.getHour() == 0 && calendarBP.getMinute() == 0 && calendarBP.getSecond() == 0);
	}

	@Test
	public void testTrackCharacter() {
		BluePrint bluePrint = valueTracker.trackValues('C', "char");

		Assert.assertTrue(bluePrint.isSimpleBluePrint());

		SimpleBluePrint<?> simpleBluePrint = bluePrint.castToSimpleBluePrint();
		Assert.assertEquals("char", simpleBluePrint.getName());
		Assert.assertEquals("C", simpleBluePrint.valueCreation());
	}

	@Test
	public void testTrackClass() {
		BluePrint bluePrint = valueTracker.trackValues(String.class, "class");

		Assert.assertTrue(bluePrint.isSimpleBluePrint());

		SimpleBluePrint<?> simpleBluePrint = bluePrint.castToSimpleBluePrint();
		Assert.assertEquals("class", simpleBluePrint.getName());
		Assert.assertEquals("String", simpleBluePrint.valueCreation());
	}

	@Test
	public void testTrackEnum() {
		BluePrint bluePrint = valueTracker.trackValues(TestValue.FIRST_VALUE, "testValue");

		Assert.assertTrue(bluePrint.isSimpleBluePrint());

		SimpleBluePrint<?> simpleBluePrint = bluePrint.castToSimpleBluePrint();
		Assert.assertEquals("testValue", simpleBluePrint.getName());
		Assert.assertEquals("FIRST_VALUE", simpleBluePrint.valueCreation());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testTrackDate() {
		BluePrint bluePrint = valueTracker.trackValues(new Date(2020 - 1900, 12 - 1, 31, 12, 27, 15), "date");

		Assert.assertTrue(bluePrint.isSimpleBluePrint());

		JavaDateBluePrint calendarBP = (JavaDateBluePrint) bluePrint;
		Assert.assertEquals("date", calendarBP.getName());
		Assert.assertEquals(120, calendarBP.getYear());
		Assert.assertEquals(11, calendarBP.getMonth());
		Assert.assertEquals(31, calendarBP.getDay());
		Assert.assertEquals(12, calendarBP.getHour());
		Assert.assertEquals(27, calendarBP.getMinute());
		Assert.assertEquals(15, calendarBP.getSecond());

		BluePrint bluePrint2 = valueTracker.trackValues(new java.sql.Date(2020 - 1900, 12 - 1, 24), "SqlDate");

		Assert.assertTrue(bluePrint2.isSimpleBluePrint());

		JavaDateBluePrint sqlDate = (JavaDateBluePrint) bluePrint2;
		Assert.assertEquals("SqlDate", sqlDate.getName());
		Assert.assertEquals(120, sqlDate.getYear());
		Assert.assertEquals(11, sqlDate.getMonth());
		Assert.assertEquals(24, sqlDate.getDay());

		Assert.assertTrue(sqlDate.getHour() == 0 && sqlDate.getMinute() == 0 && sqlDate.getSecond() == 0);
	}

	@Test
	public void testTrackLocalDate() {
		BluePrint bluePrint = valueTracker.trackValues(LocalDate.of(2020, Month.DECEMBER, 25), "localDate");

		Assert.assertTrue(bluePrint.isSimpleBluePrint());

		DateBluePrint dateBp = (DateBluePrint) bluePrint;

		Assert.assertEquals("localDate", bluePrint.getName());
		Assert.assertEquals(2020, dateBp.getYear());
		Assert.assertEquals(12, dateBp.getMonth());
		Assert.assertEquals(25, dateBp.getDay());
	}

	@Test
	public void testTrackLocalTime() {
		BluePrint bluePrint = valueTracker.trackValues(LocalTime.of(23, 59), "localTime");

		Assert.assertTrue(bluePrint.isSimpleBluePrint());

		TimeBluePrint timeBp = (TimeBluePrint) bluePrint;

		Assert.assertEquals("localTime", bluePrint.getName());
		Assert.assertEquals(23, timeBp.getHour());
		Assert.assertEquals(59, timeBp.getMinute());
		Assert.assertEquals(0, timeBp.getSecond());
	}

	@Test
	public void testTrackLocalDateTime() {
		BluePrint bluePrint = valueTracker.trackValues(
				LocalDateTime.of(LocalDate.of(1998, Month.OCTOBER, 25), LocalTime.of(5, 27, 30)), "localDateTime");

		Assert.assertTrue(bluePrint.isSimpleBluePrint());

		LocalDateTimeBluePrint dateTime = (LocalDateTimeBluePrint) bluePrint;

		Assert.assertEquals("localDateTime", dateTime.getName());
		Assert.assertEquals(1998, dateTime.getYear());
		Assert.assertEquals(10, dateTime.getMonth());
		Assert.assertEquals(25, dateTime.getDay());

		Assert.assertEquals(5, dateTime.getHour());
		Assert.assertEquals(27, dateTime.getMinute());
		Assert.assertEquals(30, dateTime.getSecond());
	}

	@Test
	public void testTrackNumber() {
		BluePrint bluePrint = valueTracker.trackValues(10, "Integer");

		Assert.assertTrue(bluePrint.isSimpleBluePrint());

		SimpleBluePrint<?> simpleBluePrint = bluePrint.castToSimpleBluePrint();
		Assert.assertEquals("Integer", simpleBluePrint.getName());
		Assert.assertEquals("10", simpleBluePrint.valueCreation());
	}

	@Test
	public void testTrackString() {
		BluePrint bluePrint = valueTracker.trackValues("This is a test", "Characters");

		Assert.assertTrue(bluePrint.isSimpleBluePrint());

		SimpleBluePrint<?> simpleBluePrint = bluePrint.castToSimpleBluePrint();
		Assert.assertEquals("Characters", simpleBluePrint.getName());
		Assert.assertEquals("This is a test", simpleBluePrint.valueCreation());
	}

	@Test
	public void testTrackXMLGregorianCalendar() throws DatatypeConfigurationException {
		XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(2020, 12, 24, 23, 55, 12,
				600, 60);

		BluePrint bluePrint = valueTracker.trackValues(calendar, "calendar");

		Assert.assertTrue(bluePrint.isSimpleBluePrint());

		XMLGregorianCalendarBluePrint calendarBP = (XMLGregorianCalendarBluePrint) bluePrint;

		Assert.assertEquals("calendar", calendarBP.getName());
		Assert.assertEquals(2020, calendarBP.getYear());
		Assert.assertEquals(12, calendarBP.getMonth());
		Assert.assertEquals(24, calendarBP.getDay());

		Assert.assertEquals(23, calendarBP.getHour());
		Assert.assertEquals(55, calendarBP.getMinute());
		Assert.assertEquals(12, calendarBP.getSecond());
		Assert.assertEquals(600, calendarBP.getMillisecond());
		Assert.assertEquals(60, calendarBP.getTimezone());

	}

	@Test
	public void testTrackCollections() {
		List<String> list = new ArrayList<>(Arrays.asList("Christoph", "Schmidt", "Word"));

		BluePrint bluePrint = valueTracker.trackValues(list, "Collection");

		Assert.assertTrue(bluePrint.isCollectionBluePrint());

		CollectionBluePrint collection = (CollectionBluePrint) bluePrint;
		Assert.assertEquals("Collection", collection.getName());
		Assert.assertEquals(List.class, collection.getInterfaceClass());
		Assert.assertEquals(ArrayList.class, collection.getImplementationClass());

		StringBluePrintFactory factory = new StringBluePrintFactory();

		List<BluePrint> compareList = new ArrayList<>();
		compareList.add(factory.createBluePrint("CollectionElement", "Christoph", null));
		compareList.add(factory.createBluePrint("CollectionElement", "Schmidt", null));
		compareList.add(factory.createBluePrint("CollectionElement", "Word", null));

		Assert.assertEquals(compareList, collection.getBluePrints());
	}

	@Test
	public void testTrackMap() {
		Map<Integer, String> map = new HashMap<>();
		map.put(1, "Powerpoint");
		map.put(2, "Word");
		map.put(3, "Outlook");
		map.put(4, "Exel");

		BluePrint bluePrint = valueTracker.trackValues(map, "dictionary");

		Assert.assertTrue(bluePrint.isCollectionBluePrint());

		MapBluePrint mapBP = (MapBluePrint) bluePrint;

		Assert.assertEquals("dictionary", mapBP.getName());
		Assert.assertEquals(Map.class, mapBP.getInterfaceClass());
		Assert.assertEquals(HashMap.class, mapBP.getImplementationClass());

		Set<Entry<BluePrint, BluePrint>> childs = mapBP.getBluePrints();

		for (Entry<BluePrint, BluePrint> entry : childs) {
			SimpleBluePrint<?> key = entry.getKey().castToSimpleBluePrint();
			SimpleBluePrint<?> value = entry.getValue().castToSimpleBluePrint();

			if ("1".equals(key.valueCreation())) {
				Assert.assertEquals("dictionaryKey", key.getName());

				Assert.assertEquals("dictionaryValue", value.getName());
				Assert.assertEquals("Powerpoint", value.valueCreation());
			} else if ("2".equals(key.valueCreation())) {
				Assert.assertEquals("dictionaryKey", key.getName());

				Assert.assertEquals("dictionaryValue", value.getName());
				Assert.assertEquals("Word", value.valueCreation());
			} else if ("3".equals(key.valueCreation())) {
				Assert.assertEquals("dictionaryKey", key.getName());

				Assert.assertEquals("dictionaryValue", value.getName());
				Assert.assertEquals("Outlook", value.valueCreation());
			} else if ("4".equals(key.valueCreation())) {
				Assert.assertEquals("dictionaryKey", key.getName());

				Assert.assertEquals("dictionaryValue", value.getName());
				Assert.assertEquals("Exel", value.valueCreation());
			}
		}
	}

	@Test
	public void testTrackArrays() {
		int[] array = new int[] { 10, 15, 20, 25, 30 };

		BluePrint bluePrint = valueTracker.trackValues(array, "array");

		Assert.assertTrue(bluePrint instanceof ArrayBluePrint);

		ArrayBluePrint arrayBluePrint = (ArrayBluePrint) bluePrint;
		BluePrint[] elements = arrayBluePrint.getElements();

		NumberBluePrintFactory numberFactory = new NumberBluePrintFactory();

		BluePrint[] expected = new BluePrint[5];
		expected[0] = numberFactory.createBluePrint("array1", 10, null);
		expected[1] = numberFactory.createBluePrint("array2", 15, null);
		expected[2] = numberFactory.createBluePrint("array3", 20, null);
		expected[3] = numberFactory.createBluePrint("array4", 25, null);
		expected[4] = numberFactory.createBluePrint("array5", 30, null);

		Assert.assertArrayEquals(expected, elements);
	}

	@Test
	public void testTrackMultiDimArrays() {
		int[][] array = new int[2][];
		int[] first = new int[] { 1, 2, 3, 4 };
		int[] second = new int[] { 10, 9, 8, 7, 6 };
		array[0] = first;
		array[1] = second;

		BluePrint bluePrint = valueTracker.trackValues(array, "array");

		Assert.assertTrue(bluePrint instanceof ArrayBluePrint);

		ArrayBluePrint arrayBluePrint = (ArrayBluePrint) bluePrint;
		BluePrint[] elements = arrayBluePrint.getElements();

		NumberBluePrintFactory numberFactory = new NumberBluePrintFactory();

		ArrayBluePrint firstRow = new ArrayBluePrint("array1", first, 4);
		firstRow.add(0, numberFactory.createBluePrint("array11", 1, null));
		firstRow.add(1, numberFactory.createBluePrint("array12", 2, null));
		firstRow.add(2, numberFactory.createBluePrint("array13", 3, null));
		firstRow.add(3, numberFactory.createBluePrint("array14", 4, null));

		ArrayBluePrint secondRow = new ArrayBluePrint("array2", second, 5);
		secondRow.add(0, numberFactory.createBluePrint("array21", 10, null));
		secondRow.add(1, numberFactory.createBluePrint("array22", 9, null));
		secondRow.add(2, numberFactory.createBluePrint("array23", 8, null));
		secondRow.add(3, numberFactory.createBluePrint("array24", 7, null));
		secondRow.add(4, numberFactory.createBluePrint("array25", 6, null));

		BluePrint[] expected = new BluePrint[] { firstRow, secondRow };

		Assert.assertArrayEquals(expected, elements);
	}

	@Test
	public void trackComplexType() {
		Adresse adresse = new Adresse("Nuernberg", 90402);
		adresse.setStrasse("Aeusere Nuernbergerstrasse");
		adresse.setHausnummer((short) 10);

		Person person = new Person("Schmidt", "Christoph", LocalDate.of(1993, Month.AUGUST, 17), Geschlecht.Maennlich);
		person.addAdresse(adresse);

		// set values of superclass
		person.setAedat(LocalDate.of(2020, Month.DECEMBER, 20));
		person.setErsb("Me");

		BluePrint bluePrint = valueTracker.trackValues(person, "person");

		Assert.assertTrue(bluePrint.isComplexBluePrint());

		ComplexBluePrint complex = bluePrint.castToComplexBluePrint();

		Assert.assertEquals("person", complex.getName());

		for (BluePrint child : complex.getChildBluePrints()) {
			if ("name".equals(child.getName())) {
				Assert.assertEquals("Schmidt", child.castToSimpleBluePrint().valueCreation());
			} else if ("firstName".equals(child.getName())) {
				Assert.assertEquals("Christoph", child.castToSimpleBluePrint().valueCreation());
			} else if ("dateOfBirth".equals(child.getName())) {
				DateBluePrint date = (DateBluePrint) child;

				Assert.assertEquals(1993, date.getYear());
				Assert.assertEquals(8, date.getMonth());
				Assert.assertEquals(17, date.getDay());
			} else if ("geschlecht".equals(child.getName())) {
				Assert.assertEquals("Maennlich", child.castToSimpleBluePrint().valueCreation());
			} else if ("adressen".equals(child.getName())) {
				Assert.assertTrue(child.isCollectionBluePrint());

				CollectionBluePrint collection = (CollectionBluePrint) child;

				Assert.assertEquals(List.class, collection.getInterfaceClass());
				Assert.assertEquals(ArrayList.class, collection.getImplementationClass());

				List<BluePrint> collectionChilds = collection.getBluePrints();

				Assert.assertTrue(collectionChilds.size() == 1);
				BluePrint element = collectionChilds.get(0);

				Assert.assertTrue(element.isComplexBluePrint());

				ComplexBluePrint complexBP = element.castToComplexBluePrint();

				Assert.assertEquals("adressenElement", complexBP.getName());

				for (BluePrint adresseChild : complexBP.getChildBluePrints()) {
					if ("strasse".equals(adresseChild.getName())) {
						Assert.assertEquals("Aeusere Nuernbergerstrasse",
								adresseChild.castToSimpleBluePrint().valueCreation());

					} else if ("hausnummer".equals(adresseChild.getName())) {
						Assert.assertEquals("10", adresseChild.castToSimpleBluePrint().valueCreation());
					} else if ("ort".equals(adresseChild.getName())) {
						Assert.assertEquals("Nuernberg", adresseChild.castToSimpleBluePrint().valueCreation());
					} else if ("plz".equals(adresseChild.getName())) {
						Assert.assertEquals("90402", adresseChild.castToSimpleBluePrint().valueCreation());
					}
				}
			} else if ("aedat".contentEquals(child.getName())) {
				DateBluePrint date = (DateBluePrint) child;

				Assert.assertEquals(2020, date.getYear());
				Assert.assertEquals(12, date.getMonth());
				Assert.assertEquals(20, date.getDay());
			} else if ("ersb".equals(child.getName())) {
				Assert.assertEquals("Me", child.castToSimpleBluePrint().valueCreation());
			}
		}

	}
}

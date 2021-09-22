package org.testgen.runtime.valuetracker.blueprint;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.testgen.runtime.valuetracker.CurrentlyBuildedBluePrints;
import org.testgen.runtime.valuetracker.TrackingException;
import org.testgen.runtime.valuetracker.blueprint.ComplexBluePrint.ComplexBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.collections.CollectionBluePrint;
import org.testgen.runtime.valuetracker.blueprint.factories.BluePrintsFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.EnumBluePrint.EnumBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.LocalDateBluePrint.LocalDateBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.NumberBluePrint.NumberBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.StringBluePrint.StringBluePrintFactory;
import org.testgen.runtime.valuetracker.testobjects.Adresse;
import org.testgen.runtime.valuetracker.testobjects.Person;
import org.testgen.runtime.valuetracker.testobjects.Person.Geschlecht;

public class ComplexBluePrintTest {

	private ComplexBluePrintFactory factory = new ComplexBluePrintFactory();

	private StringBluePrintFactory strFactory = new StringBluePrintFactory();

	private NumberBluePrintFactory numFactory = new NumberBluePrintFactory();

	private EnumBluePrintFactory enumFactory = new EnumBluePrintFactory();

	private LocalDateBluePrintFactory localDateFactory = new LocalDateBluePrintFactory();

	private CurrentlyBuildedBluePrints currentlyBuildedBluePrints = new CurrentlyBuildedBluePrints();

	private BluePrintsFactory bluePrintFactory = new BluePrintsFactory();

	@Test
	public void testBluePrintFactory() {
		Assert.assertTrue(factory.createBluePrintForType(new Adresse("", 5)));
		Assert.assertFalse(factory.createBluePrintForType(null));
		Assert.assertFalse(factory.createsSimpleBluePrint());
		Assert.assertEquals(-5, factory.getPriority());
	}

	private BluePrint createBluePrint(String name, Object value) {
		return bluePrintFactory.getBluePrintFactory(value).get().createBluePrint(name, value,
				currentlyBuildedBluePrints, (n, v) -> createBluePrint(n, v));
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

		BluePrint bluePrint = factory.createBluePrint("person", person, currentlyBuildedBluePrints,
				(name, value) -> createBluePrint(name, value));

		Assert.assertTrue(bluePrint.isComplexBluePrint());

		ComplexBluePrint complex = bluePrint.castToComplexBluePrint();

		Assert.assertEquals("person", complex.getName());
		Assert.assertTrue(complex.isComplexBluePrint());
		Assert.assertEquals(1, complex.getPreExecuteBluePrints().size());

		List<BluePrint> compareList = new ArrayList<>();
		compareList.add(strFactory.createBluePrint("name", "Schmidt"));
		compareList.add(strFactory.createBluePrint("firstName", "Christoph"));
		compareList.add(localDateFactory.createBluePrint("dateOfBirth", LocalDate.of(1993, Month.AUGUST, 17)));
		compareList.add(enumFactory.createBluePrint("geschlecht", Geschlecht.Maennlich));

		ComplexBluePrint complexBluePrint = new ComplexBluePrint("adressenElement", adresse);
		complexBluePrint.addBluePrint(strFactory.createBluePrint("strasse", "Aeusere Nuernbergerstrasse"));
		complexBluePrint.addBluePrint(numFactory.createBluePrint("hausnummer", (short) 10));
		complexBluePrint.addBluePrint(strFactory.createBluePrint("ort", "Nuernberg"));
		complexBluePrint.addBluePrint(numFactory.createBluePrint("plz", 90402));

		CollectionBluePrint collectionBluePrint = new CollectionBluePrint("adressen", new ArrayList<>(), List.class);
		collectionBluePrint.addBluePrint(complexBluePrint);

		compareList.add(collectionBluePrint);
		compareList.add(strFactory.createBluePrint("ersb", "Me"));
		compareList.add(localDateFactory.createBluePrint("aedat", LocalDate.of(2020, Month.DECEMBER, 20)));


		Assert.assertEquals(compareList, complex.getChildBluePrints());
	}
	
	@Test
	public void trackComplexTypeForJDKTypes() {
		Assert.assertThrows("cant create ComplexBluePrints for JDK Classes. Extend the List of SimpleBluePrints",
				TrackingException.class, () -> factory.createBluePrint("object", new Object(), currentlyBuildedBluePrints, (name, value)-> createBluePrint(name, value)));
	}

}

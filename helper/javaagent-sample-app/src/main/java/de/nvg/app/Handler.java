package de.nvg.app;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import de.nvg.bl.partner.Adresse;
import de.nvg.bl.partner.Person;
import de.nvg.bl.partner.Person.Geschlecht;

public class Handler implements InvocationHandler, Greeter {
	private Person person;

	public Handler() {
		Adresse adresse = new Adresse();
		adresse.setHausnummer((short) 15);
		adresse.setStrasse("Ostendstrasse");
		adresse.setOrt("Roth");
		adresse.setPlz(91154);

		person = new Person("Theresa", "Schmidt", LocalDate.of(2018, Month.DECEMBER, 12), Geschlecht.Weiblich);
		person.addAdresse(adresse);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (method.getName().contentEquals("greet"))
			return greet((String) args[0]);
		else if (method.getName().contentEquals("getList"))
			return getList();
		else
			return person();
	}

	@Override
	public String greet(String name) {
		return "Hello " + name;
	}

	@Override
	public Person person() {
		return person;
	}

	@Override
	public List<String> getList() {
		return new ArrayList<>();
	}

}

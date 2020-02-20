package de.nvg.app;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

import de.nvg.bl.Account;
import de.nvg.bl.partner.Adresse;
import de.nvg.bl.partner.Person;
import de.nvg.bl.partner.Person.Geschlecht;

public class Application {

	public static void main(String[] args) {
		Adresse adresse = new Adresse();
		adresse.setHausnummer((short) 100);
		adresse.setStrasse("Ostendstrasse");
		adresse.setOrt("Nuernberg");
		adresse.setPlz(90154);

		Person person = new Person("Christoph", "Schmidt", LocalDate.of(2018, Month.OCTOBER, 25), Geschlecht.Maennlich);
		System.out.println("calling person");
		person.addAdresse(adresse);
		person.setErdat(LocalDate.of(1998, 10, 25));
//		person.getAdressen().add(adresse);

		Account account = new Account();
		account.setLastLogin(LocalDateTime.now());
		account.setPerson(person);
		account.setUsername("Name123");
		account.setPassword("password");
		account.addHistorie(LocalDateTime.now(), "User logged in");

		BusinessLogik logik = new BusinessLogik();
		logik.addAdresseToPerson(person, adresse);
		logik.changePassword(account, "newPassword");
	}

}

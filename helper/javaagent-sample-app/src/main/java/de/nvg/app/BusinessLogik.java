package de.nvg.app;

import java.util.List;

import de.nvg.bl.Account;
import de.nvg.bl.partner.Adresse;
import de.nvg.bl.partner.Person;

public class BusinessLogik {

	public void addAdresseToPerson(Person person, Adresse adresse) {
		person.addAdresse(adresse);
	}

	public String changePassword(Account account, String password) {
		if (password == null) {
			System.out.println("im if Block");
//				throw new RuntimeException("Fehler!");

			return method(password);
//				return null;
		}

		System.out.println("Output Application");

		Person person = account.getPerson();
		System.out.println(person.getName());
		System.out.println(person.getFirstName());
		System.out.println(person.getDateOfBirth());
		System.out.println(person.getGeschlecht());

		Adresse includedAdresse = person.getAdressen().get(0);
		System.out.println(includedAdresse.getOrt());
		System.out.println(includedAdresse.getStrasse());
		System.out.println(includedAdresse.getHausnummer());
		System.out.println(includedAdresse.getPlz());

		account.getHistorie()
				.forEach((dateTime, action) -> System.out.println("DateTime: " + dateTime + " Action:" + action));

		account.setPassword(password);

//			return method(password);
		return password;

	}

	public String method(String passwort) {
		System.out.println(passwort);
		return passwort;
	}

	public void method2(String password, List<String> oldPasswords) {
		System.out.println(oldPasswords);
	}

}

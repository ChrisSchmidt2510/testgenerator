package de.nvg.app;

import de.nvg.bl.Account;
import de.nvg.bl.partner.Adresse;
import de.nvg.bl.partner.Person;

public class BusinessLogik {

	public void addAdresseToPerson(Person person, Adresse adresse) {
		person.addAdresse(adresse);
	}

	public void changePassword(Account account, String password) {
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

		account.setPassword(password);
	}

}

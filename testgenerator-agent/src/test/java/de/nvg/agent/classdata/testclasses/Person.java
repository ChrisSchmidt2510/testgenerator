package de.nvg.agent.classdata.testclasses;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Person extends BlObject {

	public enum Geschlecht {
		Maennlich, Weiblich, Sonstiges;
	}

	private String name;
	private String firstName;
	private LocalDate dateOfBirth;
	private Geschlecht geschlecht;
	private List<Adresse> adressen = new ArrayList<>();

	public Person(String name, String firstName, LocalDate dateOfBirth, Geschlecht geschlecht) {
		this.name = name;
		this.firstName = firstName;
		this.dateOfBirth = dateOfBirth;
		this.geschlecht = geschlecht;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public Geschlecht getGeschlecht() {
		return geschlecht;
	}

	public void setGeschlecht(Geschlecht geschlecht) {
		this.geschlecht = geschlecht;
	}

	public List<Adresse> getAdressen() {
		return Collections.unmodifiableList(adressen);
	}

	public void addAdresse(Adresse adresse) {
		Objects.requireNonNull(adresse);
		this.adressen.add(adresse);
	}

}

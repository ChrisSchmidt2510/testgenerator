package de.nvg.agent.classdata.modification.testclasses;

public class Sparte extends BlObject {

	private final Integer sparteID;
	private String bezeichnung;

	public Sparte(Integer sparteID) {
		if (sparteID == null) {
			throw new NullPointerException("SparteID darf nicht null sein");
		}
		this.sparteID = sparteID;
	}

	/**
	 * Getter for attribute <tt>sparteID</tt>
	 *
	 * @return Sparte, ist in jedem Fall ungleich <code>null</code>
	 */
	public Integer getSparteID() {
		return sparteID;
	}

	/**
	 * Getter for attribute <tt>bezeichnung</tt>
	 *
	 * @return String
	 */
	public String getBezeichnung() {
		return bezeichnung;
	}

	/**
	 * Setter for attribute <tt>bezeichnung</tt>
	 */
	public void setBezeichnung(String bezeichnung) {
		this.bezeichnung = bezeichnung;
	}

}

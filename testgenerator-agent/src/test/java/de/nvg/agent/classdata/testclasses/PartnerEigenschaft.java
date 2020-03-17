package de.nvg.agent.classdata.testclasses;

public class PartnerEigenschaft {
	private PartnerZuordnung bezugsPartnerZuordnung;
	private PartnerZuordnung partnerZuordnung;

	/**
	 * Standard Konstruktor
	 */
	public PartnerEigenschaft() {
		// nix zu tun
	}

	/**
	 * Getter for association end bezugsPartnerZuordnung.
	 */
	public PartnerZuordnung getBezugsPartnerZuordnung() {
		return bezugsPartnerZuordnung;
	}

	/**
	 * Setter for association end bezugsPartnerZuordnung.
	 */
	public void setBezugsPartnerZuordnung(PartnerZuordnung param) {
		this.bezugsPartnerZuordnung = param;
	}

	/**
	 * Getter for association end partnerZuordnung.
	 */
	public PartnerZuordnung getPartnerZuordnung() {
		return partnerZuordnung;
	}

	/**
	 * Setter for association end partnerZuordnung.
	 */
	public void setPartnerZuordnung(PartnerZuordnung param) {
		if (this.partnerZuordnung == null && param != null) {
			if (param.getPartnerEigenschaft().contains(this)) {
				this.partnerZuordnung = param;
			} else {
				throw new IllegalStateException(
						"R�ckverweis auf die Partnerzuordnung kann nicht gesetzt werden, diese Partnereigenschaft ist noch nicht in dieser Partnerzuordnung enthalten");
			}
		} else if (param == null && this.partnerZuordnung != null) {
			if (this.partnerZuordnung.getPartnerEigenschaft().contains(this)) {
				throw new IllegalStateException(
						"R�ckverweis kann nicht gel�scht werden, diese Partnereigenschaft ist noch in der Partnerzuordnung enthalten");
			} else {
				this.partnerZuordnung = param;
			}
		} else if (param != null && this.partnerZuordnung != null && !param.equals(this.partnerZuordnung)) {
			throw new IllegalStateException(
					"R�ckverweis kann nicht umgeh�ngt werden, bitte addTo... und removeFrom... Methoden der Partnerzuordnung benutzen");
		} else {
			this.partnerZuordnung = param;
		}
	}
}

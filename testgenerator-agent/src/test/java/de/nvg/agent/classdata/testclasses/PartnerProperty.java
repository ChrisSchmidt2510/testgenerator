package de.nvg.agent.classdata.testclasses;

public class PartnerProperty {
	private Partner partner;

	public PartnerProperty() {
	}

	public void setPartner(Partner param) {
		if (this.partner == null && param != null) {
			if (param.getPartnerEigenschaft().contains(this)) {
				this.partner = param;
			} else {
				throw new IllegalStateException();
			}
		} else if (param == null && this.partner != null) {
			if (this.partner.getPartnerEigenschaft().contains(this)) {
				throw new IllegalStateException();
			} else {
				this.partner = param;
			}
		} else if (param != null && this.partner != null && !param.equals(this.partner)) {
			throw new IllegalStateException();
		} else {
			this.partner = param;
		}
	}
}

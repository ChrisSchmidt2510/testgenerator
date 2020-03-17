package de.nvg.agent.classdata.testclasses;

import java.util.ArrayList;
import java.util.List;

public class Partner {
	private List<PartnerProperty> partnerProperties = new ArrayList<>();

	public List<PartnerProperty> getPartnerEigenschaft() {
		return partnerProperties;
	}

	public void setPartnerEigenschaft(List<PartnerProperty> partnerEigenschaft) {
		this.partnerProperties = partnerEigenschaft;
	}

}

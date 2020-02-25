package de.nvg.agent.classdata.modify.testclasses;

import java.util.ArrayList;
import java.util.List;

public class PartnerZuordnung
{
  private List<PartnerEigenschaft> partnerEigenschaft = new ArrayList<>();

  /**
   * @return the partnerEigenschaft
   */
  public List<PartnerEigenschaft> getPartnerEigenschaft()
  {
    return partnerEigenschaft;
  }

  /**
   * @param partnerEigenschaft the partnerEigenschaft to set
   */
  public void setPartnerEigenschaft(List<PartnerEigenschaft> partnerEigenschaft)
  {
    this.partnerEigenschaft = partnerEigenschaft;
  }

}

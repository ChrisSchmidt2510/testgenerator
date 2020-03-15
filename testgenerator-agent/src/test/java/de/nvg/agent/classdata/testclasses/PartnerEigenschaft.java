package de.nvg.agent.classdata.testclasses;

public class PartnerEigenschaft
{
  private PartnerZuordnung bezugsPartnerZuordnung;
  private PartnerZuordnung partnerZuordnung;

  /**
   * Standard Konstruktor
   */
  public PartnerEigenschaft()
  {
    // nix zu tun
  }

  /**
   * Getter for association end bezugsPartnerZuordnung.
   */
  public PartnerZuordnung getBezugsPartnerZuordnung()
  {
    return bezugsPartnerZuordnung;
  }

  /**
   * Setter for association end bezugsPartnerZuordnung.
   */
  public void setBezugsPartnerZuordnung(PartnerZuordnung param)
  {
    this.bezugsPartnerZuordnung = param;
  }

  /**
   * Getter for association end partnerZuordnung.
   */
  public PartnerZuordnung getPartnerZuordnung()
  {
    return partnerZuordnung;
  }

  /**
   * Setter for association end partnerZuordnung.
   */
  public void setPartnerZuordnung(PartnerZuordnung param)
  {
    if (this.partnerZuordnung == null && param != null)
    {
      // es war bisher keine Partnerzuordnung gesetzt, jetzt wird eine gesetzt
      // prüfen, ob diese Eigenschaft schon in der Collection der Partnerzuordnung
      // enthalten ist: nur dann darf diese Eigenschaft (Rückverweis) gesetzt werden
      if (param.getPartnerEigenschaft().contains(this))
      {
        // ok, Rückverweis setzen
        this.partnerZuordnung = param;
      }
      else
      {
        throw new IllegalStateException(
            "Rückverweis auf die Partnerzuordnung kann nicht gesetzt werden, diese Partnereigenschaft ist noch nicht in dieser Partnerzuordnung enthalten");
      }
    }
    else if (param == null && this.partnerZuordnung != null)
    {
      // Rückverweis ist gesetzt, soll aber nun aufgehoben werden
      // dies ist nur erlaubt, wenn diese Eigenschaft nicht mehr in der
      // Collection der Partnerzuordnung enthalten ist
      if (this.partnerZuordnung.getPartnerEigenschaft().contains(this))
      {
        throw new IllegalStateException(
            "Rückverweis kann nicht gelöscht werden, diese Partnereigenschaft ist noch in der Partnerzuordnung enthalten");
      }
      else
      {
        this.partnerZuordnung = param;
      }
    }
    else if (param != null && this.partnerZuordnung != null && !param.equals(this.partnerZuordnung))
    {
      // Rückverweis ist gesetzt, soll aber nun auf andere Partnerzuordnung verweisen
      // dies ist nicht erlaubt (dazu müssen addTo... und removeFrom... Methoden der
      // Partnerzuordnung verwendet werden)
      throw new IllegalStateException(
          "Rückverweis kann nicht umgehängt werden, bitte addTo... und removeFrom... Methoden der Partnerzuordnung benutzen");
    }
    else
    {
      this.partnerZuordnung = param;
    }
  }
}

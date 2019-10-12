package de.nvg.javaagent.classdata.modify.testclasses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Eine Sparte. Jeder Schaden ist einer bestimmten Sparte zugeordnet.
 *
 * @author b022037
 */
public class Sparte extends BlObject
{

  public static final Integer SPARTE_ID_AH = new Integer(30);
  public static final Integer SPARTE_ID_KH = new Integer(31);
  public static final Integer SPARTE_ID_VK = new Integer(32);
  public static final Integer SPARTE_ID_TK = new Integer(33);
  public static final Integer SPARTE_ID_ASS = new Integer(40);
  public static final Integer SPARTE_ID_S = new Integer(50);
  public static final Integer SPARTE_ID_TV = new Integer(60);
  public static final Integer SPARTE_ID_TR = new Integer(70);
  public static final Integer SPARTE_ID_UNF = new Integer(80);
  public static final Integer SPARTE_ID_LBU = new Integer(90);
  public static final Integer SPARTE_ID_LAB = new Integer(91);
  public static final Integer SPARTE_ID_LTOD = new Integer(92);
  /*NPF ist keine echte Sparte in BOSS, braucht aber ein eigenes Rechteprofil.
  Deshalb haben wir diese Pseudo-Sparte, damit die Rechte korrekt geprueft werden koennen.*/
  public static final Integer SPARTE_ID_NPFL = new Integer(99);

  public static final Sparte AH = new Sparte(SPARTE_ID_AH);
  public static final Sparte KFZ_VK = new Sparte(SPARTE_ID_VK);
  public static final Sparte KFZ_HAFT = new Sparte(SPARTE_ID_KH);
  public static final Sparte KFZ_TK = new Sparte(SPARTE_ID_TK);
  public static final Sparte KFZ_ASS = new Sparte(SPARTE_ID_ASS);
  public static final Sparte STT_S = new Sparte(SPARTE_ID_S);
  public static final Sparte STT_TV = new Sparte(SPARTE_ID_TV);
  public static final Sparte STT_TR = new Sparte(SPARTE_ID_TR);
  public static final Sparte UNF = new Sparte(SPARTE_ID_UNF);
  public static final Sparte LBU = new Sparte(SPARTE_ID_LBU);
  public static final Sparte L_AB = new Sparte(SPARTE_ID_LAB);
  public static final Sparte L_TOD = new Sparte(SPARTE_ID_LTOD);

  private final Integer sparteID;
  private String bezeichnung;

  public Sparte(Integer sparteID)
  {
    if (sparteID == null)
    {
      throw new NullPointerException("SparteID darf nicht null sein");
    }
    this.sparteID = sparteID;
  }

  /**
   * Getter for attribute <tt>sparteID</tt>
   *
   * @return Sparte, ist in jedem Fall ungleich <code>null</code>
   */
  public Integer getSparteID()
  {
    return sparteID;
  }

  /**
   * Getter for attribute <tt>bezeichnung</tt>
   *
   * @return String
   */
  public String getBezeichnung()
  {
    return bezeichnung;
  }

  /**
   * Setter for attribute <tt>bezeichnung</tt>
   */
  public void setBezeichnung(String bezeichnung)
  {
    this.bezeichnung = bezeichnung;
  }

  /**
   * Diese Funktion gibt abhängig vom "Bereich" der Sparte eine Liste aller Sparten zurück, die
   * diesem "Bereich" zugeordnet sind. Mit Bereich ist hier Haftpflicht, KFZ und Sach gemeint. Für
   * die Sparte.AH werden folgende Sparten zurückgegeben: Sparte.AH Für die Sparte.KFZ_HAFT oder
   * Sparte.KZF_TK oder Sparte.KFZ_VK oder Sparte.KFZ_ASS werden folgende Sparten zurückgegeben:
   * Sparte.KFZ_HAFT, Sparte.KZF_TK, Sparte.KFZ_VK, Sparte.KFZ_ASS Für die Sparten S, TR, TV werden
   * flgende Sparten zurückgegeben: Sparte.S, Sparte.TV, Sparte.TR
   *
   * @return Liste aller Sparten, die dem Bereich dieser speziellen Sparte zugeordnet sind
   */
  public Collection<Sparte> getBereichSparten()
  {
    return getBereichSparten(this.getSparteID());
  }

  public static Collection<Sparte> getBereichSparten(Integer sparteID)
  {
    Collection<Sparte> bereichSparten = new ArrayList<>();

    // Sparten des "Bereichs" ermitteln:
    if (Sparte.SPARTE_ID_AH.equals(sparteID))
    {
      // bei AH nur Sparte AH
      bereichSparten.add(Sparte.AH);
    }
    else if (Sparte.SPARTE_ID_VK.equals(sparteID) || Sparte.SPARTE_ID_TK.equals(sparteID)
             || Sparte.SPARTE_ID_KH.equals(sparteID) || Sparte.SPARTE_ID_ASS.equals(sparteID))
    {
      // "Bereich" ist KFZ, dann alle KFZ-Sparten
      bereichSparten.add(Sparte.KFZ_VK);
      bereichSparten.add(Sparte.KFZ_TK);
      bereichSparten.add(Sparte.KFZ_HAFT);
      bereichSparten.add(Sparte.KFZ_ASS);
    }
    else if (Sparte.SPARTE_ID_S.equals(sparteID) || Sparte.SPARTE_ID_TV.equals(sparteID)
             || Sparte.SPARTE_ID_TR.equals(sparteID))
    {
      bereichSparten.add(Sparte.STT_S);
      bereichSparten.add(Sparte.STT_TV);
      bereichSparten.add(Sparte.STT_TR);
    }
    else if (Sparte.SPARTE_ID_UNF.equals(sparteID))
    {
      bereichSparten.add(Sparte.UNF);
    }
    else if (Sparte.SPARTE_ID_LBU.equals(sparteID) || Sparte.SPARTE_ID_LAB.equals(sparteID)
             || Sparte.SPARTE_ID_LTOD.equals(sparteID))
    {
      bereichSparten.add(Sparte.LBU);
      bereichSparten.add(Sparte.L_AB);
      bereichSparten.add(Sparte.L_TOD);
    }
    else
    {
      // ansonsten nur die Sparte selbst
      bereichSparten.add(new Sparte(sparteID));
    }

    return bereichSparten;
  }

  /**
   * Es wird geprüft ob die Sparte zum Bereich AH gehört, falls ja wird TRUE zurückgeliefert
   * ansonsten FALSE
   *
   * @param sparteID
   * @return boolean
   */
  public static boolean gehoertSparteZuBereichAH(Integer sparteID)
  {
    if (Sparte.AH.getSparteID().equals(sparteID))
    {
      return true;
    }
    else
    {
      return false;
    }

  }

  public boolean gehoertSparteZuBereichAH()
  {
    return gehoertSparteZuBereichAH(sparteID);
  }

  /**
   * Es wird geprüft ob die Sparte zum Bereich KFZ gehört, falls ja wird TRUE zurückgeliefert
   * ansonsten FALSE
   *
   * @param sparteID
   * @return boolean
   */
  public static boolean gehoertSparteZuBereichKFZ(Integer sparteID)
  {
    if (Sparte.KFZ_HAFT.getSparteID().equals(sparteID) || Sparte.KFZ_TK.getSparteID().equals(sparteID)
        || Sparte.KFZ_VK.getSparteID().equals(sparteID) || Sparte.KFZ_ASS.getSparteID().equals(sparteID))
    {
      return true;
    }
    else
    {
      return false;
    }
  }

  /**
   * Es wird geprüft ob die Sparte zum Bereich KFZ gehört, falls ja wird TRUE zurückgeliefert
   * ansonsten FALSE
   *
   * @param sparteID
   * @return boolean
   */
  public static boolean gehoertSparteZuBereichKFZ_Haft(Integer sparteID)
  {
    if (Sparte.KFZ_HAFT.getSparteID().equals(sparteID))
    {
      return true;
    }
    else
    {
      return false;
    }
  }

  public boolean gehoertSparteZuBereichKFZ_Haft()
  {
    return gehoertSparteZuBereichKFZ_Haft(sparteID);
  }

  public boolean gehoertSparteZuBereichKFZ()
  {
    return gehoertSparteZuBereichKFZ(sparteID);
  }

  /**
   * Prüfen, ob die sparte eine KFZ Kaskosparte ist, aktuell also 32 oder 33
   *
   * @param sparteID
   * @return
   */
  public static boolean gehoertSparteZuBereichKFZ_Kasko(Integer sparteID)
  {
    if (Sparte.KFZ_TK.getSparteID().equals(sparteID) || Sparte.KFZ_VK.getSparteID().equals(sparteID))
    {
      return true;
    }
    else
    {
      return false;
    }
  }

  public boolean gehoertSparteZuBereichKFZ_Kasko()
  {
    return gehoertSparteZuBereichKFZ_Kasko(sparteID);
  }

  /**
   * Es wird geprüft ob die Sparte zum Bereich STT gehört, falls ja wird TRUE zurückgeliefert
   * ansonsten FALSE
   *
   * @param sparteID
   * @return boolean
   */
  public static boolean gehoertSparteZuBereichSTT(Integer sparteID)
  {
    if (Sparte.STT_S.getSparteID().equals(sparteID) || Sparte.STT_TR.getSparteID().equals(sparteID)
        || Sparte.STT_TV.getSparteID().equals(sparteID))
    {
      return true;
    }
    else
    {
      return false;
    }
  }

  public boolean gehoertSparteZuBereichSTT()
  {
    return gehoertSparteZuBereichSTT(sparteID);
  }

  /**
   * Es wird geprüft ob die Sparte zum Bereich STT gehört, falls ja wird TRUE zurückgeliefert
   * ansonsten FALSE
   *
   * @param sparteID
   * @return boolean
   */
  public static boolean gehoertSparteZuBereichUnfall(Integer sparteID)
  {
    if (Sparte.UNF.getSparteID().equals(sparteID))
    {
      return true;
    }
    else
    {
      return false;
    }
  }

  public boolean gehoertSparteZuBereichUnfall()
  {
    return gehoertSparteZuBereichUnfall(sparteID);
  }

  public static boolean gehoertSparteZuBereichLeben(Sparte sparte)
  {
    return sparte != null && gehoertSparteZuBereichLeben(sparte.getSparteID());
  }

  public static boolean gehoertSparteZuBereichLeben(Integer sparteID)
  {
    /**
     * ACHTUNG: Wird hier eine Spartehinzugefuegt, muss DB-Trigger
     *          TRG_SCHADEN_VERZEICHNIS angepasst werden!
     */
    return Sparte.LBU.getSparteID().equals(sparteID) || Sparte.L_AB.getSparteID().equals(sparteID)
           || Sparte.L_TOD.getSparteID().equals(sparteID);
  }

  public boolean gehoertSparteZuBereichLeben()
  {
    return gehoertSparteZuBereichLeben(sparteID);
  }

  /**
   * Prüft, ob das übergebe Objekt mit dieser Sparte übereinstimmt. Eine Übereinstimmung ist
   * gegeben, wenn das Objekt auch eine Instanz der Sparte-Klasse ist und beide dieselbe SparteID
   * haben.
   *
   * @param o
   * @return boolean
   */
  @Override
  public boolean equals(Object o)
  {
    if (o instanceof Sparte)
    {
      Sparte that = (Sparte) o;
      return sparteID.equals(that.sparteID);
    }
    return false;
  }

  @Override
  public int hashCode()
  {
    return sparteID.hashCode();
  }

  public static List<Integer> getAlleSpartenIdsSHUK()
  {
    return Arrays.asList(SPARTE_ID_AH, SPARTE_ID_KH, SPARTE_ID_VK, SPARTE_ID_TK, SPARTE_ID_ASS, SPARTE_ID_S,
        SPARTE_ID_TV, SPARTE_ID_TR, SPARTE_ID_UNF);
  }

  public boolean istSparteLebenAblaufOderTod()
  {
    return Sparte.istSparteLebenAblaufOderTod(getSparteID());
  }

  public static boolean istSparteLebenAblaufOderTod(Integer sparteID)
  {
    return Sparte.SPARTE_ID_LAB.equals(sparteID) || Sparte.SPARTE_ID_LTOD.equals(sparteID);
  }

  public boolean istSparteLebenBuOderTod()
  {
    return Sparte.istSparteLebenBuOderTod(getSparteID());
  }

  public static boolean istSparteLebenBuOderTod(Integer sparteID)
  {
    return Sparte.SPARTE_ID_LBU.equals(sparteID) || Sparte.SPARTE_ID_LTOD.equals(sparteID);
  }
}

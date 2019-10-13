package de.nvg.javaagent.classdata.modify.testclasses;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;

public class TeilDatum
{
  /** Tag des (Teil-)Datums */
  private Integer tag;
  /** Monat des (Teil-)Datums */
  private Integer monat;
  /** Jahr des (Teil-)Datums */
  private Integer jahr;
  /** Stunde des (Teil-)Datums */
  private Integer stunde;
  /** Minute des (Teil-)Datums */
  private Integer minute;

  public TeilDatum()
  {
    this(null, null, null);
  }

  /**
   * @param datum
   */
  public TeilDatum(Date datum)
  {
    setzeDatum(datum);
  }

  public TeilDatum(LocalDate ld)
  {
    setzeDatum(ld.getDayOfMonth(), ld.getMonthValue(), ld.getYear());
  }

  /**
   * @param datum
   */
  public TeilDatum(Timestamp datum)
  {
    setzeDatum(datum);
  }

  /**
   * User defined constructor <tt>TeilDatum</tt>
   *
   * @param tag
   * @param monat
   * @param jahr
   */
  public TeilDatum(Integer tag, Integer monat, Integer jahr)
  {
    setzeDatum(tag, monat, jahr);
  }

  /**
   * User defined operation <tt>setzeDatum</tt>
   *
   * @param tag
   * @param monat
   * @param jahr
   */
  public void setzeDatum(Integer tag, Integer monat, Integer jahr)
  {
    this.tag = tag;
    this.monat = monat;
    this.jahr = jahr;
  }

  /**
   * Erwartet ein Datum als String im Format "dd.mm.jjjj". Die Funktion versucht, den String zu
   * parsen. Sollte ein gültiges Datum ermittelt werden, wird es in die internen Felder Tag, Monat
   * und Jahr übernommen. Hinweis: Tag und Monat dürfen auch einstellig im String vorkommen
   * ("d.m.jjjj").
   *
   * @param datumText public void setzeDatum(String datumText) { }
   */

  /**
   * Diese Funktion übernimmt die Werte für Tag, Monat, Jahr, Stunde und Minute aus dem übergebenen
   * Date Objekt.
   *
   * @param datum
   */
  public void setzeDatum(Date datum)
  {
    if (datum == null)
    {
      this.tag = null;
      this.monat = null;
      this.jahr = null;
      this.stunde = null;
      this.minute = null;
    }
    else
    {
      Calendar cal = Calendar.getInstance();
      cal.setTime(datum);

      this.tag = new Integer(cal.get(Calendar.DAY_OF_MONTH));
      this.monat = new Integer(cal.get(Calendar.MONTH) + 1);
      this.jahr = new Integer(cal.get(Calendar.YEAR));
      this.stunde = new Integer(cal.get(Calendar.HOUR_OF_DAY));
      this.minute = new Integer(cal.get(Calendar.MINUTE));
    }
  }

  /**
   * User defined operation <tt>setzeZeit</tt>
   *
   * @param zeitText
   */
  public void setzeZeit(String zeitText)
  {
    if (zeitText == null)
    {
      setStunde(null);
      setMinute(null);
    }
    else
    {
      try
      {
        // Format für Zeitangabe: [h]h:mm
        final MessageFormat FMT_ZEIT_STRING = new MessageFormat("{0,number,#0}:{1,number,00}");
        Object[] objs = FMT_ZEIT_STRING.parse(zeitText);
        int hh = ((Number) objs[0]).intValue();
        int mm = ((Number) objs[1]).intValue();
        setStunde(new Integer(hh));
        setMinute(new Integer(mm));
      }
      catch (ParseException e)
      {
        throw new IllegalArgumentException(
            "Der übergebene zeitText kann nicht geparst werden: " + e.toString());
      }
    }
  }

  /**
   * Erwartet Stunde und Minute jeweils als Integer-Wert. Die Werte werden über die entsprechenden
   * Methoden <code>setStunde</code> und <code>setMinute</code> ins TeilDatum übernommen. Prüfungen
   * der Werte finden also evtl dort statt.
   *
   * @see Teildatum#setStunde(Integer)
   * @see Teildatum#setMinute(Integer)
   * @param stunde Integer
   * @param minute Integer
   */
  public void setzeZeit(Integer stunde, Integer minute)
  {
    setStunde(stunde);
    setMinute(minute);
  }

  /**
   * Prüft, ob das Datum komplett ist. Komplett bedeutet, dass Jahr, Monat und Tag versorgt sind.
   * Stunde und Minute wird dabei ignoriert. Wenn das Datum komplett ist, gibt diese Funktion "true"
   * zurück, ansonsten "false".
   *
   * @return boolean
   */
  public boolean isKomplett()
  {
    return (tag != null && tag.intValue() > 0) && (monat != null && monat.intValue() > 0)
           && (jahr != null && jahr.intValue() > 0);
  }

  /**
   * Gibt <code>true</code> zurück, wenn das TeilDatum komplett ist (siehe isKomplett() ) und ein
   * entsprechendes Date mit den Daten initialisiert werden kann.
   *
   * @return boolean
   */
  public boolean isGueltig()
  {
    if (isKomplett() == false)
    {
      return false;
    }

    try
    {
      getDatum(true);
      return true;
    }
    catch (Exception e)
    {
      return false;
    }
  }

  /**
   * Gibt <code>true</code> zurück, wenn Tag, Monat und Jahr nicht gefuellt sind
   * @return boolean
   */
  public boolean isLeeresTeildatum()
  {
    return jahr == null && monat == null && tag == null;
  }

  /**
   * Sollten die Felder Tag, Monat und Jahr gefüllt sein, gibt die Funktion ein entsprechend
   * initialisiertes Date zurück. Es wird nicht auf gueltigkeit gerprueft
   *
   * @return Date
   */
  public Date getDatum()
  {
    return getDatum(false);
  }

  /**
   * Sollten die Felder Tag, Monat und Jahr gefüllt sein, gibt die Funktion ein entsprechend
   * initialisiertes Date zurück. pruefung auf geultigkeit ist optional mögl.
   *
   * @param gueltigkeitPruefen boolean
   * @return Date
   */
  private Date getDatum(boolean gueltigkeitPruefen)
  {
    if (isKomplett())
    {
      int iTag = (tag == null
          ? 0
          : tag.intValue());
      int iMonat = (monat == null
          ? 0
          : monat.intValue());
      int iJahr = (jahr == null
          ? 0
          : jahr.intValue());
      int iStunde = (stunde == null
          ? 0
          : stunde.intValue());
      int iMinute = (minute == null
          ? 0
          : minute.intValue());

      Calendar cal = new GregorianCalendar(iJahr, iMonat - 1, iTag, iStunde, iMinute);
      if (gueltigkeitPruefen)
      {
        cal.setLenient(false);
      }

      return cal.getTime();
    }
    else
    {
      throw new IllegalStateException(
          "es muss mindestens Tag, Monat und Jahr gesetzt sein, um ein Date zurückzugeben");
    }
  }

  /**
   * Klont dieses Objekt.
   *
   * @return Object
   */
  @Override
  public TeilDatum clone()
  {
    try
    {
      TeilDatum cln = (TeilDatum) super.clone();

      return cln;
    }
    catch (CloneNotSupportedException e)
    {
      // this shouldn't happen, since we are Cloneable
      throw new InternalError();
    }
  }

  /**
   * Gibt die interne Uhrzeit im Format "hh:mm" zurück. Sollte Stunde und Minute nicht gesetzt sein
   * (null), dann gibt diese Funktion null zurück.
   *
   * @return String
   */
  public String getZeitString()
  {
    if (stunde == null && minute == null)
    {
      return null;
    }
    Integer[] params = new Integer[2];
    if (stunde == null)
    {
      params[0] = new Integer(0);
    }
    else
    {
      params[0] = stunde;
    }
    if (minute == null)
    {
      params[1] = new Integer(0);
    }
    else
    {
      params[1] = minute;
    }
    final MessageFormat FMT_ZEIT_STRING = new MessageFormat("{0,number,#0}:{1,number,00}");
    return FMT_ZEIT_STRING.format(params);
  }

  /**
   * Getter for attribute <tt>tag</tt>
   */
  public Integer getTag()
  {
    return tag;
  }

  /**
   * Setter for attribute <tt>tag</tt>
   */
  public void setTag(Integer tag)
  {
    this.tag = tag;
  }

  /**
   * Getter for attribute <tt>monat</tt>
   */
  public Integer getMonat()
  {
    return monat;
  }

  /**
   * Setter for attribute <tt>monat</tt>
   */
  public void setMonat(Integer monat)
  {
    this.monat = monat;
  }

  /**
   * Getter for attribute <tt>jahr</tt>
   */
  public Integer getJahr()
  {
    return jahr;
  }

  /**
   * Setter for attribute <tt>jahr</tt>
   */
  public void setJahr(Integer jahr)
  {
    this.jahr = jahr;
  }

  /**
   * Getter for attribute <tt>stunde</tt>
   */
  public Integer getStunde()
  {
    return stunde;
  }

  /**
   * Setter for attribute <tt>stunde</tt>
   */
  public void setStunde(Integer stunde)
  {
    if (stunde != null)
    {
      int hh = stunde.intValue();
      if (hh < 0 || hh > 23)
      {
        throw new IllegalArgumentException("Der Wert für Stunde muss zwischen 0 und 23 liegen");
      }
    }
    this.stunde = stunde;
  }

  /**
   * Getter for attribute <tt>minute</tt>
   */
  public Integer getMinute()
  {
    return minute;
  }

  /**
   * Setter for attribute <tt>minute</tt>
   */
  public void setMinute(Integer minute)
  {
    if (minute != null)
    {
      int mm = minute.intValue();
      if (mm < 0 || mm > 59)
      {
        throw new IllegalArgumentException("Der Wert für Minute muss zwischen 0 und 59 liegen");
      }
    }
    this.minute = minute;
  }

  /**
   * Diese Funktion prüft, ob das übergebene Objekt ein TeilDatum ist, das identische Werte in Tag,
   * Monat, Jahr, Stunde und Minute hat.
   *
   * @param o Objekt, das mit diesem TeilDatum verglichen werden soll
   * @return Gibt <code>true</code> zurück, wenn das übergebene Objekt identische Werte in Tag,
   *         Monat, Jahr, Stunde und Minute hat. Ansonsten wird <code>false</code> zurückgegeben.
   */
  @Override
  public boolean equals(Object o)
  {
    if (o instanceof TeilDatum)
    {
      TeilDatum cmp = (TeilDatum) o;
      boolean eq = (tag == null
          ? cmp.tag == null
          : tag.equals(cmp.tag));
      eq = eq && (monat == null
          ? cmp.monat == null
          : monat.equals(cmp.monat));
      eq = eq && (jahr == null
          ? cmp.jahr == null
          : jahr.equals(cmp.jahr));
      eq = eq && (stunde == null
          ? cmp.stunde == null
          : stunde.equals(cmp.stunde));
      eq = eq && (minute == null
          ? cmp.minute == null
          : minute.equals(cmp.minute));
      return eq;
    }
    return false;
  }

  /**
   * (non-Javadoc)
   *
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;

    result = prime * result + ((tag == null)
        ? 0
        : tag.hashCode());
    result = prime * result + ((monat == null)
        ? 0
        : monat.hashCode());
    result = prime * result + ((jahr == null)
        ? 0
        : jahr.hashCode());
    result = prime * result + ((stunde == null)
        ? 0
        : stunde.hashCode());
    result = prime * result + ((minute == null)
        ? 0
        : minute.hashCode());

    return result;
  }

  @Override
  public String toString()
  {
    StringBuffer sb = new StringBuffer();

    sb.append(tag);
    sb.append('.');
    sb.append(monat);
    sb.append('.');
    sb.append(jahr);
    sb.append(' ');
    sb.append(getZeitString());

    return sb.toString();
  }

  /**
   * <p>
   * Erzeugt ein mit Tag, Monat, Jahr gefülltes Teildatum aus einem String ohne Trennzeichen (also
   * TTMMJJJJ). Einzelne Bestandteile des Datums können 0 sein.
   * <p>
   * Wenn null oder ein leerer String als Parameter übergeben wird, dann wird null zurückgegeben.
   *
   * @param s Datum im Format TTMMJJJJ
   * @return gefülltes Teildatum oder null.
   */
  public static TeilDatum buildTMJ_ohneTrennzeichen(String s)
  {
    if (s == null)
    {
      return null;
    }
    s = s.trim();
    if (s.length() == 0)
    {
      return null;
    }

    if (s.length() != 8)
    {
      throw new IllegalArgumentException(
          "Parameter hat ungültige Länge. Erwartet wurden 8 Zeichen (TTMMJJJJ), geliefert wurden "
                                         + s.length() + " Zeichen");
    }

    String t = s.substring(0, 2);
    Integer tag = new Integer(t);
    String m = s.substring(2, 4);
    Integer monat = new Integer(m);
    String j = s.substring(4);
    Integer jahr = new Integer(j);

    TeilDatum td = new TeilDatum(tag, monat, jahr);
    return td;
  }

  public void addiereMonate(int monate)
  {
    Objects.requireNonNull(monat, "monat");

    // 1+1 2
    // 12+1 = 13
    // 11 + 14 = 25

    monat = monat + monate;

    if (monat > 12)
    {
      Objects.requireNonNull(jahr, "jahr");

      int diffMonate = monat - 12;

      monat = 1;

      int jahre = diffMonate / 12;

      if (diffMonate % 12 > 0)
      {
        jahre++;
      }

      jahr = jahr + jahre;
    }
  }
}

package de.nvg.agent.classdata.testclasses;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class TeilDatum {
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

	public TeilDatum(LocalDate ld) {
		setzeDatum(ld.getDayOfMonth(), ld.getMonthValue(), ld.getYear());
	}

	/**
	 * User defined operation <tt>setzeDatum</tt>
	 *
	 * @param tag
	 * @param monat
	 * @param jahr
	 */
	public void setzeDatum(Integer tag, Integer monat, Integer jahr) {
		this.tag = tag;
		this.monat = monat;
		this.jahr = jahr;
	}

	/**
	 * Diese Funktion uebernimmt die Werte fuer Tag, Monat, Jahr, Stunde und Minute
	 * aus dem uebergebenen Date Objekt.
	 *
	 * @param datum
	 */
	public void setzeDatum(Date datum) {
		if (datum == null) {
			this.tag = null;
			this.monat = null;
			this.jahr = null;
			this.stunde = null;
			this.minute = null;
		} else {
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
	 * Getter for attribute <tt>tag</tt>
	 */
	public Integer getTag() {
		return tag;
	}

	/**
	 * Setter for attribute <tt>tag</tt>
	 */
	public void setTag(Integer tag) {
		this.tag = tag;
	}

	/**
	 * Getter for attribute <tt>monat</tt>
	 */
	public Integer getMonat() {
		return monat;
	}

	/**
	 * Setter for attribute <tt>monat</tt>
	 */
	public void setMonat(Integer monat) {
		this.monat = monat;
	}

	/**
	 * Getter for attribute <tt>jahr</tt>
	 */
	public Integer getJahr() {
		return jahr;
	}

	/**
	 * Setter for attribute <tt>jahr</tt>
	 */
	public void setJahr(Integer jahr) {
		this.jahr = jahr;
	}

	/**
	 * Getter for attribute <tt>stunde</tt>
	 */
	public Integer getStunde() {
		return stunde;
	}

	/**
	 * Setter for attribute <tt>stunde</tt>
	 */
	public void setStunde(Integer stunde) {
		if (stunde != null) {
			int hh = stunde.intValue();
			if (hh < 0 || hh > 23) {
				throw new IllegalArgumentException("Der Wert fuer Stunde muss zwischen 0 und 23 liegen");
			}
		}
		this.stunde = stunde;
	}

	/**
	 * Getter for attribute <tt>minute</tt>
	 */
	public Integer getMinute() {
		return minute;
	}

	/**
	 * Setter for attribute <tt>minute</tt>
	 */
	public void setMinute(Integer minute) {
		if (minute != null) {
			int mm = minute.intValue();
			if (mm < 0 || mm > 59) {
				throw new IllegalArgumentException("Der Wert fuer Minute muss zwischen 0 und 59 liegen");
			}
		}
		this.minute = minute;
	}

	public void addiereMonate(int monate) {
		Objects.requireNonNull(monat, "monat");

		// 1+1 2
		// 12+1 = 13
		// 11 + 14 = 25

		monat = monat + monate;

		if (monat > 12) {
			Objects.requireNonNull(jahr, "jahr");

			int diffMonate = monat - 12;

			monat = 1;

			int jahre = diffMonate / 12;

			if (diffMonate % 12 > 0) {
				jahre++;
			}

			jahr = jahr + jahre;
		}
	}
}

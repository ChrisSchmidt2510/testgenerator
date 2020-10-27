package org.testgen.agent.classdata.testclasses;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class FragmentDate {
	@SuppressWarnings("unused")
	private Integer day;
	private Integer month;
	private Integer year;
	@SuppressWarnings("unused")
	private Integer hour;
	@SuppressWarnings("unused")
	private Integer minute;

	public FragmentDate(LocalDate ld) {
		setDate(ld.getDayOfMonth(), ld.getMonthValue(), ld.getYear());
	}

	public void setDate(Integer day, Integer month, Integer year) {
		this.day = day;
		this.month = month;
		this.year = year;
	}

	public void setDate(Date date) {
		if (date == null) {
			this.day = null;
			this.month = null;
			this.year = null;
			this.hour = null;
			this.minute = null;
		} else {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);

			this.day = new Integer(cal.get(Calendar.DAY_OF_MONTH));
			this.month = new Integer(cal.get(Calendar.MONTH) + 1);
			this.year = new Integer(cal.get(Calendar.YEAR));
			this.hour = new Integer(cal.get(Calendar.HOUR_OF_DAY));
			this.minute = new Integer(cal.get(Calendar.MINUTE));
		}
	}

	public void addMonths(int monate) {
		Objects.requireNonNull(month, "month");

		// 1+1 2
		// 12+1 = 13
		// 11 + 14 = 25

		month = month + monate;

		if (month > 12) {
			Objects.requireNonNull(year, "year");

			int diffMonate = month - 12;

			month = 1;

			int jahre = diffMonate / 12;

			if (diffMonate % 12 > 0) {
				jahre++;
			}

			year = year + jahre;
		}
	}
}

package de.nvg.proxy;

import java.time.LocalDate;
import java.util.Objects;

import de.nvg.proxy.impl.ReferenceProxy;

public class ProxyTestBean {
	private ReferenceProxy<LocalDate> date = new ReferenceProxy<>(LocalDate.now(), this, "date");

	public LocalDate getDate() {
		return date.getValue();
	}

	public void setDate(LocalDate date) {
		this.date.setValue(date);
	}

	@Override
	public int hashCode() {
		return date.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ProxyTestBean))
			return false;
		ProxyTestBean other = (ProxyTestBean) obj;
		return Objects.equals(date, other.date);
	}

	@Override
	public String toString() {
		return "ProxyTestBean [date=" + date + "]";
	}

}

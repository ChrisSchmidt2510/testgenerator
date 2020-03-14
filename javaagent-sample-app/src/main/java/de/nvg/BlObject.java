package de.nvg;

import java.time.LocalDate;
import java.util.Objects;

public abstract class BlObject {
	private LocalDate erdat;
	private String ersb;
	private LocalDate aedat;
	private String aesb;

	@Override
	public int hashCode() {
		return Objects.hash(aedat, aesb, erdat, ersb);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof BlObject)) {
			return false;
		}
		BlObject other = (BlObject) obj;
		return Objects.equals(aedat, other.aedat) && Objects.equals(aesb, other.aesb)
				&& Objects.equals(erdat, other.erdat) && Objects.equals(ersb, other.ersb);
	}

	public LocalDate getErdat() {
		return erdat;
	}

	public void setErdat(LocalDate erdat) {
		this.erdat = erdat;
	}

	public String getErsb() {
		return ersb;
	}

	public void setErsb(String ersb) {
		this.ersb = ersb;
	}

	public LocalDate getAedat() {
		return aedat;
	}

	public void setAedat(LocalDate aedat) {
		this.aedat = aedat;
	}

	public String getAesb() {
		return aesb;
	}

	public void setAesb(String aesb) {
		this.aesb = aesb;
	}

}

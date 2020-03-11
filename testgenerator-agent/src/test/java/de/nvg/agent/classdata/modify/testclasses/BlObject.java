package de.nvg.agent.classdata.modify.testclasses;

import java.time.LocalDate;
import java.util.Objects;

import de.nvg.runtime.classdatamodel.ClassData;
import de.nvg.runtime.classdatamodel.ConstructorData;
import de.nvg.runtime.classdatamodel.FieldData;
import de.nvg.runtime.classdatamodel.SetterMethodData;

public abstract class BlObject {
	private LocalDate erdat;
	private String ersb;
	private LocalDate aedat;
	private String aesb;

	private static ClassData classData;

	static {
		classData = new ClassData("de.nvg.BlObject", new ConstructorData(true));
		classData.addFieldSetterPair(new FieldData(false, "erdat", "java.time.LocalDate"),
				new SetterMethodData("setErdat", "(Ljava/time/LocalDate;)V", false));
		classData.addFieldSetterPair(new FieldData(false, "ersb", "java.lang.String"),
				new SetterMethodData("setErsb", "(Ljava/lang/String)V;", false));
	}

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

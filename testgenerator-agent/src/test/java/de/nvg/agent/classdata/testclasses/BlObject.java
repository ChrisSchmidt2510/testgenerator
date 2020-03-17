package de.nvg.agent.classdata.testclasses;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import de.nvg.runtime.classdatamodel.ClassData;
import de.nvg.runtime.classdatamodel.ConstructorData;
import de.nvg.runtime.classdatamodel.FieldData;
import de.nvg.runtime.classdatamodel.SetterMethodData;
import de.nvg.runtime.classdatamodel.SignatureData;

public abstract class BlObject {
	private LocalDate erdat;
	private String ersb;
	private LocalDate aedat;
	private String aesb;

	private static ClassData testgenerator$classData;

	static {
		testgenerator$classData = new ClassData("de.nvg.BlObject", new ConstructorData(true));
		testgenerator$classData.addFieldSetterPair(new FieldData(false, "erdat", LocalDate.class),
				new SetterMethodData("setErdat", "(Ljava/time/LocalDate;)V", false));

		FieldData ersb = new FieldData(false, "ersb", String.class);
		SignatureData signature = new SignatureData(List.class);
		signature.addSubType(new SignatureData(LocalDate.class));
		testgenerator$classData.addFieldSetterPair(ersb,
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

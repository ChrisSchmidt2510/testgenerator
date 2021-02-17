package org.testgen.agent.classdata.testclasses;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.testgen.runtime.classdata.ClassDataHolder;
import org.testgen.runtime.classdata.model.ClassData;
import org.testgen.runtime.classdata.model.ConstructorData;
import org.testgen.runtime.classdata.model.FieldData;
import org.testgen.runtime.classdata.model.SetterMethodData;
import org.testgen.runtime.classdata.model.SetterType;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;

public abstract class BlObject implements ClassDataHolder {
	private LocalDate erdat;
	private String ersb;
	private LocalDate aedat;
	private String aesb;

	public static ClassData getTestgenerator$$ClassData() {
		ClassData classData = new ClassData("de.nvg.BlObject", new ConstructorData(true));
		classData.addFieldSetterPair(new FieldData(false, "erdat", LocalDate.class),
				new SetterMethodData("setErdat", "(Ljava/time/LocalDate;)V", false, SetterType.VALUE_SETTER));

		FieldData ersb = new FieldData(false, "ersb", String.class);
		SignatureType signature = new SignatureType(List.class);
		signature.addSubType(new SignatureType(LocalDate.class));
		classData.addFieldSetterPair(ersb,
				new SetterMethodData("setErsb", "(Ljava/lang/String)V;", false, SetterType.VALUE_SETTER));

		return classData;
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

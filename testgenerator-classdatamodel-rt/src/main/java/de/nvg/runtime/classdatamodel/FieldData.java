package de.nvg.runtime.classdatamodel;

import java.util.Objects;

public class FieldData {
	private final boolean isPublic;
	private final String name;
	private final String descriptor;
	private SignatureData signature;

	public FieldData(String name, String descriptor) {
		this(false, name, descriptor);
	}

	public FieldData(boolean isPublic, String name, String descriptor) {
		this.isPublic = isPublic;
		this.name = name;
		this.descriptor = descriptor;
	}

	public void setSignature(SignatureData signature) {
		this.signature = signature;
	}

	public SignatureData getSignature() {
		return signature;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public String getName() {
		return name;
	}

	public String getDescriptor() {
		return descriptor;
	}

	@Override
	public int hashCode() {
		return Objects.hash(descriptor, name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof FieldData)) {
			return false;
		}
		FieldData other = (FieldData) obj;
		return Objects.equals(descriptor, other.descriptor) && Objects.equals(name, other.name);
	}

	@Override
	public String toString() {
		return "FieldData [name=" + name + ", descriptor=" + descriptor + "]";
	}

}

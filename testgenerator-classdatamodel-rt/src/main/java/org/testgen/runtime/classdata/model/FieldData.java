package org.testgen.runtime.classdata.model;

import java.util.Objects;

import org.testgen.runtime.classdata.model.descriptor.SignatureType;

public class FieldData {
	private final boolean isPublic;
	private final String name;
	private final Class<?> descriptor;
	private SignatureType signature;

	public FieldData(String name, Class<?> descriptor) {
		this(false, name, descriptor);
	}

	public FieldData(boolean isPublic, String name, Class<?> descriptor) {
		this.isPublic = isPublic;
		this.name = name;
		this.descriptor = descriptor;
	}

	public void setSignature(SignatureType signature) {
		this.signature = signature;
	}

	public SignatureType getSignature() {
		return signature;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public String getName() {
		return name;
	}

	public Class<?> getDescriptor() {
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

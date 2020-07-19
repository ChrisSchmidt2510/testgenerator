package org.testgen.runtime.classdata.model.descriptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SignatureType implements DescriptorType {
	private final Class<?> basetype;
	private final List<SignatureType> subTypes = new ArrayList<>();

	public SignatureType(Class<?> type) {
		this.basetype = type;
	}

	public boolean isSimpleSignature() {
		return subTypes.isEmpty();
	}

	public void addSubType(SignatureType subType) {
		this.subTypes.add(subType);
	}

	public Class<?> getType() {
		return basetype;
	}

	public List<SignatureType> getSubTypes() {
		return Collections.unmodifiableList(subTypes);
	}

	@Override
	public int hashCode() {
		return Objects.hash(subTypes, basetype);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof SignatureType))
			return false;
		SignatureType other = (SignatureType) obj;
		return Objects.equals(subTypes, other.subTypes) && Objects.equals(basetype, other.basetype);
	}

	@Override
	public String toString() {
		return String.format("SignatureData [type=%s, subTypes=%s]", basetype, subTypes);
	}

}

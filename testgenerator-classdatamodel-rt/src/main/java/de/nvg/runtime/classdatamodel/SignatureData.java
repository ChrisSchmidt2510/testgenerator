package de.nvg.runtime.classdatamodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SignatureData {
	private final Class<?> type;

	private final List<SignatureData> subTypes = new ArrayList<>();

	public SignatureData(Class<?> type) {
		this.type = type;
	}

	public void addSubType(SignatureData subType) {
		this.subTypes.add(subType);
	}

	public Class<?> getType() {
		return type;
	}

	public List<SignatureData> getSubTypes() {
		return Collections.unmodifiableList(subTypes);
	}

	@Override
	public int hashCode() {
		return Objects.hash(subTypes, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof SignatureData))
			return false;
		SignatureData other = (SignatureData) obj;
		return Objects.equals(subTypes, other.subTypes) && Objects.equals(type, other.type);
	}

}

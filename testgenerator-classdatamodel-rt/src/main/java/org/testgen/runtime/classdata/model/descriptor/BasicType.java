package org.testgen.runtime.classdata.model.descriptor;

import java.util.Objects;

public class BasicType implements DescriptorType {

	private final Class<?> type;

	public BasicType(Class<?> type) {
		this.type = type;
	}

	public Class<?> getType() {
		return type;
	}

	public static BasicType of(Class<?> type) {
		return new BasicType(type);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof BasicType))
			return false;
		BasicType other = (BasicType) obj;
		return Objects.equals(type, other.type);
	}

	@Override
	public String toString() {
		return type.getTypeName();
	}

}

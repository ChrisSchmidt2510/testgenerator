package de.nvg.runtime.classdatamodel;

import java.util.Objects;

public class SetterMethodData {
	private final String name;
	private final String descriptor;
	private final boolean isStatic;

	public SetterMethodData(String name, String descriptor, boolean isStatic) {
		this.name = name;
		this.descriptor = descriptor;
		this.isStatic = isStatic;
	}

	public String getName() {
		return name;
	}

	public String getDescriptor() {
		return descriptor;
	}

	public boolean isStatic() {
		return isStatic;
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
		if (!(obj instanceof SetterMethodData)) {
			return false;
		}
		SetterMethodData other = (SetterMethodData) obj;
		return Objects.equals(descriptor, other.descriptor) && Objects.equals(name, other.name);
	}

	@Override
	public String toString() {
		return "SetterMethodData [name=" + name + ", descriptor=" + descriptor + ", isStatic=" + isStatic + "]";
	}

}

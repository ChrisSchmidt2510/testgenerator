package org.testgen.agent.classdata.model;

import java.util.Objects;

public class MethodData {
	private final String name;
	private final String descriptor;
	private final MethodType methodType;
	private final boolean isStatic;

	public MethodData(String name, String descriptor, MethodType methodType, boolean isStatic) {
		this.name = name;
		this.descriptor = descriptor;
		this.methodType = methodType;
		this.isStatic = isStatic;
	}

	@Override
	public int hashCode() {
		return Objects.hash(descriptor, methodType, isStatic, name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof MethodData))
			return false;
		MethodData other = (MethodData) obj;
		return Objects.equals(descriptor, other.descriptor) && isStatic == other.isStatic
				&& Objects.equals(name, other.name) && methodType == other.methodType;
	}

	public String getName() {
		return name;
	}

	public String getDescriptor() {
		return descriptor;
	}

	public MethodType getMethodType() {
		return methodType;
	}

	public boolean isStatic() {
		return isStatic;
	}

	@Override
	public String toString() {
		return "MethodData [name=" + name + ", descriptor=" + descriptor + ", methodType=" + methodType + ", isStatic="
				+ isStatic + "]";
	}

}

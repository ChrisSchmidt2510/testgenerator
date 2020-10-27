package org.testgen.agent.classdata.model;

import java.util.Objects;

public class MethodData {
	private final String name;
	private final String descriptor;
	private final MethodType methodType;
	private final int argumentIndex;
	private final boolean isStatic;

	public MethodData(String name, String descriptor, MethodType methodType, int argumentIndex, boolean isStatic) {
		this.name = name;
		this.descriptor = descriptor;
		this.methodType = methodType;
		this.argumentIndex = argumentIndex;
		this.isStatic = isStatic;
	}

	@Override
	public int hashCode() {
		return Objects.hash(argumentIndex, descriptor, methodType, isStatic, name);
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
		return argumentIndex == other.argumentIndex && Objects.equals(descriptor, other.descriptor)
				&& isStatic == other.isStatic && Objects.equals(name, other.name) && methodType == other.methodType;
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

	public int getArgumentIndex() {
		return argumentIndex;
	}

	public boolean isStatic() {
		return isStatic;
	}

	@Override
	public String toString() {
		return "MethodData [name=" + name + ", descriptor=" + descriptor + ", methodType=" + methodType
				+ ", argumentIndex=" + argumentIndex + ", isStatic=" + isStatic + "]";
	}

}

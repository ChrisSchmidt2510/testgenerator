package de.nvg.runtime.classdatamodel;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class ClassData {
	private final String name;
	private final Supplier<ClassData> superClass;
	private final ConstructorData constructor;
	private final Map<FieldData, SetterMethodData> fields = new HashMap<>();

	public ClassData(String name, Supplier<ClassData> superClass, ConstructorData constructor) {
		this.name = name;
		this.superClass = superClass;
		this.constructor = constructor;
	}

	public ClassData(String name, ConstructorData constructor) {
		this.name = name;
		this.superClass = null;
		this.constructor = constructor;
	}

	public void addField(FieldData field, SetterMethodData setter) {
		fields.put(field, setter);
	}

	public String getName() {
		return name;
	}

	public ClassData getSuperclass() {
		return superClass != null ? superClass.get() : null;
	}

	public ConstructorData getConstructor() {
		return constructor;
	}

	public Map<FieldData, SetterMethodData> getFields() {
		return Collections.unmodifiableMap(fields);
	}

	public boolean hasDefaultConstructor() {
		return constructor.hasDefaultConstructor();
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClassData other = (ClassData) obj;
		return Objects.equals(name, other.name);
	}

}

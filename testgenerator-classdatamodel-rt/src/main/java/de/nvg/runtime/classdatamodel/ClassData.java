package de.nvg.runtime.classdatamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import de.nvg.testgenerator.MapBuilder;

public class ClassData {
	private final String name;
	private final Supplier<ClassData> superClass;
	private final ConstructorData constructor;
	private final List<FieldData> fields = new ArrayList<>();
	private final Map<FieldData, SetterMethodData> fieldSetterPairs = new HashMap<>();

	private static final Map<Class<?>, Class<?>> PRIMITIVES = MapBuilder.<Class<?>, Class<?>>hashMapBuilder()
			.add(Short.class, Short.TYPE).add(Byte.class, Byte.TYPE).add(Integer.class, Integer.TYPE)
			.add(Float.class, Float.TYPE).add(Double.class, Double.TYPE).add(Boolean.class, Boolean.TYPE)
			.add(Character.class, Character.TYPE).toUnmodifiableMap();

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

	public void addField(FieldData field) {
		fields.add(field);
	}

	public void addFieldSetterPair(FieldData field, SetterMethodData setter) {
		fieldSetterPairs.put(field, setter);
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

	public FieldData getFieldInHierarchie(String name, Class<?> descriptor) {
		FieldData field = new FieldData(name, descriptor.getName());

		if (fields.contains(field)) {
			return fields.get(fields.indexOf(field));
		} else if (PRIMITIVES.containsKey(descriptor)) {
			field = new FieldData(name, PRIMITIVES.get(descriptor).getName());

			if (fields.contains(field)) {
				return fields.get(fields.indexOf(field));
			}
		}

		if (superClass != null) {
			return superClass.get().getFieldInHierarchie(name, descriptor);
		}

		throw new IllegalArgumentException(
				"no field found in hierarchie for name " + name + " and descriptor " + descriptor.getName());
	}

	public FieldData getFieldInHierachie(FieldData field) {
		if (fields.contains(field)) {
			return fields.get(fields.indexOf(field));
		} else if (superClass != null) {
			return superClass.get().getFieldInHierachie(field);
		}

		throw new IllegalArgumentException("no field found in hierarchie for field " + field);
	}

	public SetterMethodData getSetterInHierarchie(FieldData field) {

		if (fieldSetterPairs.containsKey(field)) {
			return fieldSetterPairs.get(field);
		} else if (superClass != null) {
			return superClass.get().getSetterInHierarchie(field);
		}

		return null;
	}

	public SetterMethodData getSetterMethodData(String name, Class<?> descriptor) {
		FieldData field = getFieldInHierarchie(name, descriptor);

		return getSetterInHierarchie(field);
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
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ClassData other = (ClassData) obj;
		return Objects.equals(name, other.name);
	}

	@Override
	public String toString() {
		return "ClassData [name=" + name + ", superClass=" + superClass + ", constructor=" + constructor + ", fields="
				+ fieldSetterPairs + "]";
	}

}

package org.testgen.runtime.classdata.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.testgen.core.MapBuilder;

public class ClassData {
	private final String name;
	private final Supplier<ClassData> superClass;
	private final Supplier<ClassData> outerClass;
	private final ConstructorData constructor;
	private final List<FieldData> fields = new ArrayList<>();
	private final Map<FieldData, SetterMethodData> fieldSetterPairs = new HashMap<>();

	private static final Map<Class<?>, Class<?>> PRIMITIVES = MapBuilder.<Class<?>, Class<?>>hashMapBuilder()
			.add(Short.class, Short.TYPE).add(Byte.class, Byte.TYPE).add(Integer.class, Integer.TYPE)
			.add(Float.class, Float.TYPE).add(Double.class, Double.TYPE).add(Boolean.class, Boolean.TYPE)
			.add(Character.class, Character.TYPE).toUnmodifiableMap();

	private static final List<Class<?>> COLLECTIONS = Collections
			.unmodifiableList(Arrays.asList(Collection.class, List.class, Queue.class, Set.class, Map.class));

	public ClassData(String name, Supplier<ClassData> superClass, Supplier<ClassData> outerClass,
			ConstructorData constructor) {
		this.name = name;
		this.superClass = superClass;
		this.outerClass = outerClass;
		this.constructor = constructor;
	}

	public ClassData(String name, ConstructorData constructor) {
		this(name, null, null, constructor);
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

	public String getInnerClassName() {
		return name.substring(name.indexOf("$") + 1);
	}

	public ClassData getSuperclass() {
		return superClass != null ? superClass.get() : null;
	}

	public ClassData getOuterClass() {
		return outerClass != null ? outerClass.get() : null;
	}

	public boolean isInnerClass() {
		return outerClass != null;
	}

	public ConstructorData getConstructor() {
		return constructor;
	}

	public FieldData getFieldInHierarchie(String name, Class<?> descriptor) {
		FieldData field = new FieldData(name, descriptor);

		if (fields.contains(field)) {
			return fields.get(fields.indexOf(field));
		} else if (PRIMITIVES.containsKey(descriptor)) {
			field = new FieldData(name, PRIMITIVES.get(descriptor));

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

	public FieldData getCollectionFieldInHierarchie(String name) {
		List<FieldData> matchedFields = fields.stream()
				.filter(field -> field.getName().equals(name) && COLLECTIONS.contains(field.getDescriptor()))
				.collect(Collectors.toList());

		if (matchedFields.size() == 1) {
			return matchedFields.get(0);
		} else if (matchedFields.isEmpty() && superClass != null) {
			return superClass.get().getCollectionFieldInHierarchie(name);
		}

		throw new IllegalArgumentException("no collection-field found in hierarchie for name " + name);
	}

	public FieldData getFieldInHierarchie(FieldData field) {
		if (fields.contains(field)) {
			return fields.get(fields.indexOf(field));
		} else if (superClass != null) {
			return superClass.get().getFieldInHierarchie(field);
		}

		throw new IllegalArgumentException("no field found in hierarchie for field " + field);
	}

	public Class<?> getClassOfField(String name) {
		return fields.stream().filter(f -> f.getName().equals(name)).map(FieldData::getDescriptor).findAny()
				.orElse(null);
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

package org.testgen.compiler.classdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BasicClassData {

	private final String name;
	private final boolean isEnum;
	private BasicClassData superClass;
	private final List<InnerClassData> innerClasses = new ArrayList<>();

	private final List<FieldData> fields = new ArrayList<>();

	private ConstructorData constructor;

	private Map<MethodData, FieldData> methods = new HashMap<>();

	public BasicClassData(String name) {
		this(name, false);
	}

	public BasicClassData(String name, boolean isEnum) {
		this.name = name;
		this.isEnum = isEnum;
	}

	public String getName() {
		return name;
	}

	public BasicClassData getSuperClass() {
		return superClass;
	}

	public void setSuperClass(BasicClassData superClass) {
		this.superClass = superClass;
	}

	public void addInnerClass(InnerClassData innerClass) {
		innerClasses.add(innerClass);
	}

	public List<InnerClassData> getInnerClasses() {
		return Collections.unmodifiableList(innerClasses);
	}

	public boolean isEnum() {
		return isEnum;
	}

	public void addFields(List<FieldData> fields) {
		this.fields.addAll(fields);
	}

	public void addField(FieldData field) {
		this.fields.add(field);
	}

	public List<FieldData> getFields() {
		return Collections.unmodifiableList(fields);
	}

	public void setDefaultConstructor(boolean defaultConstructor) {
		constructor = new ConstructorData(defaultConstructor);
	}

	public boolean hasDefaultConstructor() {
		return constructor != null && constructor.isDefaultConstructor();
	}

	public void setConstructor(ConstructorData constructor) {
		this.constructor = constructor;
	}

	public ConstructorData getConstructor() {
		return constructor;
	}

	public void addMethod(MethodData method, FieldData field) {
		methods.put(method, field);
	}

	public Map<MethodData, FieldData> getMethods() {
		return Collections.unmodifiableMap(methods);
	}

	public abstract String getPackageName();

	public FieldData getField(String name, String dataType) {
		FieldData classField = fields.stream()
				.filter(field -> field.getName().equals(name) && field.getDataType().equals(dataType)).findAny()
				.orElse(null);

		if (classField != null) {
			return classField;
		} else if (superClass != null) {

			FieldData superClassField = superClass.getField(name, dataType);

			if (superClassField != null) {
				return superClassField;
			}
		}

		throw new IllegalArgumentException(
				"no Field in ClassHierachie found for name " + name + " and type " + dataType);
	}

	@Override
	public String toString() {
		return "Classname: " + name;
	}

}

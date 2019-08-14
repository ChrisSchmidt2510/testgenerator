package de.nvg.javaagent.classdata.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

public class ClassData {

	private final String name;
	private ClassData superClass;
	private boolean isEnum;

	private final List<FieldData> fields = new ArrayList<>();

	private boolean hasDefaultConstructor;
	private List<ConstructorData> constructors = new ArrayList<>();

	private Map<MethodData, FieldData> methods = new HashMap<>();
	private Map<FieldData, List<MethodData>> fieldsUsedInMethods = null;

	public ClassData(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public ClassData getSuperClass() {
		return superClass;
	}

	public void setSuperClass(ClassData superClass) {
		this.superClass = superClass;
	}

	public void setIsEnum(boolean isEnum) {
		this.isEnum = isEnum;
	}

	public boolean isEnum() {
		return isEnum;
	}

	public void addFields(List<FieldData> fields) {
		this.fields.addAll(fields);
	}

	public List<FieldData> getFields() {
		return Collections.unmodifiableList(fields);
	}

	public void setHasDefaultConstructor(boolean defaultConstructor) {
		this.hasDefaultConstructor = defaultConstructor;
	}

	public boolean hasDefaultConstructor() {
		return hasDefaultConstructor;
	}

	public void addConstructor(ConstructorData constructor) {
		this.constructors.add(constructor);
	}

	public List<ConstructorData> getConstructors() {
		return Collections.unmodifiableList(constructors);
	}

	public void addMethod(MethodData method, FieldData field) {
		methods.put(method, field);
	}

	public Map<MethodData, FieldData> getMethods() {
		return Collections.unmodifiableMap(methods);
	}

	public Map<FieldData, List<MethodData>> getFieldsUsedInMethods() {
		if (fieldsUsedInMethods == null) {

			fieldsUsedInMethods = new HashMap<>();
			for (Entry<MethodData, FieldData> entry : methods.entrySet()) {

				if (fieldsUsedInMethods.containsKey(entry.getValue())) {
					fieldsUsedInMethods.get(entry.getValue()).add(entry.getKey());
				} else {
					fieldsUsedInMethods.put(entry.getValue(), new ArrayList<>(Arrays.asList(entry.getKey())));
				}
			}
		}

		return fieldsUsedInMethods;
	}

	public Optional<FieldData> getField(String name, String dataType) {
		return fields.stream().filter(field -> field.getName().equals(name) && field.getDataType().equals(dataType))
				.findAny();
	}

}

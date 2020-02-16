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
	private String superClass;
	private boolean isEnum;

	private final List<FieldData> fields = new ArrayList<>();

	private ConstructorData constructor;

	private Map<MethodData, FieldData> methods = new HashMap<>();
	private Map<FieldData, List<MethodData>> fieldsUsedInMethods = null;

	public ClassData(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getSuperClass() {
		return superClass;
	}

	public void setSuperClass(String superClass) {
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

	public void setDefaultConstructor(boolean defaultConstructor) {
		constructor = new ConstructorData(defaultConstructor);
	}

	public boolean hasDefaultConstructor() {
		return constructor == null ? false : constructor.isDefaultConstructor();
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

	public String toString() {
		return "Classname: " + name;
	}

}

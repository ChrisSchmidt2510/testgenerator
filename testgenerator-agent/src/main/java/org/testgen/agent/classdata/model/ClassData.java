package org.testgen.agent.classdata.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.testgen.agent.classdata.constants.JavaTypes;

public class ClassData {

	private final String name;
	private String outerClassName;
	private ClassData superClass;
	private List<ClassData> innerClasses = new ArrayList<>();
	private Set<String> interfaces = new HashSet<>();

	private final List<FieldData> fields = new ArrayList<>();

	private ConstructorData constructor;

	private Map<MethodData, FieldData> methods = new HashMap<>();
	private Map<FieldData, List<MethodData>> fieldsUsedInMethods;

	public ClassData(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public ClassData getSuperClass() {
		return superClass;
	}

	public void setOuterClass(String outerClass) {
		this.outerClassName = outerClass;
	}

	public String getOuterClass() {
		return outerClassName;
	}

	public boolean isInnerClass() {
		return outerClassName != null;
	}

	public void setSuperClass(ClassData superClass) {
		this.superClass = superClass;
	}

	public void addInnerClass(ClassData classData) {
		this.innerClasses.add(classData);
	}

	public List<ClassData> getInnerClasses() {
		return Collections.unmodifiableList(innerClasses);
	}

	public void addInterface(String interfaceName) {
		this.interfaces.add(interfaceName);
	}

	public Set<String> getInterfaces() {
		return Collections.unmodifiableSet(interfaces);
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

	public Entry<MethodData, FieldData> getMethod(String name, String descriptor) {
		return methods.entrySet().stream().filter(
				entry -> entry.getKey().getName().equals(name) && entry.getKey().getDescriptor().equals(descriptor))
				.findAny().orElse(null);
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

	public FieldData getField(String name, String dataType) {
		FieldData classField = fields.stream()
				.filter(field -> field.getName().equals(name) && field.getDataType().equals(dataType)).findAny()
				.orElse(null);

		if (classField != null) {
			return classField;

		} else if (superClass != null) {

			FieldData superClassField = superClass.getField(name, dataType);

			if (superClassField != null)
				return superClassField;

		} else if (!innerClasses.isEmpty()) {

			for (ClassData innerClass : innerClasses) {
				FieldData innerClassField = innerClass.getField(name, dataType);

				if (innerClassField != null)
					return innerClassField;
			}
		}

		throw new IllegalArgumentException(
				"no Field in ClassHierachie found for name " + name + " and type " + dataType);
	}

	public boolean isSerializable() {
		boolean serializable = interfaces.stream().anyMatch(e -> JavaTypes.SERIALIZABLE.equals(e));

		if (!serializable && superClass != null)
			return superClass.isSerializable();

		return serializable;
	}

	@Override
	public String toString() {
		return "Classname: " + name;
	}

}

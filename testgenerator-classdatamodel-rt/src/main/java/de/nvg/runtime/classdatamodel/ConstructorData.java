package de.nvg.runtime.classdatamodel;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConstructorData {
	private final boolean defaultConstructor;
	private final Map<Integer, FieldData> constructorFields = new LinkedHashMap<>();

	public ConstructorData(boolean defaultConstructor) {
		this.defaultConstructor = defaultConstructor;
	}

	public void addElement(int index, FieldData field) {
		this.constructorFields.put(index, field);
	}

	public boolean hasDefaultConstructor() {
		return defaultConstructor;
	}

	public boolean isNotEmpty() {
		return !constructorFields.isEmpty();
	}

	public Map<Integer, FieldData> getConstructorFields() {
		return Collections.unmodifiableMap(constructorFields);
	}

	@Override
	public String toString() {
		return "ConstructorData [defaultConstructor=" + defaultConstructor + ", constructorFieldIndex="
				+ constructorFields + "]";
	}

}

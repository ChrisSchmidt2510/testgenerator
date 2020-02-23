package de.nvg.runtime.classdatamodel;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConstructorData {
	private final boolean defaultConstructor;
	private final Map<Integer, FieldData> constructorFieldIndex = new LinkedHashMap<>();

	public ConstructorData(boolean defaultConstructor) {
		this.defaultConstructor = defaultConstructor;
	}

	public void addElement(int index, FieldData field) {
		this.constructorFieldIndex.put(index, field);
	}

	public boolean hasDefaultConstructor() {
		return defaultConstructor;
	}

	public boolean isNotEmpty() {
		return !constructorFieldIndex.isEmpty();
	}

	public Map<Integer, FieldData> getConstructorFieldIndex() {
		return Collections.unmodifiableMap(constructorFieldIndex);
	}

	@Override
	public String toString() {
		return "ConstructorData [defaultConstructor=" + defaultConstructor + ", constructorFieldIndex="
				+ constructorFieldIndex + "]";
	}

}

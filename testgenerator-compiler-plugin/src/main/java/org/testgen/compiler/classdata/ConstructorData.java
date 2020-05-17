package org.testgen.compiler.classdata;

import java.util.HashMap;
import java.util.Map;

public class ConstructorData {
	private boolean defaultConstructor;
	private Map<Integer, FieldData> constructorElements = new HashMap<>();

	public ConstructorData(boolean defaultConstructor) {
		this.defaultConstructor = defaultConstructor;
	}

	public ConstructorData(Map<Integer, FieldData> constructorFields) {
		this.constructorElements = constructorFields;
		this.defaultConstructor = false;
	}

	public boolean isDefaultConstructor() {
		return defaultConstructor;
	}

	public void addConstructorElement(Integer argumentIndex, FieldData fieldData) {
		constructorElements.put(argumentIndex, fieldData);
	}

	public Map<Integer, FieldData> getConstructorElements() {
		return constructorElements;
	}
}

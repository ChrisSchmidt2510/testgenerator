package org.testgen.testgenerator.ui.plugin.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Model {

	private String className;
	private List<String> methods = new ArrayList<>();

	private int selectedMethodIndex;

	private Map<String, String> blPackages = new HashMap<>();

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void setMethods(List<String> methods) {
		this.methods = methods;
	}

	public List<String> getMethods() {
		return methods;
	}

	public void setSelectedMethodIndex(int index) {
		this.selectedMethodIndex = index;
	}

	public int getSelectedMethodIndex() {
		return selectedMethodIndex;
	}

	public void setBlPackages(Map<String, String> blPackages) {
		this.blPackages = blPackages;
	}

	public Map<String, String> getBlPackages() {
		return blPackages;
	}

}

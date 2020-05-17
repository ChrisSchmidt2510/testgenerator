package org.testgen.compiler.classdata;

public class ClassData extends BasicClassData {
	private final String packageName;

	public ClassData(String packageName, String name) {
		super(name);
		this.packageName = packageName;
	}

	public ClassData(String packageName, String name, boolean isEnum) {
		super(name, isEnum);
		this.packageName = packageName;
	}

	@Override
	public String getPackageName() {
		return packageName;
	}

}

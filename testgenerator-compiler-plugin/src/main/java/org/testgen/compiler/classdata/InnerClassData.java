package org.testgen.compiler.classdata;

public class InnerClassData extends BasicClassData {
	private final BasicClassData parentClass;

	public InnerClassData(BasicClassData parentClass, String name) {
		super(name);
		this.parentClass = parentClass;
	}

	public InnerClassData(BasicClassData outerClass, String name, boolean isEnum) {
		super(name, isEnum);
		this.parentClass = outerClass;
	}

	public BasicClassData getParentClass() {
		return parentClass;
	}

	@Override
	public String getPackageName() {
		return parentClass.getPackageName() + "." + parentClass.getName();
	}

}

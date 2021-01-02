package org.testgen.runtime.valuetracker.blueprint;

public abstract class AbstractBasicBluePrint<E> implements BluePrint {
	protected final String name;
	protected final E value;
	protected boolean build;

	protected AbstractBasicBluePrint(String name, E value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public Object getReference() {
		return value;
	}

	@Override
	public Class<?> getReferenceClass() {
		if (value == null)
			return null;

		return value.getClass();
	}

	@Override
	public String getSimpleClassName() {
		return value.getClass().getSimpleName();
	}

	@Override
	public String getClassNameOfReference() {
		return value.getClass().getName();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setBuild() {
		build = true;
	}

	@Override
	public boolean isNotBuild() {
		return !build;
	}

}

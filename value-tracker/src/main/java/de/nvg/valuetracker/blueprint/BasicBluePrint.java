package de.nvg.valuetracker.blueprint;

public abstract class BasicBluePrint<E> implements BluePrint {
	protected final String name;
	protected final E value;
	protected boolean build;

	protected BasicBluePrint(String name, E value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public Object getReference() {
		return value;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isBuild() {
		return build;
	}

}

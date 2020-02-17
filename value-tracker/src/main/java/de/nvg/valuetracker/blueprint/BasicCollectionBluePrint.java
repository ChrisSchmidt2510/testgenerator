package de.nvg.valuetracker.blueprint;

public abstract class BasicCollectionBluePrint<E> extends BasicBluePrint<E> {
	private final String interfaceClassName;

	public BasicCollectionBluePrint(String name, E value, Class<?> interfaceClass) {
		super(name, value);
		this.interfaceClassName = interfaceClass.getName();
	}

	public String getImplementationClass() {
		return value.getClass().getName();
	}

	public String getInterfaceClass() {
		return interfaceClassName;
	}

	@Override
	public boolean isComplexType() {
		return true;
	}

}

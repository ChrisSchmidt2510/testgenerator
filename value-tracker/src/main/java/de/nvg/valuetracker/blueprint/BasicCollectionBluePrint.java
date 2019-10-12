package de.nvg.valuetracker.blueprint;

public abstract class BasicCollectionBluePrint<E> extends BasicBluePrint<E> {
	private final String interfaceClassName;
	private final String factoryMethod;

	public BasicCollectionBluePrint(String name, E value, String interfaceClassName, String factoryMethod) {
		super(name, value);
		this.interfaceClassName = interfaceClassName;
		this.factoryMethod = factoryMethod;
	}

	public String getImplementationClass() {
		return value.getClass().getName();
	}

	public String getInterfaceClass() {
		return interfaceClassName;
	}

	public String getFactoryMethod() {
		return factoryMethod;
	}

	@Override
	public boolean isComplexType() {
		return true;
	}

}

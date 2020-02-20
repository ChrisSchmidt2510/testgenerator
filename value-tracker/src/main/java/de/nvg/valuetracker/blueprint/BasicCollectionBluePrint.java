package de.nvg.valuetracker.blueprint;

public abstract class BasicCollectionBluePrint<E> extends BasicBluePrint<E> {
	protected final Class<?> interfaceClass;

	public BasicCollectionBluePrint(String name, E value, Class<?> interfaceClass) {
		super(name, value);
		this.interfaceClass = interfaceClass;
	}

	public Class<?> getImplementationClass() {
		return value.getClass();
	}

	public Class<?> getInterfaceClass() {
		return interfaceClass;
	}

	@Override
	public boolean isComplexType() {
		return true;
	}

	@Override
	public String toString() {
		return interfaceClass.getName() + " " + name;
	}

}

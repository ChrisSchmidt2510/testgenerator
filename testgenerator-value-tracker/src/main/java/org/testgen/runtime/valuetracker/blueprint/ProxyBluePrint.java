package org.testgen.runtime.valuetracker.blueprint;

import java.util.List;

public class ProxyBluePrint extends AbstractBasicBluePrint<Object> {
	private final Class<?> interfaceClass;

	public ProxyBluePrint(String name, Class<?> interfaceClass) {
		super(name, null);
		this.interfaceClass = interfaceClass;
	}

	@Override
	public List<BluePrint> getPreExecuteBluePrints() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isComplexType() {
		return false;
	}

	@Override
	public void resetBuildState() {
		this.build = false;
	}

	@Override
	public Object getReference() {
		return interfaceClass;
	}

	public Class<?> getInterfaceClass() {
		return interfaceClass;
	}

}

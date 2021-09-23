package org.testgen.runtime.valuetracker.blueprint.complextypes;

import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import org.testgen.runtime.valuetracker.CurrentlyBuildedBluePrints;
import org.testgen.runtime.valuetracker.blueprint.BasicBluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.factories.BluePrintFactory;

public class ProxyBluePrint extends BasicBluePrint<Object> {
	private final Class<?> interfaceClass;

	ProxyBluePrint(String name, Class<?> interfaceClass) {
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

	public static class ProxyBluePrintFactory implements BluePrintFactory {

		@Override
		public boolean createBluePrintForType(Object value) {
			return value != null && Proxy.isProxyClass(value.getClass());
		}

		@Override
		public BluePrint createBluePrint(String name, Object value,
				CurrentlyBuildedBluePrints currentlyBuildedBluePrints,
				BiFunction<String, Object, BluePrint> childCallBack) {
			Class<?> interfaceClass = Stream.of(value.getClass().getInterfaces())
					.filter(cl -> !Serializable.class.isAssignableFrom(cl)).findAny()
					.orElseThrow(() -> new IllegalArgumentException("proxies have to implement at least 1 interface"));

			return new ProxyBluePrint(name, interfaceClass);
		}

	}

}

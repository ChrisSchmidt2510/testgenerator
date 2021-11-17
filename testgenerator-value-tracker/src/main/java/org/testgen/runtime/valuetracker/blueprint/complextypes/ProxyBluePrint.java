package org.testgen.runtime.valuetracker.blueprint.complextypes;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import org.testgen.logging.LogManager;
import org.testgen.logging.Logger;
import org.testgen.runtime.valuetracker.CurrentlyBuildedBluePrints;
import org.testgen.runtime.valuetracker.blueprint.BasicBluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.factories.BluePrintFactory;

public class ProxyBluePrint extends BasicBluePrint<Object> {
	private final Class<?>[] interfaceClasses;

	private final CurrentlyBuildedBluePrints currentlyBuildedBluePrints;
	private final BiFunction<String, Object, BluePrint> callBackHandler;
	private final List<Entry<Method, BluePrint>> proxyResults = new ArrayList<>();

	private InvocationHandler invocationHandler;

	ProxyBluePrint(String name, Object proxy, CurrentlyBuildedBluePrints currentlyBuildedBluePrints,
			BiFunction<String, Object, BluePrint> callBackHandler) {
		super(name, proxy);
		this.interfaceClasses = proxy.getClass().getInterfaces();
		this.currentlyBuildedBluePrints = currentlyBuildedBluePrints;
		this.callBackHandler = callBackHandler;
	}

	public void addProxyResult(Method method, Object value) {
		if (currentlyBuildedBluePrints.isCurrentlyBuilded(value))
			currentlyBuildedBluePrints.addFinishedListener(value, bp ->proxyResults.add(new SimpleEntry<>(method, bp)));
		else
			proxyResults.add(new SimpleEntry<>(method, callBackHandler.apply(method.getName(), value)));
	}

	@Override
	public List<BluePrint> getPreExecuteBluePrints() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isComplexType() {
		return true;
	}

	@Override
	public void resetBuildState() {
		this.proxyResults.stream().map(Entry::getValue).forEach(BluePrint::resetBuildState);

		this.build = false;
	}

	public Class<?>[] getInterfaceClasses() {
		return interfaceClasses;
	}

	public Class<?> getType() {
		Optional<Class<?>> optType = Stream.of(interfaceClasses).filter(cls -> cls.getMethods().length > 0 && filterMethods(cls.getMethods()))
				.findFirst();
		
		if(optType.isPresent())
			return optType.get();
		
		return interfaceClasses[0];
	}
	
	public List<Entry<Method, BluePrint>> getProxyResults(){
		return Collections.unmodifiableList(proxyResults);
	}

	private boolean filterMethods(Method[] methods) {
		return Stream.of(methods).anyMatch(m -> !m.isDefault() && !Modifier.isStatic(m.getModifiers()));
	}

	public InvocationHandler getInvocationHandler() {
		return invocationHandler;
	}
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(interfaceClasses);
		result = prime * result + Objects.hash(name, invocationHandler, proxyResults);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof ProxyBluePrint))
			return false;
		ProxyBluePrint other = (ProxyBluePrint) obj;
		return Arrays.equals(interfaceClasses, other.interfaceClasses)
				&& Objects.equals(name, other.name)
				&& Objects.equals(invocationHandler, other.invocationHandler)
				&& Objects.equals(proxyResults, other.proxyResults);
	}

	@Override
	public String toString() {
		return "ProxyBluePrint [ name=" + name + ", interfaceClasses=" + Arrays.toString(interfaceClasses) + ", proxyResults="
				+ proxyResults + ", invocationHandler=" + invocationHandler + "]";
	}



	public static class ProxyBluePrintFactory implements BluePrintFactory {

		private static final Logger LOGGER = LogManager.getLogger(ProxyBluePrintFactory.class);

		@Override
		public boolean createBluePrintForType(Object value) {
			return value != null && Proxy.isProxyClass(value.getClass());
		}

		@Override
		public BluePrint createBluePrint(String name, Object value,
				CurrentlyBuildedBluePrints currentlyBuildedBluePrints,
				BiFunction<String, Object, BluePrint> childCallBack) {

			ProxyBluePrint proxyBluePrint = new ProxyBluePrint(name, value, currentlyBuildedBluePrints, childCallBack);

			try {
				proxyBluePrint.invocationHandler = Proxy.getInvocationHandler(value);
			} catch (RuntimeException e) {
				LOGGER.error("error getting invocationHandler from Proxy", e);
			}

			return proxyBluePrint;
		}

	}

}

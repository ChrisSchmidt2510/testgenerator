package org.testgen.runtime.valuetracker.blueprint.complextypes;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

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
	private final List<BluePrint> proxyResults = new ArrayList<>();

	private InvocationHandler invocationHandler;

	ProxyBluePrint(String name, Object proxy, CurrentlyBuildedBluePrints currentlyBuildedBluePrints,
			BiFunction<String, Object, BluePrint> callBackHandler) {
		super(name, proxy);
		this.interfaceClasses = proxy.getClass().getInterfaces();
		this.currentlyBuildedBluePrints = currentlyBuildedBluePrints;
		this.callBackHandler = callBackHandler;
	}

	public void addProxyResult(String name, Object value) {
		if (currentlyBuildedBluePrints.isCurrentlyBuilded(value))
			currentlyBuildedBluePrints.addFinishedListener(value, proxyResults::add);
		else
			proxyResults.add(callBackHandler.apply(name, value));
	}

	@Override
	public List<BluePrint> getPreExecuteBluePrints() {
		return proxyResults;
	}

	@Override
	public boolean isComplexType() {
		return true;
	}

	@Override
	public void resetBuildState() {
		this.proxyResults.forEach(BluePrint::resetBuildState);
		
		this.build = false;
	}

	public Class<?>[] getInterfaceClasses() {
		return interfaceClasses;
	}

	public InvocationHandler getInvocationHandler() {
		return invocationHandler;
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

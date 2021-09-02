package org.testgen.runtime.valuetracker;

import java.lang.reflect.Proxy;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.testgen.config.TestgeneratorConfig;
import org.testgen.core.MapBuilder;
import org.testgen.logging.LogManager;
import org.testgen.logging.Logger;
import org.testgen.runtime.proxy.AbstractProxy;
import org.testgen.runtime.proxy.impl.BooleanProxy;
import org.testgen.runtime.proxy.impl.DoubleProxy;
import org.testgen.runtime.proxy.impl.FloatProxy;
import org.testgen.runtime.proxy.impl.IntegerProxy;
import org.testgen.runtime.proxy.impl.LongProxy;
import org.testgen.runtime.proxy.impl.ReferenceProxy;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.BluePrintsFactory;
import org.testgen.runtime.valuetracker.blueprint.ProxyBluePrint;
import org.testgen.runtime.valuetracker.blueprint.Type;
import org.testgen.runtime.valuetracker.storage.ValueStorage;

public final class ObjectValueTracker {

	private static final Map<Class<?>, Function<IntegerProxy, Object>> INTEGER_PROXY_METHODS = //
			MapBuilder.<Class<?>, Function<IntegerProxy, Object>>hashMapBuilder()
					.add(int.class, IntegerProxy::getUntrackedValue)
					.add(byte.class, IntegerProxy::getUntrackedByteValue)
					.add(char.class, IntegerProxy::getUntrackedCharValue)
					.add(short.class, IntegerProxy::getUntrackedShortValue).toUnmodifiableMap();

	private static final Logger LOGGER = LogManager.getLogger(ObjectValueTracker.class);

	private static final ObjectValueTracker INSTANCE = new ObjectValueTracker();

	private final BluePrintsFactory bluePrintsFactory = new BluePrintsFactory();

	private final Map<Class<?>, List<BluePrint>> bluePrintsPerClass = new HashMap<>();

	private final Deque<Object> currentlyBuiltBluePrints = new ArrayDeque<>();

	private final BluePrintUnderProcessRegistration registration = new BluePrintUnderProcessRegistration();

	private ObjectValueTracker() {
	}

	public static ObjectValueTracker getInstance() {
		return INSTANCE;
	}

	public void track(Object value, String name, Type type) {
		if (value != null && !Proxy.isProxyClass(value.getClass())) {
			ValueStorage.getInstance().addBluePrint(trackValues(value, name), type);
		}
	}

	public void trackProxyValues(Object value, String name, Class<?> interfaceClass, String proxyName) {
		if (value != null) {
			Optional<BluePrintFactory> factoryOpt = bluePrintsFactory.getBluePrintFactory(value);
			
			if(!factoryOpt.isPresent())
				throw new IllegalArgumentException(value +" is no proxy");
			
			BluePrintFactory factory = factoryOpt.get();
			ProxyBluePrint proxy = (ProxyBluePrint) getBluePrintForReference(interfaceClass, //
					() -> factory.createBluePrint(proxyName, value, null, registration, null));

			ValueStorage.getInstance().addProxyBluePrint(proxy, trackValues(value, name));
		}
	}

	public void enableFieldTracking() {
		TestgeneratorConfig.setFieldTracking(true);
	}

	public void enableProxyTracking() {
		TestgeneratorConfig.setProxyTracking(true);
	}

	BluePrint trackValues(Object value, String name) {
		LOGGER.info("Start Tracking Values for Object: " + name + " " + value);
		Object proxyValue = getProxyValue(value);

		currentlyBuiltBluePrints.push(proxyValue);

		BluePrint bluePrint = null;

		Optional<BluePrintFactory> factoryOptional = bluePrintsFactory.getBluePrintFactory(proxyValue);

		if (factoryOptional.isPresent()) {
			BluePrintFactory factory = factoryOptional.get();

			BiFunction<String, Object, BluePrint> childCallBack = (newName, newValue) -> trackValues(newValue, newName);

			Predicate<Object> currentlyBuildedFilter = o -> currentlyBuiltBluePrints.contains(o);

			if (factory.createsSimpleBluePrint())
				bluePrint = factory.createBluePrint(name, proxyValue, currentlyBuildedFilter, registration,
						childCallBack);
			else
				bluePrint = getBluePrintForReference(proxyValue, () -> factory.createBluePrint(name, proxyValue,
						currentlyBuildedFilter, registration, childCallBack));
		}

		registration.executeActions(proxyValue, bluePrint);

		currentlyBuiltBluePrints.pop();

		return bluePrint;
	}

	private BluePrint getBluePrintForReference(Object reference, Supplier<BluePrint> bluePrintCreator) {
		BluePrint bluePrint = getBluePrintForReference(reference);

		if (bluePrint != null) {
			return bluePrint;

		} else {
			BluePrint createdBluePrint = bluePrintCreator.get();
			addBluePrintPerClass(reference.getClass(), createdBluePrint);

			return createdBluePrint;
		}
	}

	private void addBluePrintPerClass(Class<?> clazz, BluePrint bluePrint) {
		if (bluePrintsPerClass.containsKey(clazz)) {
			bluePrintsPerClass.get(clazz).add(bluePrint);

		} else {
			bluePrintsPerClass.put(clazz, new ArrayList<>(Arrays.asList(bluePrint)));
		}
	}

	private BluePrint getBluePrintForReference(Object reference) {
		List<BluePrint> bluePrintsForClass = bluePrintsPerClass.get(reference.getClass());

		if (bluePrintsForClass != null) {

			for (BluePrint bluePrint : bluePrintsForClass) {
				if (bluePrint.getReference() != null && bluePrint.getReference() == reference) {
					return bluePrint;
				}
			}
		}

		return null;
	}

	public static Object getProxyValue(Object value) {
		if (AbstractProxy.isProxy(value)) {
			
			if (value instanceof ReferenceProxy<?>) {
				return ((ReferenceProxy<?>) value).getUntrackedValue();

			} else if (value instanceof DoubleProxy) {
				return ((DoubleProxy) value).getUntrackedValue();

			} else if (value instanceof FloatProxy) {
				return ((FloatProxy) value).getUntrackedValue();

			} else if (value instanceof IntegerProxy) {
				IntegerProxy proxy = ((IntegerProxy) value);
				return INTEGER_PROXY_METHODS.get(proxy.getDataType()).apply(proxy);

			} else if (value instanceof LongProxy) {
				return ((LongProxy) value).getUntrackedValue();

			} else if (value instanceof BooleanProxy) {
				return ((BooleanProxy) value).getUntrackedValue();
			}
		}

		return value;
	}

}

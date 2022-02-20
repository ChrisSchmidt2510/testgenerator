package org.testgen.runtime.valuetracker;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.testgen.core.CurrentlyBuiltQueue;
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
import org.testgen.runtime.valuetracker.blueprint.Type;
import org.testgen.runtime.valuetracker.blueprint.complextypes.ProxyBluePrint;
import org.testgen.runtime.valuetracker.blueprint.factories.BluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.factories.BluePrintsFactory;
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

	private final CurrentlyBuiltQueue<BluePrint> currentlyBuiltQueue = new CurrentlyBuiltQueue<>();

	private ObjectValueTracker() {
	}

	public static ObjectValueTracker getInstance() {
		return INSTANCE;
	}

	public void track(Object value, String name, Type type) {
		ValueStorage.getInstance().addBluePrint(trackNormalValue(value, name), type);
	}

	public ProxyBluePrint trackProxy(Object proxy, String name) {
		ProxyBluePrint proxyBluePrint = trackProxyValue(proxy, name);

		ValueStorage.getInstance().addProxyBluePrint(proxyBluePrint);

		return proxyBluePrint;
	}

	BluePrint trackNormalValue(Object value, String name) {
		if (Proxy.isProxyClass(value.getClass()))
			throw new IllegalArgumentException("cant track Values for Proxies use trackProxyValue");

		LOGGER.info("Start Tracking Values for Object: " + name + " " + value);

		currentlyBuiltQueue.register(value);

		BluePrint bluePrint = null;

		Optional<BluePrintFactory> factoryOptional = bluePrintsFactory.getBluePrintFactory(value);

		if (factoryOptional.isPresent()) {
			BluePrintFactory factory = factoryOptional.get();

			BiFunction<String, Object, BluePrint> childCallBack = (newName, newValue) -> trackValue(newValue, newName);

			if (factory.createsSimpleBluePrint())
				bluePrint = factory.createBluePrint(name, value, currentlyBuiltQueue, childCallBack);
			else
				bluePrint = getBluePrintForReference(value,
						() -> factory.createBluePrint(name, value, currentlyBuiltQueue, childCallBack));

		} else
			throw new TrackingException(String.format("no factory available for object %s", value));

		currentlyBuiltQueue.executeResultListener(value, bluePrint);

		return bluePrint;
	}

	ProxyBluePrint trackProxyValue(Object proxy, String name) {
		if (!Proxy.isProxyClass(proxy.getClass()))
			throw new IllegalArgumentException(proxy + "is no proxy");

		Optional<BluePrintFactory> factoryOpt = bluePrintsFactory.getBluePrintFactory(proxy);

		if (!factoryOpt.isPresent())
			throw new TrackingException("cant create a BluePrint for proxy " + proxy);

		BluePrintFactory factory = factoryOpt.get();

		ProxyBluePrint proxyBluePrint = (ProxyBluePrint) getBluePrintForReference(proxy,
				() -> factory.createBluePrint(name, proxy, currentlyBuiltQueue,
						(childName, childValue) -> trackValue(childValue, childName)));

		return proxyBluePrint;
	}

	private BluePrint trackValue(Object value, String name) {
		Object unwrappedValue = getTestgeneratorProxyValue(value);

		if (Proxy.isProxyClass(unwrappedValue.getClass()))
			return trackProxyValue(unwrappedValue, name);

		return trackNormalValue(unwrappedValue, name);
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

	public static Object getTestgeneratorProxyValue(Object value) {
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

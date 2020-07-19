package de.nvg.valuetracker;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.testgen.core.MapBuilder;
import org.testgen.core.TestgeneratorConstants;
import org.testgen.core.logging.LogManager;
import org.testgen.core.logging.Logger;
import org.testgen.core.properties.RuntimeProperties;

import de.nvg.proxy.impl.BooleanProxy;
import de.nvg.proxy.impl.DoubleProxy;
import de.nvg.proxy.impl.FloatProxy;
import de.nvg.proxy.impl.IntegerProxy;
import de.nvg.proxy.impl.LongProxy;
import de.nvg.proxy.impl.ReferenceProxy;
import de.nvg.valuetracker.blueprint.ArrayBluePrint;
import de.nvg.valuetracker.blueprint.BluePrint;
import de.nvg.valuetracker.blueprint.ComplexBluePrint;
import de.nvg.valuetracker.blueprint.ProxyBluePrint;
import de.nvg.valuetracker.blueprint.Type;
import de.nvg.valuetracker.blueprint.collections.CollectionBluePrint;
import de.nvg.valuetracker.blueprint.collections.MapBluePrint;
import de.nvg.valuetracker.blueprint.simpletypes.SimpleBluePrintFactory;
import de.nvg.valuetracker.storage.ValueStorage;

public final class ObjectValueTracker {

	private static final List<Class<?>> DIRECT_OUTPUT_CLASSES = Collections.unmodifiableList(Arrays.asList(
			Integer.class, Byte.class, Short.class, Character.class, Float.class, Double.class, Long.class,
			Boolean.class, Integer.TYPE, Byte.TYPE, Short.TYPE, Character.TYPE, Float.TYPE, Double.TYPE, Long.TYPE,
			Long.TYPE, Boolean.TYPE, LocalDate.class, LocalTime.class, LocalDateTime.class, java.util.Date.class,
			Date.class, Calendar.class, GregorianCalendar.class, BigDecimal.class, String.class, Class.class));

	private static final Map<Class<?>, Function<IntegerProxy, Object>> INTEGER_PROXY_METHODS = //
			MapBuilder.<Class<?>, Function<IntegerProxy, Object>>hashMapBuilder()
					.add(int.class, IntegerProxy::getUntrackedValue)
					.add(byte.class, IntegerProxy::getUntrackedByteValue)
					.add(char.class, IntegerProxy::getUntrackedCharValue)
					.add(short.class, IntegerProxy::getUntrackedShortValue).toUnmodifiableMap();

	private static final String OUTER_CLASS_FIELD_NAME = "this$0";

	private static final Logger LOGGER = LogManager.getLogger(ObjectValueTracker.class);

	private static final ObjectValueTracker INSTANCE = new ObjectValueTracker();

	private final Map<Class<?>, List<BluePrint>> bluePrintsPerClass = new HashMap<>();

	private final Deque<Object> currentlyBuildedBluePrints = new ArrayDeque<>();
	private final Map<Object, List<Consumer<BluePrint>>> addValueAfterCreation = new HashMap<>();

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
			ProxyBluePrint proxy = (ProxyBluePrint) getBluePrintForReference(interfaceClass, //
					() -> new ProxyBluePrint(proxyName, interfaceClass));

			ValueStorage.getInstance().addProxyBluePrint(proxy, trackValues(value, name));
		}
	}

	public void enableFieldTracking() {
		RuntimeProperties.getInstance().setFieldTracking(true);
	}

	public void enableProxyTracking() {
		RuntimeProperties.getInstance().setProxyTracking(true);
	}

	BluePrint trackValues(Object value, String name) {
		LOGGER.info("Start Tracking Values for Object: " + name + " " + value);
		Object proxyValue = getProxyValue(value);

		currentlyBuildedBluePrints.push(proxyValue);

		BluePrint bluePrint = null;

		if (DIRECT_OUTPUT_CLASSES.contains(proxyValue.getClass()) || proxyValue.getClass().isEnum())
			bluePrint = SimpleBluePrintFactory.of(name, proxyValue);
		else if (isCollection(proxyValue))
			bluePrint = getBluePrintForReference(proxyValue, () -> trackValuesFromCollections(proxyValue, name));
		else if (proxyValue.getClass().isArray())
			bluePrint = getBluePrintForReference(proxyValue, () -> trackValuesArray(proxyValue, name));
		else if (Proxy.isProxyClass(proxyValue.getClass()))
			bluePrint = getBluePrintForReference(proxyValue, () -> new ProxyBluePrint(name, proxyValue.getClass()));
		else
			bluePrint = getBluePrintForReference(proxyValue, () -> trackValuesFromComplexTypes(proxyValue, name));

		List<Consumer<BluePrint>> valueAfterCreation = addValueAfterCreation.get(proxyValue);

		if (valueAfterCreation != null) {
			for (Consumer<BluePrint> consumer : valueAfterCreation) {
				consumer.accept(bluePrint);
			}
		}

		addValueAfterCreation.remove(proxyValue);
		currentlyBuildedBluePrints.pop();

		return bluePrint;
	}

	private BluePrint trackValuesFromComplexTypes(Object value, String name) {
		ComplexBluePrint complexBluePrint = new ComplexBluePrint(name, value);

		trackValuesFieldsFromClass(value.getClass().getDeclaredFields(), value, complexBluePrint);
		trackValuesFieldsFromClass(value.getClass().getSuperclass().getDeclaredFields(), value, complexBluePrint);

		return complexBluePrint;
	}

	private void trackValuesFieldsFromClass(Field[] fields, Object value, ComplexBluePrint complexBluePrint) {
		for (Field field : fields) {

			try {
				field.setAccessible(true);

				if (!isConstant(field)) {

					Object fieldValue = getProxyValue(field.get(value));

					if (fieldValue != null && !TestgeneratorConstants.isTestgeneratorField(field.getName())
							&& !Proxy.isProxyClass(fieldValue.getClass()) && fieldValue != this) {

						LOGGER.debug("Tracking Value for Field: " + field.getName() + " with Value: " + fieldValue);

						if (currentlyBuildedBluePrints.contains(fieldValue)) {
							Consumer<BluePrint> consumer = bp -> complexBluePrint.addBluePrint(bp);

							if (addValueAfterCreation.containsKey(fieldValue)) {
								addValueAfterCreation.get(fieldValue).add(consumer);
							} else {
								addValueAfterCreation.put(fieldValue, new ArrayList<>(Arrays.asList(consumer)));
							}

						} else {
							String name = value.getClass().isMemberClass()
									&& OUTER_CLASS_FIELD_NAME.equals(field.getName()) ? "outerClass" : field.getName();

							BluePrint elementBluePrint = trackValues(fieldValue, name);
							complexBluePrint.addBluePrint(elementBluePrint);
						}

					}
				}
			} catch (Throwable e) {
				LOGGER.error(e);
				throw new TrackingException("Fehler bei der Erstellung des BluePrints", e);
			}
		}
	}

	private BluePrint trackValuesFromCollections(Object object, String name) {

		CollectionBluePrint bluePrint = null;

		if (object instanceof List<?>) {
			bluePrint = new CollectionBluePrint(name, (Collection<?>) object, List.class);

		} else if (object instanceof Set<?>) {
			bluePrint = new CollectionBluePrint(name, (Collection<?>) object, Set.class);

		} else if (object instanceof Queue<?>) {
			bluePrint = new CollectionBluePrint(name, (Collection<?>) object, Queue.class);
		} else if (object instanceof Collection<?>) {
			bluePrint = new CollectionBluePrint(name, (Collection<?>) object, Collection.class);
		}

		if (bluePrint != null) {
			trackValuesCollection((Collection<?>) object, name, bluePrint);
			return bluePrint;
		}

		if (object instanceof Map<?, ?>) {
			return trackValuesMap((Map<?, ?>) object, name);
		}

		throw new IllegalArgumentException("Unvalid Type for the parameter object");
	}

	private void trackValuesCollection(Collection<?> collection, String name, CollectionBluePrint bluePrint) {
		for (Object value : collection) {

			BluePrint elementBluePrint = trackValues(value, name + "Element");
			bluePrint.addBluePrint(elementBluePrint);
		}
	}

	private MapBluePrint trackValuesMap(Map<?, ?> map, String name) {
		MapBluePrint mapBluePrint = new MapBluePrint(name, map);

		if (!map.isEmpty()) {

			for (Entry<?, ?> entry : map.entrySet()) {
				BluePrint keyBluePrint = trackValues(entry.getKey(), name + "Key");
				BluePrint valueBluePrint = trackValues(entry.getValue(), name + "Value");

				mapBluePrint.addKeyValuePair(keyBluePrint, valueBluePrint);
			}

		}
		return mapBluePrint;
	}

	private ArrayBluePrint trackValuesArray(Object array, String name) {
		int length = Array.getLength(array);

		ArrayBluePrint arrayBluePrint = new ArrayBluePrint(name, array, length);

		for (int i = 0; i < length; i++) {
			Object element = Array.get(array, i);
			if (element != null) {
				BluePrint bluePrint = trackValues(element, name + "Element");
				arrayBluePrint.add(i, bluePrint);
			} else {
				arrayBluePrint.add(i, SimpleBluePrintFactory.of(name + "Element", null));
			}
		}

		return arrayBluePrint;
	}

	private static boolean isConstant(Field field) {
		int modifier = field.getModifiers();

		return Modifier.isFinal(modifier) && Modifier.isStatic(modifier);
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

	private static Object getProxyValue(Object value) {
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

		return value;
	}

	private static boolean isCollection(Object value) {
		return value instanceof Collection<?> || value instanceof Map<?, ?>;
	}

}

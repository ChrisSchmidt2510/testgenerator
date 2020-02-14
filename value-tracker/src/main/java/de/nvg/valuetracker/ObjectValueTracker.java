package de.nvg.valuetracker;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import de.nvg.proxy.impl.BooleanProxy;
import de.nvg.proxy.impl.DoubleProxy;
import de.nvg.proxy.impl.FloatProxy;
import de.nvg.proxy.impl.IntegerProxy;
import de.nvg.proxy.impl.LongProxy;
import de.nvg.proxy.impl.ReferenceProxy;
import de.nvg.runtime.classdatamodel.ClassData;
import de.nvg.testgenerator.MapBuilder;
import de.nvg.testgenerator.classdata.constants.Primitives;
import de.nvg.testgenerator.logging.LogManager;
import de.nvg.testgenerator.logging.Logger;
import de.nvg.testgenerator.properties.RuntimeProperties;
import de.nvg.valuetracker.blueprint.BluePrint;
import de.nvg.valuetracker.blueprint.ComplexBluePrint;
import de.nvg.valuetracker.blueprint.Type;
import de.nvg.valuetracker.blueprint.collections.CollectionBluePrint;
import de.nvg.valuetracker.blueprint.collections.MapBluePrint;
import de.nvg.valuetracker.blueprint.simpletypes.SimpleBluePrintFactory;
import de.nvg.valuetracker.storage.ValueStorage;

public class ObjectValueTracker {

	private static final List<Class<?>> DIRECT_OUTPUT_CLASSES = Collections.unmodifiableList(Arrays.asList(
			Integer.class, Byte.class, Short.class, Character.class, Float.class, Double.class, Long.class,
			Boolean.class, LocalDate.class, LocalTime.class, LocalDateTime.class, java.util.Date.class, Date.class,
			Calendar.class, GregorianCalendar.class, BigDecimal.class, String.class));

	private static final Map<String, Function<IntegerProxy, Object>> INTEGER_PROXY_METHODS = //
			MapBuilder.<String, Function<IntegerProxy, Object>>hashMapBuilder()
					.add(Primitives.JAVA_INT, IntegerProxy::getValue)
					.add(Primitives.JAVA_BYTE, IntegerProxy::getByteValue)
					.add(Primitives.JAVA_CHAR, IntegerProxy::getCharValue)
					.add(Primitives.JAVA_SHORT, IntegerProxy::getShortValue).toUnmodifiableMap();

	private static final String CALLED_FIELDS = "calledFields";

	private static final Logger LOGGER = LogManager.getLogger(ObjectValueTracker.class);

	private final Map<Class<?>, List<BluePrint>> bluePrintsPerClass = new HashMap<>();

	public void track(Object value, String name, Type type) {
		if (value != null) {
			ValueStorage.getInstance().addBluePrint(trackValues(value, name), type);
		}
	}

	public void enableGetterCallsTracking() {
		RuntimeProperties.getInstance().setActivateTracking(true);
	}

	private BluePrint trackValues(Object value, String name) {
		LOGGER.info("Start Tracking Values for Object: " + name + " " + value);
		Object proxyValue = getProxyValue(value);

		if (DIRECT_OUTPUT_CLASSES.contains(proxyValue.getClass()) || proxyValue.getClass().isEnum()) {
			return getBluePrintForReference(proxyValue, () -> SimpleBluePrintFactory.of(name, proxyValue));
		} else if (isCollection(proxyValue)) {
			return getBluePrintForReference(proxyValue, () -> trackValuesFromCollections(proxyValue, name));
		} else {
			return getBluePrintForReference(proxyValue, () -> trackValuesFromComplexTypes(proxyValue, name));
		}

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

					if (fieldValue != null && isTestgeneratorGeneratedField(fieldValue, field.getName())
							&& fieldValue != this) {

						LOGGER.debug("Tracking Value for Field: " + field.getName() + " with Value: " + fieldValue);

						Class<?> fieldType = fieldValue.getClass();

						BluePrint elementBluePrint;
						if (DIRECT_OUTPUT_CLASSES.contains(fieldType) || fieldType.isEnum()) {
							elementBluePrint = getBluePrintForReference(value,
									() -> SimpleBluePrintFactory.of(field.getName(), fieldValue));

						} else if (isCollection(fieldValue)) {
							elementBluePrint = getBluePrintForReference(value,
									() -> trackValuesFromCollections(fieldValue, field.getName()));

						} else {
							elementBluePrint = getBluePrintForReference(value,
									() -> trackValues(fieldValue, field.getName()));
						}

						complexBluePrint.addBluePrint(elementBluePrint);
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
		if (object instanceof Collection) {
			bluePrint = new CollectionBluePrint(name, (Collection<?>) object, "Collection", "Arrays.asList");

		} else if (object instanceof List) {
			bluePrint = new CollectionBluePrint(name, (Collection<?>) object, "List", "Arrays.asList");

		} else if (object instanceof Set) {
			bluePrint = new CollectionBluePrint(name, (Collection<?>) object, "Set", null);

		} else if (object instanceof Queue) {
			bluePrint = new CollectionBluePrint(name, (Collection<?>) object, "Queue", null);
		}

		if (bluePrint != null) {
			trackValuesCollection((Collection<?>) object, name, bluePrint);
			return bluePrint;
		}

		if (object instanceof Map) {
			return trackValuesMap((Map<?, ?>) object, name);
		}

		throw new IllegalArgumentException("Unvalid Type for the parameter object");
	}

	private void trackValuesCollection(Collection<?> collection, String name, CollectionBluePrint bluePrint) {
		int counter = 1;

		for (Object value : collection) {

			BluePrint elementBluePrint = trackValues(value, name + "$" + counter++);
			bluePrint.addBluePrint(elementBluePrint);
		}
	}

	private MapBluePrint trackValuesMap(Map<?, ?> map, String name) {

		int counter = 1;

		MapBluePrint mapBluePrint = new MapBluePrint(name, map, map.getClass().getName(), null);

		if (!map.isEmpty()) {

			for (Entry<?, ?> entry : map.entrySet()) {
				BluePrint keyBluePrint = trackValues(entry.getKey(), name + "$" + counter + "Key");
				BluePrint valueBluePrint = trackValues(entry.getValue(), name + "$" + counter++ + "Value");

				mapBluePrint.addKeyValuePair(keyBluePrint, valueBluePrint);
			}

		}
		return mapBluePrint;
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
			return ((ReferenceProxy<?>) value).getValue();

		} else if (value instanceof DoubleProxy) {
			return ((DoubleProxy) value).getValue();

		} else if (value instanceof FloatProxy) {
			return ((FloatProxy) value).getValue();

		} else if (value instanceof IntegerProxy) {
			IntegerProxy proxy = ((IntegerProxy) value);
			return INTEGER_PROXY_METHODS.get(proxy.getDataType()).apply(proxy);

		} else if (value instanceof LongProxy) {
			return ((LongProxy) value).getValue();

		} else if (value instanceof BooleanProxy) {
			return ((BooleanProxy) value).getValue();
		}

		return value;
	}

	private static boolean isCollection(Object value) {
		return value instanceof List<?> || value instanceof Set<?> || value instanceof Map<?, ?>
				|| value instanceof Queue<?>;
	}

	private static boolean isTestgeneratorGeneratedField(Object value, String fieldName) {
		return !((value instanceof ClassData) || (value instanceof Set && CALLED_FIELDS.equals(fieldName)));
	}

}

package org.testgen.runtime.valuetracker.blueprint.complextypes;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.testgen.core.ReflectionUtil;
import org.testgen.core.TestgeneratorConstants;
import org.testgen.logging.LogManager;
import org.testgen.logging.Logger;
import org.testgen.runtime.valuetracker.CurrentlyBuildedBluePrints;
import org.testgen.runtime.valuetracker.ObjectValueTracker;
import org.testgen.runtime.valuetracker.TrackingException;
import org.testgen.runtime.valuetracker.blueprint.BasicBluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.factories.BluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.NullBluePrint.NullBluePrintFactory;

public class ComplexBluePrint extends BasicBluePrint<Object> {

	private List<BluePrint> bluePrints = new ArrayList<>();

	public ComplexBluePrint(String fieldName, Object value) {
		super(fieldName, value);
	}

	@Override
	public List<BluePrint> getPreExecuteBluePrints() {
		return bluePrints.stream().filter(BluePrint::isComplexType).collect(Collectors.toList());
	}

	public BluePrint getBluePrintForName(String fieldName) {
		NullBluePrintFactory nullFactory = new NullBluePrintFactory();

		return bluePrints.stream().filter(bp -> fieldName.equals(bp.getName())).findAny()
				.orElse(nullFactory.createBluePrint(fieldName, null));
	}

	public void addBluePrint(BluePrint bluePrint) {
		bluePrints.add(bluePrint);
	}

	public List<BluePrint> getChildBluePrints() {
		return Collections.unmodifiableList(bluePrints);
	}

	@Override
	public void resetBuildState() {
		if (build) {
			build = false;
			bluePrints.forEach(BluePrint::resetBuildState);
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, bluePrints);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof ComplexBluePrint))
			return false;
		ComplexBluePrint other = (ComplexBluePrint) obj;
		return Objects.equals(name, other.name) && Objects.equals(bluePrints, other.bluePrints);
	}

	@Override
	public String toString() {
		return value.getClass().getName() + " " + name + " childs: " + bluePrints.toString();
	}

	@Override
	public boolean isComplexType() {
		return true;
	}

	public static class ComplexBluePrintFactory implements BluePrintFactory {

		public static final List<String> JDK_PACKAGES = Collections
				.unmodifiableList(Arrays.asList("com.oracle", "com.sun", "java", "javax", "jdk", "org", "sun"));

		private static final String OUTER_CLASS_FIELD_NAME = "this$0";

		/**
		 * jacoco generates for coverage cases new fields into classes. Tracking these
		 * fields is useless for test generation, so we ignore these fields.
		 */
		private static final String JACOCO = "jacoco";

		private static final Logger LOGGER = LogManager.getLogger(ComplexBluePrintFactory.class);

		@Override
		public boolean createBluePrintForType(Object value) {
			return value != null;
		}

		/**
		 * giving this factory a lower priority than usual, because where is no real
		 * condition for picking this factory. This factory should only be taken, if no
		 * other factory can be picked. -5 is chosen, that it can anyway overridden.
		 */
		@Override
		public int getPriority() {
			return -5;
		}

		@Override
		public BluePrint createBluePrint(String name, Object value,
				CurrentlyBuildedBluePrints currentlyBuildedBluePrints,
				BiFunction<String, Object, BluePrint> childCallBack) {
			Class<? extends Object> valueClass = value.getClass();
			String packageName = valueClass.getPackage().getName();

			if (JDK_PACKAGES.stream().anyMatch(pkg -> packageName.startsWith(pkg))
					// if the ClassLoader of a class is null, the class was loaded the bootstrap
					// ClassLoader. Normally only JDK classes are loaded with the bootstrap
					// ClassLoader.
					&& valueClass.getClassLoader() == null)
				throw new TrackingException(
						"cant create ComplexBluePrints for JDK Classes. Extend the List of SimpleBluePrints");

			ComplexBluePrint bluePrint = new ComplexBluePrint(name, value);

			trackValues(value, valueClass, bluePrint, currentlyBuildedBluePrints, childCallBack);

			while (!valueClass.getSuperclass().equals(Object.class)) {
				valueClass = valueClass.getSuperclass();
				trackValues(value, valueClass, bluePrint, currentlyBuildedBluePrints, childCallBack);
			}

			return bluePrint;
		}

		private void trackValues(Object value, Class<?> valueClass, ComplexBluePrint bluePrint,
				CurrentlyBuildedBluePrints currentlyBuildedBluePrints,
				BiFunction<String, Object, BluePrint> childCallBack) {
			for (Field field : valueClass.getDeclaredFields()) {
				try {
					field.setAccessible(true);

					if (ReflectionUtil.isModifierConstant(field.getModifiers()))
						continue;

					Object fieldValue = ObjectValueTracker.getProxyValue(field.get(value));

					if (fieldValue == null || TestgeneratorConstants.isTestgeneratorField(field.getName())
							|| fieldValue instanceof ObjectValueTracker || field.getName().contains(JACOCO))
						continue;

					LOGGER.debug("Tracking Value for Field: " + field.getName() + " with Value: " + fieldValue);

					if (currentlyBuildedBluePrints.isCurrentlyBuilded(fieldValue))
						currentlyBuildedBluePrints.addFinishedListener(fieldValue, bp -> bluePrint.addBluePrint(bp));

					else {
						String name = value.getClass().isMemberClass() && OUTER_CLASS_FIELD_NAME.equals(field.getName())
								? "outerClass"
								: field.getName();

						BluePrint child = childCallBack.apply(name, fieldValue);
						bluePrint.addBluePrint(child);
					}

				} catch (Exception e) {
					LOGGER.error("error while creating BluePrints", e);
					throw new TrackingException("error while creating BluePrints", e);
				}

			}
		}

	}
}

package org.testgen.runtime.valuetracker.blueprint;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.testgen.core.TestgeneratorConstants;
import org.testgen.logging.LogManager;
import org.testgen.logging.Logger;
import org.testgen.runtime.valuetracker.BluePrintUnderProcessRegistration;
import org.testgen.runtime.valuetracker.ObjectValueTracker;
import org.testgen.runtime.valuetracker.TrackingException;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.NullBluePrint.NullBluePrintFactory;

public class ComplexBluePrint extends AbstractBasicBluePrint<Object> {

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
	public String toString() {
		return value.getClass().getName() + " " + name;
	}

	@Override
	public boolean isComplexType() {
		return true;
	}

	public static class ComplexBluePrintFactory implements BluePrintFactory {

		private static final String OUTER_CLASS_FIELD_NAME = "this$0";

		private static final Logger LOGGER = LogManager.getLogger(ComplexBluePrintFactory.class);

		@Override
		public boolean createBluePrintForType(Object value) {
			return value != null ? true : false;
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
		public BluePrint createBluePrint(String name, Object value, Predicate<Object> currentlyBuildedFilter,
				BluePrintUnderProcessRegistration registration, BiFunction<String, Object, BluePrint> childCallBack) {
			ComplexBluePrint bluePrint = new ComplexBluePrint(name, value);

			Class<?> valueClass = value.getClass();

			trackValues(value, valueClass, bluePrint, currentlyBuildedFilter, registration, childCallBack);

			while (!valueClass.getSuperclass().equals(Object.class)) {
				valueClass = valueClass.getSuperclass();
				trackValues(value, valueClass, bluePrint, currentlyBuildedFilter, registration, childCallBack);
			}

			return bluePrint;
		}

		private void trackValues(Object value, Class<?> valueClass, ComplexBluePrint bluePrint,
				Predicate<Object> currentlyBuildedFilter, BluePrintUnderProcessRegistration registration,
				BiFunction<String, Object, BluePrint> childCallBack) {
			for (Field field : valueClass.getDeclaredFields()) {
				try {
					field.setAccessible(true);

					if (isConstant(field))
						continue;

					Object fieldValue = ObjectValueTracker.getProxyValue(field.get(value));

					if (fieldValue == null || TestgeneratorConstants.isTestgeneratorField(field.getName())
							|| fieldValue instanceof ObjectValueTracker)
						continue;

					LOGGER.debug("Tracking Value for Field: " + field.getName() + " with Value: " + fieldValue);

					if (currentlyBuildedFilter.test(fieldValue))
						registration.register(fieldValue, bp -> bluePrint.addBluePrint(bp));

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

		private boolean isConstant(Field field) {
			int modifier = field.getModifiers();

			return Modifier.isFinal(modifier) && Modifier.isStatic(modifier);
		}

	}
}

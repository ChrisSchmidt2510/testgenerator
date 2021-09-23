package org.testgen.runtime.valuetracker.blueprint.complextypes;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.testgen.core.ReflectionUtil;
import org.testgen.logging.LogManager;
import org.testgen.logging.Logger;
import org.testgen.runtime.valuetracker.CurrentlyBuildedBluePrints;
import org.testgen.runtime.valuetracker.TrackingException;
import org.testgen.runtime.valuetracker.blueprint.BasicBluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.factories.BluePrintFactory;

public class FunctionalInterfaceBluePrint extends BasicBluePrint<Object> {

	private final Class<?> interfaceClass;
	private final int numOfParams;
	private final List<BluePrint> locals = new ArrayList<>();

	FunctionalInterfaceBluePrint(String name, Object value, Class<?> interfaceClass, int numOfParams) {
		super(name, value);
		this.interfaceClass = interfaceClass;
		this.numOfParams = numOfParams;
	}

	public Class<?> getInterfaceClass() {
		return interfaceClass;
	}

	public int numberOfParameters() {
		return numOfParams;
	}

	void addLocalVariable(BluePrint local) {
		this.locals.add(local);
	}

	@Override
	public List<BluePrint> getPreExecuteBluePrints() {
		return Collections.unmodifiableList(locals);
	}

	@Override
	public boolean isComplexType() {
		return true;
	}

	@Override
	public void resetBuildState() {
		locals.forEach(BluePrint::resetBuildState);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, interfaceClass, numOfParams, locals);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof FunctionalInterfaceBluePrint))
			return false;
		FunctionalInterfaceBluePrint other = (FunctionalInterfaceBluePrint) obj;
		return Objects.equals(name, other.name) && Objects.equals(interfaceClass, other.interfaceClass)
				&& numOfParams == other.numOfParams && Objects.equals(locals, other.locals);
	}

	public static class FunctionalInterfaceBluePrintFactory implements BluePrintFactory {

		private static final Logger LOGGER = LogManager.getLogger(FunctionalInterfaceBluePrintFactory.class);

		private static final Predicate<Method> FUNCTIONAL_INTERFACE_IDENTIFIER = m -> !m.isDefault()
				&& !Modifier.isStatic(m.getModifiers());

		@Override
		public boolean createBluePrintForType(Object value) {
			if (value != null) {
				Class<?> valueClass = value.getClass();

				return valueClass.isSynthetic() && !Proxy.isProxyClass(valueClass) && isFunctionalInterface(valueClass);
			}
			return false;
		}

		@Override
		public BluePrint createBluePrint(String name, Object value,
				CurrentlyBuildedBluePrints currentlyBuildedBluePrints,
				BiFunction<String, Object, BluePrint> childCallBack) {

			Class<?> valueClass = value.getClass();

			Class<?> functionalInterface = getFunctionalInterface(valueClass);

			Method method = Stream.of(functionalInterface.getMethods()).filter(FUNCTIONAL_INTERFACE_IDENTIFIER)
					.findAny().orElseThrow(() -> new IllegalArgumentException("isn't a FunctionalInterface"));

			FunctionalInterfaceBluePrint bluePrint = new FunctionalInterfaceBluePrint(name, value, functionalInterface,
					method.getParameterCount());

			for (Field field : valueClass.getDeclaredFields()) {
				try {
					field.setAccessible(true);

					if (ReflectionUtil.isModifierConstant(field.getModifiers()))
						continue;

					Object fieldValue = field.get(value);

					LOGGER.debug("Tracking Value for Field: " + field.getName() + " with Value: " + fieldValue);

					if (currentlyBuildedBluePrints.isCurrentlyBuilded(fieldValue))
						currentlyBuildedBluePrints.addFinishedListener(fieldValue,
								bp -> bluePrint.addLocalVariable(bp));

					else
						bluePrint.addLocalVariable(childCallBack.apply(field.getName(), fieldValue));

				} catch (Exception e) {
					LOGGER.error("error while creating BluePrints", e);
					throw new TrackingException("error while creating BluePrints", e);
				}
			}

			return bluePrint;
		}

		private boolean isFunctionalInterface(Class<?> cls) {
			for (Class<?> interfaceClass : cls.getInterfaces()) {
				if (Stream.of(interfaceClass.getMethods()).filter(FUNCTIONAL_INTERFACE_IDENTIFIER).count() == 1L)
					return true;
			}
			return false;
		}

		private Class<?> getFunctionalInterface(Class<?> cls) {
			for (Class<?> interfaceClass : cls.getInterfaces()) {
				if (Stream.of(interfaceClass.getMethods()).filter(FUNCTIONAL_INTERFACE_IDENTIFIER).count() == 1L)
					return interfaceClass;
			}

			throw new IllegalArgumentException(String.format("%s is no implementation of a FunctionalInterface", cls));
		}

	}

}

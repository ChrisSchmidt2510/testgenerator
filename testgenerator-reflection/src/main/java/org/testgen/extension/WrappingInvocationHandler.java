package org.testgen.extension;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.testgen.core.logging.LogManager;
import org.testgen.core.logging.Logger;
import org.testgen.core.properties.RuntimeProperties;

public class WrappingInvocationHandler implements InvocationHandler {
	private static final String OBJECT_VALUE_TRACKER = "de.nvg.valuetracker.ObjectValueTracker";
	private static final String METHOD_GET_INSTANCE = "getInstance";
	private static final String METHOD_TRACK = "track";

	private static final String TYPE = "de.nvg.valuetracker.blueprint.Type";
	private static final String FIELD_PROXY = "PROXY";

	private static final Logger LOGGER = LogManager.getLogger(WrappingInvocationHandler.class);

	private final InvocationHandler originalInvoker;

	private Method track;
	private Object valueTracker;
	private Object type;

	public WrappingInvocationHandler(InvocationHandler originalInvoker) {
		this.originalInvoker = originalInvoker;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object result = originalInvoker.invoke(proxy, method, args);

		if (RuntimeProperties.getInstance().isProxyTrackingActive()) {
			init();

			RuntimeProperties.getInstance().setProxyFieldTracking(true);
			track.invoke(valueTracker, result, method.getName(), type);
			RuntimeProperties.getInstance().setProxyFieldTracking(false);
		}
		return result;
	}

	private void init() {
		if (valueTracker == null) {
			try {
				ClassLoader loader = Thread.currentThread().getContextClassLoader();

				Class<?> valueTrackerClass = Class.forName(OBJECT_VALUE_TRACKER, true, loader);
				Class<?> type = Class.forName(TYPE, true, loader);
				this.track = valueTrackerClass.getMethod(METHOD_TRACK, Object.class, String.class, type);

				this.valueTracker = valueTrackerClass.getMethod(METHOD_GET_INSTANCE).invoke(null);

				Field field = type.getDeclaredField(FIELD_PROXY);
				this.type = field.get(null);
			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | //
					IllegalAccessException | NoSuchFieldException | IllegalArgumentException
					| InvocationTargetException e) {
				LOGGER.error("error while creating WrappingInvocationHandler", e);
			}
		}
	}

}

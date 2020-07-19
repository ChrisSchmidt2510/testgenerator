package org.testgen.extension;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.testgen.core.logging.LogManager;
import org.testgen.core.logging.Logger;
import org.testgen.core.properties.RuntimeProperties;

public class WrappingInvocationHandler implements InvocationHandler {
	private static final String OBJECT_VALUE_TRACKER = "de.nvg.valuetracker.ObjectValueTracker";
	private static final String METHOD_GET_INSTANCE = "getInstance";
	private static final String METHOD_TRACK_PROXY_VALUES = "trackProxyValues";

	private static final String PROXY_NAME = "proxy";

	private static final Logger LOGGER = LogManager.getLogger(WrappingInvocationHandler.class);

	private final InvocationHandler originalInvoker;

	private Method track;
	private Object valueTracker;

	public WrappingInvocationHandler(InvocationHandler originalInvoker) {
		this.originalInvoker = originalInvoker;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object result = originalInvoker.invoke(proxy, method, args);

		if (RuntimeProperties.getInstance().isProxyTrackingActive()) {
			init();

			RuntimeProperties.getInstance().setProxyFieldTracking(true);
			track.invoke(valueTracker, result, method.getName(), method.getDeclaringClass(), PROXY_NAME);
			RuntimeProperties.getInstance().setProxyFieldTracking(false);
		}

		return result;
	}

	private void init() {
		if (valueTracker == null) {
			try {
				ClassLoader loader = Thread.currentThread().getContextClassLoader();

				Class<?> valueTrackerClass = Class.forName(OBJECT_VALUE_TRACKER, true, loader);
				this.track = valueTrackerClass.getMethod(METHOD_TRACK_PROXY_VALUES, Object.class, String.class,
						Class.class, String.class);

				this.valueTracker = valueTrackerClass.getMethod(METHOD_GET_INSTANCE).invoke(null);

			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | //
					IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				LOGGER.error("error while creating WrappingInvocationHandler", e);
			}
		}
	}

}

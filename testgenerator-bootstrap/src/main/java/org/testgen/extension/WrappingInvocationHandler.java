package org.testgen.extension;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.testgen.core.ReflectionUtil;

public class WrappingInvocationHandler implements InvocationHandler {
	private static final String OBJECT_VALUE_TRACKER = "de.nvg.valuetracker.ObjectValueTracker";
	private static final String METHOD_GET_INSTANCE = "getInstance";
	private static final String METHOD_TRACK_PROXY_VALUES = "trackProxyValues";

	private static final String PROXY_NAME = "proxy";

	private static final String PROPERTY_PROXY_TRACKING = "TestgeneratorRuntimeProxyTracking";
	private static final String PROPERTY_PROXY_FIELD_TRACKING = "TestgeneratorRuntimeProxyFieldTracking";

	private final InvocationHandler originalInvoker;

	private Method track;
	private Object valueTracker;

	public WrappingInvocationHandler(InvocationHandler originalInvoker) {
		this.originalInvoker = originalInvoker;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object result = originalInvoker.invoke(proxy, method, args);

		if (Boolean.getBoolean(PROPERTY_PROXY_TRACKING)) {
			init();

			setPropertyProxyFieldTracking(true);
			track.invoke(valueTracker, result, method.getName(), method.getDeclaringClass(), PROXY_NAME);
			setPropertyProxyFieldTracking(false);
		}

		return result;
	}

	private void init() {
		if (valueTracker == null) {
			try {
				Class<?> valueTrackerClass = ReflectionUtil.forName(OBJECT_VALUE_TRACKER);
				this.track = ReflectionUtil.getMethod(valueTrackerClass, METHOD_TRACK_PROXY_VALUES, Object.class,
						String.class, Class.class, String.class);

				this.valueTracker = ReflectionUtil
						.invoke(ReflectionUtil.getMethod(valueTrackerClass, METHOD_GET_INSTANCE), null);

			} catch (Exception e) {
				throw new RuntimeException("error while creating WrappingInvocationHandler", e);
			}
		}
	}

	private static void setPropertyProxyFieldTracking(boolean value) {
		System.setProperty(PROPERTY_PROXY_FIELD_TRACKING, Boolean.toString(value));
	}

}

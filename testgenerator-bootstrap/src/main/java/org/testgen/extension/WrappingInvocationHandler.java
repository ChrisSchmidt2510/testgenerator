package org.testgen.extension;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.testgen.config.TestgeneratorConfig;
import org.testgen.core.ReflectionUtil;

public class WrappingInvocationHandler implements InvocationHandler {
	private static final String OBJECT_VALUE_TRACKER = "org.testgen.runtime.valuetracker.ObjectValueTracker";
	private static final String METHOD_GET_INSTANCE = "getInstance";
	private static final String METHOD_TRACK_PROXY_VALUES = "trackProxy";

	private static final String PROXY_BLUE_PRINT_METHOD_ADD_PROXY_RESULT ="addProxyResult";
	
	private static final String PROXY_NAME = "proxy";

	private final InvocationHandler originalInvoker;

	private Object proxyBluePrint;
	private Method addProxyResult;

	public WrappingInvocationHandler(InvocationHandler originalInvoker) {
		this.originalInvoker = originalInvoker;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object result = originalInvoker.invoke(proxy, method, args);

			if (!Void.TYPE.equals(method.getReturnType())&& TestgeneratorConfig.isProxyTrackingActivated()) {
				init(proxy);

				TestgeneratorConfig.setProxyFieldTracking(true);
				addProxyResult.invoke(method, result);
				TestgeneratorConfig.setProxyFieldTracking(false);
			}

		return result;
	}

	public InvocationHandler getOriginalInvoker() {
		return originalInvoker;
	}

	private void init(Object proxy) {
		if (proxyBluePrint == null) {
			try {
				Class<?> valueTrackerClass = ReflectionUtil.forName(OBJECT_VALUE_TRACKER);
				Method trackProxy = ReflectionUtil.getMethod(valueTrackerClass, METHOD_TRACK_PROXY_VALUES, Object.class,
						String.class);

				Object valueTracker = ReflectionUtil
						.invoke(ReflectionUtil.getMethod(valueTrackerClass, METHOD_GET_INSTANCE), null);

				proxyBluePrint = ReflectionUtil.invoke(trackProxy, valueTracker, proxy, PROXY_NAME);
				
				addProxyResult = ReflectionUtil.getMethod(proxyBluePrint.getClass(), PROXY_BLUE_PRINT_METHOD_ADD_PROXY_RESULT, Method.class, Object.class);
				
			} catch (Exception e) {
				throw new RuntimeException("error while creating WrappingInvocationHandler", e);
			}
		}
	}

}

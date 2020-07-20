package org.testgen.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class ReflectionUtil {

	private ReflectionUtil() {
	}

	public static Class<?> forName(String className, ClassLoader loader) {
		try {
			return Class.forName(className, false, loader);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public static Class<?> forName(String className) {
		return forName(className, Thread.currentThread().getContextClassLoader());
	}

	public static Constructor<?> getConstructor(Class<?> implementer, Class<?>... parameterTypes) {
		try {
			return implementer.getConstructor(parameterTypes);
		} catch (NoSuchMethodException | SecurityException e) {
			return null;
		}
	}

	public static Method getMethod(Class<?> caller, String name, Class<?>... params) {
		try {
			return caller.getMethod(name, params);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new RuntimeException("no method found for name" + name + "with params " + params, e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<T> clazz, Class<?>[] parameterTypes, Object... args) {
		Constructor<?> constructor = getConstructor(clazz, parameterTypes);

		if (constructor != null) {
			try {
				return (T) constructor.newInstance(args);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new RuntimeException("cant create instance of Class " + clazz, e);
			}
		}
		throw new RuntimeException("no constructor found for parameters" + parameterTypes);
	}

	public static <T> T newInstance(Class<T> clazz) {
		return newInstance(clazz, null);
	}

	@SuppressWarnings("unchecked")
	public static <T> T invoke(Method method, Object caller, Object... args) {
		try {
			return (T) method.invoke(caller, args);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException("cant invoke method" + method, e);
		}
	}

}

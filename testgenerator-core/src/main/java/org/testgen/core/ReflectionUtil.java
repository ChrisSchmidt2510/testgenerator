package org.testgen.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

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

	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<T> clazz, Class<?>[] parameterTypes, Object... args) {
		Constructor<?> constructor = getConstructor(clazz, parameterTypes);

		if (constructor != null) {
			try {
				return (T) constructor.newInstance(args);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new RuntimeException("cant create instance of Class " + clazz);
			}
		}
		throw new RuntimeException("no constructor found for parameters" + parameterTypes);
	}

	public static <T> T newInstance(Class<T> clazz) {
		return newInstance(clazz, null);
	}

}

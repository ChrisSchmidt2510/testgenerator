package org.testgen.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

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

	public static void checkForInterface(Class<?> clazz, Class<?> interfaceClass) {
		if (!Arrays.stream(clazz.getInterfaces()).anyMatch(i -> interfaceClass.equals(i))) {
			throw new IllegalArgumentException(
					String.format("%s is a invalid implementation for %s", clazz, interfaceClass));
		}
	}

	public static Constructor<?> getConstructor(Class<?> implementer, Class<?>... parameterTypes) {
		try {
			return implementer.getConstructor(parameterTypes);
		} catch (NoSuchMethodException | SecurityException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T getField(Class<?> caller, String fieldName, Object instance) {
		Field field;
		try {
			field = caller.getDeclaredField(fieldName);
			field.setAccessible(true);
			return (T) field.get(instance);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException("no field found for name " + fieldName, e);
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

	public static Field getField(Class<?> clazz, String name) {
		try {
			return clazz.getDeclaredField(name);
		} catch (NoSuchFieldException | SecurityException e) {
			throw new RuntimeException("no Field found for name " + name + " in Class " + clazz, e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T accessStaticField(Field field) {
		try {
			return (T) field.get(null);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException("cant invoke Field" + field);
		}
	}

}

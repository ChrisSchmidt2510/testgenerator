package org.testgen.core;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;

public final class MethodHandles {
	private static final Lookup LOOKUP = java.lang.invoke.MethodHandles.lookup();

	private MethodHandles() {
	}

	public static void setFieldValue(Object reference, String fieldName, Object value) {

		try {
			Field field = reference.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);

			MethodHandle setter = LOOKUP.unreflectSetter(field);
			setter.invoke(reference, value);
		} catch (Throwable e) {
			throw new RuntimeException("Field: " + fieldName + " can't be invoked for Class: " + reference, e);
		}
	}

	public static <T> T getFieldValue(Object reference, String fieldName) {
		try {
			Field field = reference.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);

			MethodHandle getter = LOOKUP.unreflectGetter(field);
			return (T) getter.invoke(reference);
		} catch (Throwable e) {
			throw new RuntimeException("Field: " + fieldName + " can't be invoked for Class: " + reference, e);
		}
	}

	public static <T> T getStaticFieldValue(Class<?> referenceClass, String fieldName) {
		Field field;
		try {
			field = referenceClass.getDeclaredField(fieldName);
			field.setAccessible(true);

			MethodHandle getter = LOOKUP.unreflectGetter(field);
			return (T) getter.invoke();
		} catch (Throwable e) {
			throw new RuntimeException("static Field:" + fieldName + " can't be invoked for Class: " + referenceClass,
					e);
		}

	}

}

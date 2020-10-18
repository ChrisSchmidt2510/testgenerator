package sun.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.testgen.extension.Modified;

import de.nvg.proxy.impl.BooleanProxy;
import de.nvg.proxy.impl.DoubleProxy;
import de.nvg.proxy.impl.FloatProxy;
import de.nvg.proxy.impl.IntegerProxy;
import de.nvg.proxy.impl.LongProxy;
import de.nvg.proxy.impl.ReferenceProxy;

public final class UnsafeFieldAccessorFactory {

	private UnsafeFieldAccessorFactory() {
	}

	@Modified
	public static FieldAccessor newFieldAccessor(Field field, boolean override) {
		Class<?> type = field.getOriginalType();

		boolean isStatic = Modifier.isStatic(field.getModifiers());
		boolean isFinal = Modifier.isFinal(field.getModifiers());
		boolean isVolatile = Modifier.isVolatile(field.getModifiers());
		boolean isQualified = isFinal || isVolatile;
		boolean isReadOnly = isFinal && (isStatic || !override);

		if (isStatic) {
			// This code path does not guarantee that the field's
			// declaring class has been initialized, but it must be
			// before performing reflective operations.
			UnsafeFieldAccessorImpl.unsafe.ensureClassInitialized(field.getDeclaringClass());

			if (!isQualified) {
				if (type == Boolean.TYPE) {
					return new UnsafeStaticBooleanFieldAccessorImpl(field);
				} else if (type == Byte.TYPE) {
					return new UnsafeStaticByteFieldAccessorImpl(field);
				} else if (type == Short.TYPE) {
					return new UnsafeStaticShortFieldAccessorImpl(field);
				} else if (type == Character.TYPE) {
					return new UnsafeStaticCharacterFieldAccessorImpl(field);
				} else if (type == Integer.TYPE) {
					return new UnsafeStaticIntegerFieldAccessorImpl(field);
				} else if (type == Long.TYPE) {
					return new UnsafeStaticLongFieldAccessorImpl(field);
				} else if (type == Float.TYPE) {
					return new UnsafeStaticFloatFieldAccessorImpl(field);
				} else if (type == Double.TYPE) {
					return new UnsafeStaticDoubleFieldAccessorImpl(field);
				} else {
					return new UnsafeStaticObjectFieldAccessorImpl(field);
				}
			} else {
				if (type == Boolean.TYPE) {
					return new UnsafeQualifiedStaticBooleanFieldAccessorImpl(field, isReadOnly);
				} else if (type == Byte.TYPE) {
					return new UnsafeQualifiedStaticByteFieldAccessorImpl(field, isReadOnly);
				} else if (type == Short.TYPE) {
					return new UnsafeQualifiedStaticShortFieldAccessorImpl(field, isReadOnly);
				} else if (type == Character.TYPE) {
					return new UnsafeQualifiedStaticCharacterFieldAccessorImpl(field, isReadOnly);
				} else if (type == Integer.TYPE) {
					return new UnsafeQualifiedStaticIntegerFieldAccessorImpl(field, isReadOnly);
				} else if (type == Long.TYPE) {
					return new UnsafeQualifiedStaticLongFieldAccessorImpl(field, isReadOnly);
				} else if (type == Float.TYPE) {
					return new UnsafeQualifiedStaticFloatFieldAccessorImpl(field, isReadOnly);
				} else if (type == Double.TYPE) {
					return new UnsafeQualifiedStaticDoubleFieldAccessorImpl(field, isReadOnly);
				} else {
					return new UnsafeQualifiedStaticObjectFieldAccessorImpl(field, isReadOnly);
				}
			}
		} else {
			if (!isQualified) {
				if (type == Boolean.TYPE) {
					return new UnsafeBooleanFieldAccessorImpl(field);
				} else if (type == Byte.TYPE) {
					return new UnsafeByteFieldAccessorImpl(field);
				} else if (type == Short.TYPE) {
					return new UnsafeShortFieldAccessorImpl(field);
				} else if (type == Character.TYPE) {
					return new UnsafeCharacterFieldAccessorImpl(field);
				} else if (type == Integer.TYPE) {
					return new UnsafeIntegerFieldAccessorImpl(field);
				} else if (type == Long.TYPE) {
					return new UnsafeLongFieldAccessorImpl(field);
				} else if (type == Float.TYPE) {
					return new UnsafeFloatFieldAccessorImpl(field);
				} else if (type == Double.TYPE) {
					return new UnsafeDoubleFieldAccessorImpl(field);
				} else if (type == BooleanProxy.class) {
					return new UnsafeBooleanProxyFieldAccessorImpl(field);
				} else if (type == IntegerProxy.class) {
					return new UnsafeIntegerProxyFieldAccessorImpl(field);
				} else if (type == FloatProxy.class) {
					return new UnsafeFloatProxyFieldAccessorImpl(field);
				} else if (type == DoubleProxy.class) {
					return new UnsafeDoubleProxyFieldAccessorImpl(field);
				} else if (type == LongProxy.class) {
					return new UnsafeLongProxyFieldAccessorImpl(field);
				} else if (type == ReferenceProxy.class) {
					return new UnsafeReferenceProxyFieldAccessorImpl(field);
				} else {
					return new UnsafeObjectFieldAccessorImpl(field);
				}
			} else {
				if (type == Boolean.TYPE) {
					return new UnsafeQualifiedBooleanFieldAccessorImpl(field, isReadOnly);
				} else if (type == Byte.TYPE) {
					return new UnsafeQualifiedByteFieldAccessorImpl(field, isReadOnly);
				} else if (type == Short.TYPE) {
					return new UnsafeQualifiedShortFieldAccessorImpl(field, isReadOnly);
				} else if (type == Character.TYPE) {
					return new UnsafeQualifiedCharacterFieldAccessorImpl(field, isReadOnly);
				} else if (type == Integer.TYPE) {
					return new UnsafeQualifiedIntegerFieldAccessorImpl(field, isReadOnly);
				} else if (type == Long.TYPE) {
					return new UnsafeQualifiedLongFieldAccessorImpl(field, isReadOnly);
				} else if (type == Float.TYPE) {
					return new UnsafeQualifiedFloatFieldAccessorImpl(field, isReadOnly);
				} else if (type == Double.TYPE) {
					return new UnsafeQualifiedDoubleFieldAccessorImpl(field, isReadOnly);
				} else if (type == BooleanProxy.class) {
					return new UnsafeQualifiedBooleanProxyFieldAccessorImpl(field, isReadOnly);
				} else if (type == IntegerProxy.class) {
					return new UnsafeQualifiedIntegerProxyFieldAccessorImpl(field, isReadOnly);
				} else if (type == FloatProxy.class) {
					return new UnsafeQualifiedFloatProxyFieldAccessorImpl(field, isReadOnly);
				} else if (type == DoubleProxy.class) {
					return new UnsafeQualifiedDoubleProxyFieldAccessorImpl(field, isReadOnly);
				} else if (type == LongProxy.class) {
					return new UnsafeQualifiedLongProxyFieldAccessorImpl(field, isReadOnly);
				} else if (type == ReferenceProxy.class) {
					return new UnsafeQualifiedReferenceProxyFieldAccessorImpl(field, isReadOnly);
				} else {
					return new UnsafeQualifiedObjectFieldAccessorImpl(field, isReadOnly);
				}
			}
		}

	}

}

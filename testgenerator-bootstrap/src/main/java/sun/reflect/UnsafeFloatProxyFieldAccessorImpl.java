package sun.reflect;

import java.lang.reflect.Field;

import de.nvg.proxy.impl.FloatProxy;

@SuppressWarnings("restriction")
public class UnsafeFloatProxyFieldAccessorImpl extends UnsafeBaseAccessorImpl {

	UnsafeFloatProxyFieldAccessorImpl(Field field) {
		super(field);
	}

	@Override
	public Object get(Object obj) throws IllegalArgumentException {
		return getFloat(obj);
	}

	@Override
	public float getFloat(Object obj) throws IllegalArgumentException {
		ensureObj(obj);

		FloatProxy proxy = getProxy(FloatProxy.class, obj);
		return proxy.getValue();
	}

	@Override
	public void set(Object obj, Object value) throws IllegalArgumentException, IllegalAccessException {
		ensureObj(obj);
		if (isFinal) {
			throwFinalFieldIllegalAccessException(value);
		}

		if (value == null) {
			throwSetIllegalArgumentException(value);
		}

		if (value instanceof Float) {
			FloatProxy proxy = getProxy(FloatProxy.class, obj);
			proxy.setValue((float) value);
			return;
		}

		throwSetIllegalArgumentException(value);

	}

	@Override
	public void setFloat(Object obj, float f) throws IllegalArgumentException, IllegalAccessException {
		ensureObj(obj);
		if (isFinal) {
			throwFinalFieldIllegalAccessException(f);
		}

		FloatProxy proxy = getProxy(FloatProxy.class, obj);
		proxy.setValue(f);
	}

}

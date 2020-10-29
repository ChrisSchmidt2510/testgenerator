package sun.reflect;

import java.lang.reflect.Field;

import org.testgen.runtime.proxy.impl.DoubleProxy;

@SuppressWarnings("restriction")
public class UnsafeDoubleProxyFieldAccessorImpl extends UnsafeBaseAccessorImpl {

	UnsafeDoubleProxyFieldAccessorImpl(Field field) {
		super(field);
	}

	@Override
	public Object get(Object obj) throws IllegalArgumentException {
		return getDouble(obj);
	}

	@Override
	public double getDouble(Object obj) throws IllegalArgumentException {
		ensureObj(obj);

		DoubleProxy proxy = getProxy(DoubleProxy.class, obj);
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

		if (value instanceof Double) {
			DoubleProxy proxy = getProxy(DoubleProxy.class, obj);
			proxy.setValue((double) value);
			return;
		}

		throwSetIllegalArgumentException(value);

	}

	@Override
	public void setDouble(Object obj, double d) throws IllegalArgumentException, IllegalAccessException {
		ensureObj(obj);
		if (isFinal) {
			throwFinalFieldIllegalAccessException(d);
		}

		DoubleProxy proxy = getProxy(DoubleProxy.class, obj);
		proxy.setValue(d);
	}

}

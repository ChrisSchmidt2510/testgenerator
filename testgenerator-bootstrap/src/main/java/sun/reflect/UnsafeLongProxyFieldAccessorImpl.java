package sun.reflect;

import java.lang.reflect.Field;

import org.testgen.runtime.proxy.impl.LongProxy;

@SuppressWarnings("restriction")
public class UnsafeLongProxyFieldAccessorImpl extends UnsafeBaseAccessorImpl {

	UnsafeLongProxyFieldAccessorImpl(Field field) {
		super(field);
	}

	@Override
	public Object get(Object obj) throws IllegalArgumentException {
		return getLong(obj);
	}

	@Override
	public long getLong(Object obj) throws IllegalArgumentException {
		ensureObj(obj);

		LongProxy proxy = getProxy(LongProxy.class, obj);
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

		if (value instanceof Long) {
			LongProxy proxy = getProxy(LongProxy.class, obj);
			proxy.setValue((long) value);
			return;
		}

		throwSetIllegalArgumentException(value);

	}

	@Override
	public void setLong(Object obj, long l) throws IllegalArgumentException, IllegalAccessException {
		ensureObj(obj);
		if (isFinal) {
			throwFinalFieldIllegalAccessException(l);
		}

		LongProxy proxy = getProxy(LongProxy.class, obj);
		proxy.setValue(l);
	}

}

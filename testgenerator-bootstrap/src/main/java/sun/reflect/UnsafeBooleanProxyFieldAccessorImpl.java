package sun.reflect;

import java.lang.reflect.Field;

import org.testgen.runtime.proxy.impl.BooleanProxy;

@SuppressWarnings("restriction")
public class UnsafeBooleanProxyFieldAccessorImpl extends UnsafeBaseAccessorImpl {

	UnsafeBooleanProxyFieldAccessorImpl(Field field) {
		super(field);
	}

	@Override
	public Object get(Object obj) throws IllegalArgumentException {
		return getBoolean(obj);
	}

	@Override
	public boolean getBoolean(Object obj) throws IllegalArgumentException {
		ensureObj(obj);

		BooleanProxy proxy = getProxy(BooleanProxy.class, obj);
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

		if (value instanceof Boolean) {
			BooleanProxy proxy = getProxy(BooleanProxy.class, obj);
			proxy.setValue((boolean) value);
			return;
		}

		throwSetIllegalArgumentException(value);
	}

	@Override
	public void setBoolean(Object obj, boolean z) throws IllegalArgumentException, IllegalAccessException {
		ensureObj(obj);
		if (isFinal) {
			throwFinalFieldIllegalAccessException(z);
		}

		BooleanProxy proxy = getProxy(BooleanProxy.class, obj);
		proxy.setValue(z);
	}

}

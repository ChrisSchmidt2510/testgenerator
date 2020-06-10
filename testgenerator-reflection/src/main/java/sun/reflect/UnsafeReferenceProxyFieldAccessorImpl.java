package sun.reflect;

import java.lang.reflect.Field;

import de.nvg.proxy.impl.ReferenceProxy;

@SuppressWarnings("restriction")
public class UnsafeReferenceProxyFieldAccessorImpl extends UnsafeBaseAccessorImpl {

	UnsafeReferenceProxyFieldAccessorImpl(Field field) {
		super(field);
	}

	@Override
	public Object get(Object obj) throws IllegalArgumentException {
		ensureObj(obj);
		ReferenceProxy<?> proxy = getProxy(ReferenceProxy.class, obj);
		return proxy.getValue();
	}

	@Override
	public void set(Object obj, Object value) throws IllegalArgumentException, IllegalAccessException {
		ensureObj(obj);
		if (isFinal) {
			throwFinalFieldIllegalAccessException(value);
		}

		@SuppressWarnings("unchecked")
		ReferenceProxy<Object> proxy = getProxy(ReferenceProxy.class, obj);
		if (value != null && !proxy.getDataType().isAssignableFrom(value.getClass())) {
			throwSetIllegalArgumentException(value);
		}

		proxy.setValue(value);

	}

}

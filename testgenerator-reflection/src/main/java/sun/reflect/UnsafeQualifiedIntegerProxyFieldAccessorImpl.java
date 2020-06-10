package sun.reflect;

import java.lang.reflect.Field;

import de.nvg.proxy.impl.IntegerProxy;

@SuppressWarnings("restriction")
public class UnsafeQualifiedIntegerProxyFieldAccessorImpl extends UnsafeQualifiedBaseAccessorImpl {

	UnsafeQualifiedIntegerProxyFieldAccessorImpl(Field field, boolean isReadOnly) {
		super(field, isReadOnly);
	}

	@Override
	public Object get(Object obj) throws IllegalArgumentException {
		ensureObj(obj);
		IntegerProxy proxy = getProxy(IntegerProxy.class, obj);

		Class<?> dataType = proxy.getDataType();
		if (Byte.TYPE == dataType) {
			return proxy.getByteValue();
		} else if (Character.TYPE == dataType) {
			return proxy.getCharValue();
		} else if (Short.TYPE == dataType) {
			return proxy.getByteValue();
		} else {
			return proxy.getValue();
		}
	}

	@Override
	public byte getByte(Object obj) throws IllegalArgumentException {
		ensureObj(obj);

		IntegerProxy proxy = getProxy(IntegerProxy.class, obj);

		if (Byte.TYPE == proxy.getDataType()) {
			return proxy.getByteValue();
		}

		throw newGetByteIllegalArgumentException();
	}

	@Override
	public char getChar(Object obj) throws IllegalArgumentException {
		ensureObj(obj);

		IntegerProxy proxy = getProxy(IntegerProxy.class, obj);

		if (Character.TYPE == proxy.getDataType()) {
			return proxy.getCharValue();
		}

		throw newGetCharIllegalArgumentException();
	}

	@Override
	public short getShort(Object obj) throws IllegalArgumentException {
		ensureObj(obj);

		IntegerProxy proxy = getProxy(IntegerProxy.class, obj);

		if (Short.TYPE == proxy.getDataType()) {
			return proxy.getShortValue();
		}

		throw newGetShortIllegalArgumentException();
	}

	@Override
	public int getInt(Object obj) throws IllegalArgumentException {
		ensureObj(obj);

		IntegerProxy proxy = getProxy(IntegerProxy.class, obj);

		if (Integer.TYPE == proxy.getDataType()) {
			return proxy.getValue();
		}

		throw newGetIntIllegalArgumentException();
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
		if (value instanceof Byte) {
			setProxyValue(obj, (byte) value);
			return;
		} else if (value instanceof Character) {
			setProxyValue(obj, (char) value);
		} else if (value instanceof Short) {
			setProxyValue(obj, (short) value);
		} else if (value instanceof Integer) {
			setProxyValue(obj, (int) value);
		}

		throwSetIllegalArgumentException(value);

	}

	@Override
	public void setByte(Object obj, byte b) throws IllegalArgumentException, IllegalAccessException {
		ensureObj(obj);
		if (isFinal) {
			throwFinalFieldIllegalAccessException(b);
		}

		setProxyValue(obj, b);
	}

	@Override
	public void setChar(Object obj, char c) throws IllegalArgumentException, IllegalAccessException {
		ensureObj(obj);
		if (isFinal) {
			throwFinalFieldIllegalAccessException(c);
		}

		setProxyValue(obj, c);
	}

	@Override
	public void setShort(Object obj, short s) throws IllegalArgumentException, IllegalAccessException {
		ensureObj(obj);
		if (isFinal) {
			throwFinalFieldIllegalAccessException(s);
		}

		setProxyValue(obj, s);
	}

	@Override
	public void setInt(Object obj, int i) throws IllegalArgumentException, IllegalAccessException {
		ensureObj(obj);
		if (isFinal) {
			throwFinalFieldIllegalAccessException(i);
		}

		setProxyValue(obj, i);
	}

	private void setProxyValue(Object obj, int value) {
		IntegerProxy proxy = getProxy(IntegerProxy.class, obj);
		proxy.setValue(value);
	}

}

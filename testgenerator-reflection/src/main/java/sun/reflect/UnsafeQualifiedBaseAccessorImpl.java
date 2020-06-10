package sun.reflect;

import java.lang.reflect.Field;

@SuppressWarnings("restriction")
public abstract class UnsafeQualifiedBaseAccessorImpl extends UnsafeQualifiedFieldAccessorImpl {

	UnsafeQualifiedBaseAccessorImpl(Field field, boolean isReadOnly) {
		super(field, isReadOnly);
	}

	protected <T> T getProxy(Class<T> proxyClass, Object obj) {
		return proxyClass.cast(unsafe.getObjectVolatile(obj, fieldOffset));
	}

	@Override
	public boolean getBoolean(Object obj) throws IllegalArgumentException {
		throw newGetBooleanIllegalArgumentException();
	}

	@Override
	public byte getByte(Object obj) throws IllegalArgumentException {
		throw newGetByteIllegalArgumentException();
	}

	@Override
	public char getChar(Object obj) throws IllegalArgumentException {
		throw newGetCharIllegalArgumentException();
	}

	@Override
	public short getShort(Object obj) throws IllegalArgumentException {
		throw newGetShortIllegalArgumentException();
	}

	@Override
	public int getInt(Object obj) throws IllegalArgumentException {
		throw newGetIntIllegalArgumentException();
	}

	@Override
	public long getLong(Object obj) throws IllegalArgumentException {
		throw newGetLongIllegalArgumentException();
	}

	@Override
	public float getFloat(Object obj) throws IllegalArgumentException {
		throw newGetFloatIllegalArgumentException();
	}

	@Override
	public double getDouble(Object obj) throws IllegalArgumentException {
		throw newGetDoubleIllegalArgumentException();
	}

	@Override
	public void setBoolean(Object obj, boolean z) throws IllegalArgumentException, IllegalAccessException {
		throwSetIllegalArgumentException(z);
	}

	@Override
	public void setByte(Object obj, byte b) throws IllegalArgumentException, IllegalAccessException {
		throwSetIllegalArgumentException(b);
	}

	@Override
	public void setChar(Object obj, char c) throws IllegalArgumentException, IllegalAccessException {
		throwSetIllegalArgumentException(c);
	}

	@Override
	public void setShort(Object obj, short s) throws IllegalArgumentException, IllegalAccessException {
		throwSetIllegalArgumentException(s);
	}

	@Override
	public void setInt(Object obj, int i) throws IllegalArgumentException, IllegalAccessException {
		throwSetIllegalArgumentException(i);
	}

	@Override
	public void setLong(Object obj, long l) throws IllegalArgumentException, IllegalAccessException {
		throwSetIllegalArgumentException(l);
	}

	@Override
	public void setFloat(Object obj, float f) throws IllegalArgumentException, IllegalAccessException {
		throwSetIllegalArgumentException(f);
	}

	@Override
	public void setDouble(Object obj, double d) throws IllegalArgumentException, IllegalAccessException {
		throwSetIllegalArgumentException(d);
	}

}

package de.nvg.agent.classdata.modification.helper.cache;

import static de.nvg.agent.classdata.modification.fields.ProxyConstants.*;

import java.util.Objects;

import javassist.bytecode.MethodInfo;

public final class CodeType {
	final byte operationType;
	final String proxy;

	final String fieldName;
	final String fieldType;

	final String methodName;
	final String methodDescriptor;

	static final byte NEW_PROXY = 10;
	static final byte PROXY_INIT = 20;
	static final byte SET_PROXY_VALUE = 30;
	static final byte SET_PROXY_VALUE_METHOD = 35;
	static final byte GET_PROXY_VALUE = 40;

	private CodeType(byte operationType, String proxy, String fieldName, String fieldType, //
			String methodName, String methodDescriptor) {
		this.operationType = operationType;
		this.proxy = proxy;
		this.fieldName = fieldName;
		this.fieldType = fieldType;
		this.methodName = methodName;
		this.methodDescriptor = methodDescriptor;
	}

	public static CodeType typeGetProxyValue(String fieldType, String fieldName) {
		String proxy = getProxyClassname(fieldType);
		String methodName = getValueMethodName(fieldType);
		String methodDescriptor = getGetValueDescriptor(fieldType);

		return new CodeType(GET_PROXY_VALUE, proxy, fieldName, fieldType, methodName, methodDescriptor);
	}

	public static CodeType typeSetProxyValue(String fieldType, String fieldName) {
		String proxy = getProxyClassname(fieldType);

		return new CodeType(SET_PROXY_VALUE, proxy, fieldName, fieldType, null, null);
	}

	public static CodeType typeSetProxyValueMethod(String fieldType) {
		String proxy = getProxyClassname(fieldType);

		return new CodeType(SET_PROXY_VALUE_METHOD, proxy, null, fieldType, SET_VALUE, getSetValueDescriptor(proxy));
	}

	public static CodeType typeNewProxy(String fieldType) {
		String proxy = getProxyClassname(fieldType);

		return new CodeType(NEW_PROXY, proxy, null, fieldType, null, null);
	}

	public static CodeType typeProxyInit(String fieldType, String fieldName, boolean defaultInit) {
		String proxy = getProxyClassname(fieldType);

		String descriptor = defaultInit ? getDefaultInitDescriptor(proxy)
				: PROXY_CONSTRUCTOR_WITH_INITALIZATION.get(proxy);

		return new CodeType(PROXY_INIT, proxy, fieldName, fieldType, MethodInfo.nameInit, descriptor);
	}

	@Override
	public int hashCode() {
		return Objects.hash(fieldName, fieldType, methodDescriptor, methodName, operationType, proxy);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof CodeType))
			return false;
		CodeType other = (CodeType) obj;
		return Objects.equals(fieldName, other.fieldName) && Objects.equals(fieldType, other.fieldType)
				&& Objects.equals(methodDescriptor, other.methodDescriptor)
				&& Objects.equals(methodName, other.methodName) && operationType == other.operationType
				&& Objects.equals(proxy, other.proxy);
	}

}

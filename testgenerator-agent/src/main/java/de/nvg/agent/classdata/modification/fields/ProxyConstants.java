package de.nvg.agent.classdata.modification.fields;

import java.util.Map;

import org.testgen.core.MapBuilder;
import org.testgen.core.classdata.constants.JVMTypes;
import org.testgen.core.classdata.constants.Primitives;

public class ProxyConstants {
	public static final String REFERENCE_PROXY_CLASSNAME = "de/nvg/proxy/impl/ReferenceProxy";
	public static final String REFERENCE_PROXY = "Lde/nvg/proxy/impl/ReferenceProxy;";
	public static final String REFERENCE_PROXY_CONSTRUCTOR = "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Class;)V";
	public static final String REFERENCE_PROXY_DEFAULT_CONSTRUCTOR = "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Class;)V";

	public static final String BOOLEAN_PROXY_CLASSNAME = "de/nvg/proxy/impl/BooleanProxy";
	public static final String BOOLEAN_PROXY = "Lde/nvg/proxy/impl/BooleanProxy;";
	public static final String BOOLEAN_PROXY_CONSTRUCTOR = "(ZLjava/lang/Object;Ljava/lang/String;)V";

	public static final String DOUBLE_PROXY_CLASSNAME = "de/nvg/proxy/impl/DoubleProxy";
	public static final String DOUBLE_PROXY = "Lde/nvg/proxy/impl/DoubleProxy;";
	public static final String DOUBLE_PROXY_CONSTRUCTOR = "(DLjava/lang/Object;Ljava/lang/String;)V";

	public static final String FLOAT_PROXY_CLASSNAME = "de/nvg/proxy/impl/FloatProxy";
	public static final String FLOAT_PROXY = "Lde/nvg/proxy/impl/FloatProxy;";
	public static final String FLOAT_PROXY_CONSTRUCTOR = "(FLjava/lang/Object;Ljava/lang/String;)V";

	public static final String INTEGER_PROXY_CLASSNAME = "de/nvg/proxy/impl/IntegerProxy";
	public static final String INTEGER_PROXY = "Lde/nvg/proxy/impl/IntegerProxy;";
	public static final String INTEGER_PROXY_CONSTRUCTOR = "(ILjava/lang/Object;Ljava/lang/String;Ljava/lang/Class;)V";
	public static final String INTEGER_PROXY_DEFAULT_CONSTRUCTOR = "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Class;)V";

	public static final String LONG_PROXY_CLASSNAME = "de/nvg/proxy/impl/LongProxy";
	public static final String LONG_PROXY = "Lde/nvg/proxy/impl/LongProxy;";
	public static final String LONG_PROXY_CONSTRUCTOR = "(LLjava/lang/Object;Ljava/lang/String;)V";

	public static final String DEFAULT_PROXY_CONSTRUCTOR = "(Ljava/lang/Object;Ljava/lang/String;)V";

	public static final String SET_VALUE = "setValue";

	public static final String GET_VALUE = "getValue";
	public static final String GET_BYTE_VALUE = "getByteValue";
	public static final String GET_SHORT_VALUE = "getShortValue";
	public static final String GET_CHAR_VALUE = "getCharValue";

	private static final Map<String, String> PRIMITIVE_PROXIES = MapBuilder.<String, String>hashMapBuilder()
			.add(Primitives.JVM_BYTE, INTEGER_PROXY)//
			.add(Primitives.JVM_BOOLEAN, BOOLEAN_PROXY)//
			.add(Primitives.JVM_SHORT, INTEGER_PROXY)//
			.add(Primitives.JVM_CHAR, INTEGER_PROXY)//
			.add(Primitives.JVM_INT, INTEGER_PROXY)//
			.add(Primitives.JVM_FLOAT, FLOAT_PROXY)//
			.add(Primitives.JVM_DOUBLE, DOUBLE_PROXY)//
			.add(Primitives.JVM_LONG, LONG_PROXY).toUnmodifiableMap();

	private static final Map<String, String> PRIMITIVE_PROXIES_CLASSNAME = MapBuilder.<String, String>hashMapBuilder()//
			.add(Primitives.JVM_BYTE, INTEGER_PROXY_CLASSNAME)//
			.add(Primitives.JVM_BOOLEAN, BOOLEAN_PROXY_CLASSNAME)//
			.add(Primitives.JVM_SHORT, INTEGER_PROXY_CLASSNAME)//
			.add(Primitives.JVM_CHAR, INTEGER_PROXY_CLASSNAME)//
			.add(Primitives.JVM_INT, INTEGER_PROXY_CLASSNAME)//
			.add(Primitives.JVM_FLOAT, FLOAT_PROXY_CLASSNAME)//
			.add(Primitives.JVM_DOUBLE, DOUBLE_PROXY_CLASSNAME)//
			.add(Primitives.JVM_LONG, LONG_PROXY_CLASSNAME).toUnmodifiableMap();

	public static final Map<String, String> PROXY_CONSTRUCTOR_WITH_INITALIZATION = //
			MapBuilder.<String, String>hashMapBuilder()//
					.add(REFERENCE_PROXY_CLASSNAME, REFERENCE_PROXY_CONSTRUCTOR)//
					.add(BOOLEAN_PROXY_CLASSNAME, BOOLEAN_PROXY_CONSTRUCTOR)//
					.add(INTEGER_PROXY_CLASSNAME, INTEGER_PROXY_CONSTRUCTOR)//
					.add(FLOAT_PROXY_CLASSNAME, FLOAT_PROXY_CONSTRUCTOR)//
					.add(DOUBLE_PROXY_CLASSNAME, DOUBLE_PROXY_CONSTRUCTOR)//
					.add(LONG_PROXY_CLASSNAME, LONG_PROXY_CONSTRUCTOR).toUnmodifiableMap();

	private static final Map<String, String> PROXY_SET_VALUE_DESCRIPTOR = //
			MapBuilder.<String, String>hashMapBuilder()//
					.add(REFERENCE_PROXY_CLASSNAME, JVMTypes.OBJECT) //
					.add(INTEGER_PROXY_CLASSNAME, Primitives.JVM_INT)//
					.add(BOOLEAN_PROXY_CLASSNAME, Primitives.JVM_BOOLEAN)//
					.add(FLOAT_PROXY_CLASSNAME, Primitives.JVM_FLOAT)//
					.add(DOUBLE_PROXY_CLASSNAME, Primitives.JVM_DOUBLE)//
					.add(LONG_PROXY_CLASSNAME, Primitives.JVM_LONG).toUnmodifiableMap();

	public static String getProxy(String dataType) {
		if (PRIMITIVE_PROXIES.containsKey(dataType)) {
			return PRIMITIVE_PROXIES.get(dataType);
		}
		return REFERENCE_PROXY;
	}

	public static String getProxyClassname(String dataType) {
		if (PRIMITIVE_PROXIES_CLASSNAME.containsKey(dataType)) {
			return PRIMITIVE_PROXIES_CLASSNAME.get(dataType);
		}
		return REFERENCE_PROXY_CLASSNAME;
	}

	public static String getDefaultInitDescriptor(String proxy) {
		if (INTEGER_PROXY_CLASSNAME.equals(proxy)) {
			return INTEGER_PROXY_DEFAULT_CONSTRUCTOR;
		} else if (REFERENCE_PROXY_CLASSNAME.equals(proxy)) {
			return REFERENCE_PROXY_DEFAULT_CONSTRUCTOR;
		}
		return DEFAULT_PROXY_CONSTRUCTOR;
	}

	public static String getSetValueDescriptor(String proxy) {
		return "(" + PROXY_SET_VALUE_DESCRIPTOR.get(proxy) + ")V";
	}

	public static String getGetValueDescriptor(String dataType) {
		return "()" + (Primitives.isPrimitiveDataType(dataType) ? dataType : JVMTypes.OBJECT);
	}

	public static String toDescriptor(String proxy) {
		return "L" + proxy + ";";
	}

	public static String getValueMethodName(String dataType) {
		switch (dataType) {
		case Primitives.JVM_BYTE:
			return GET_BYTE_VALUE;
		case Primitives.JVM_CHAR:
			return GET_CHAR_VALUE;
		case Primitives.JVM_SHORT:
			return GET_SHORT_VALUE;
		default:
			return GET_VALUE;
		}
	}
}

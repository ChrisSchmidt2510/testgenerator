package org.testgen.agent.classdata.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class JVMTypes {

	public static final String OBJECT = "Ljava/lang/Object;";
	public static final String OBJECT_CLASSNAME = "java/lang/Object";

	public static final String COLLECTION = "Ljava/util/Collection;";
	public static final String LIST = "Ljava/util/List;";
	public static final String LIST_CLASSNAME = "java/util/List";

	public static final String ARRAYLIST_CLASSNAME = "java/util/ArrayList";
	public static final String SET = "Ljava/util/Set;";
	public static final String QUEUE = "Ljava/util/Queue;";
	public static final String DEQUE = "Ljava/util/Deque;";
	public static final String MAP = "Ljava/util/Map;";

	public static final String BOOLEAN_CLASSNAME = "java/lang/Boolean";
	public static final String BYTE_CLASSNAME = "java/lang/Byte";
	public static final String CHAR_CLASSNAME = "java/lang/Character";
	public static final String SHORT_CLASSNAME = "java/lang/Short";
	public static final String INTEGER_CLASSNAME = "java/lang/Integer";
	public static final String FLOAT_CLASSNAME = "java/lang/Float";
	public static final String LONG_CLASSNAME = "java/lang/Long";
	public static final String DOUBLE_CLASSNAME = "java/lang/Double";

	public static final String CLASS = "Ljava/lang/Class;";

	public static final String WRAPPER_CLASSES_FIELD_TYPE = "TYPE";

	public static final String WRAPPER_METHOD_VALUE_OF = "valueOf";

	public static final String BOOLEAN_METHOD_VALUE_OF_DESC = "(Z)Ljava/lang/Boolean;";
	public static final String BYTE_METHOD_VALUE_OF_DESC = "(B)Ljava/lang/Byte;";
	public static final String CHARACTER_METHOD_VALUE_OF_DESC = "(C)Ljava/lang/Character;";
	public static final String SHORT_METHOD_VALUE_OF_DESC = "(S)Ljava/lang/Short;";
	public static final String INTEGER_METHOD_VALUE_OF_DESC = "(I)Ljava/lang/Integer;";
	public static final String FLOAT_METHOD_VALUE_OF_DESC = "(F)Ljava/lang/Float;";
	public static final String DOUBLE_METHOD_VALUE_OF_DESC = "(D)Ljava/lang/Double;";
	public static final String LONG_METHOD_VALUE_OF_DESC = "(J)Ljava/lang/Long;";

	public static final String COLLECTION_METHOD_ADD = "add";
	public static final String COLLECTION_METHOD_ADD_DESC = "(Ljava/lang/Object;)Z";
	public static final List<String> COLLECTION_TYPES = Collections
			.unmodifiableList(Arrays.asList(COLLECTION, LIST, SET, QUEUE, DEQUE, MAP));

	private JVMTypes() {
	}

}

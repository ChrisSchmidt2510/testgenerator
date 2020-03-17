package de.nvg.testgenerator.classdata.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import de.nvg.testgenerator.MapBuilder;

public final class JVMTypes {

	public static final String OBJECT = "Ljava/lang/Object;";

	public static final String COLLECTION = "Ljava/util/Collection;";
	public static final String LIST = "Ljava/util/List;";
	public static final String SET = "Ljava/util/Set;";
	public static final String QUEUE = "Ljava/util/Queue;";
	public static final String MAP = "Ljava/util/Map;";
	public static final String MAP_CLASSNAME = "java/util/Map";

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

	public static final String INTEGER_METHOD_VALUE_OF = "valueOf";
	public static final String INTEGER_METHOD_VALUE_OF_DESC = "(I)Ljava/lang/Integer;";

	public static final String COLLECTION_METHOD_ADD = "add";
	public static final String QUEUE_METHOD_OFFER = "offer";
	public static final String MAP_METHOD_PUT = "put";
	public static final String MAP_METHOD_PUT_DESC = "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;";

	public static final List<String> COLLECTION_TYPES = Collections
			.unmodifiableList(Arrays.asList(COLLECTION, LIST, SET, QUEUE, MAP));

	public static final Map<String, List<String>> COLLECTION_ADD_METHODS = MapBuilder
			.<String, List<String>>hashMapBuilder()//
			.add(COLLECTION, Collections.singletonList(COLLECTION_METHOD_ADD))
			.add(LIST, Collections.singletonList(COLLECTION_METHOD_ADD))
			.add(SET, Collections.singletonList(COLLECTION_METHOD_ADD))
			.add(QUEUE, Arrays.asList(COLLECTION_METHOD_ADD, QUEUE_METHOD_OFFER))
			.add(MAP, Collections.singletonList(MAP_METHOD_PUT)).toUnmodifiableMap();

	private JVMTypes() {
	}

}

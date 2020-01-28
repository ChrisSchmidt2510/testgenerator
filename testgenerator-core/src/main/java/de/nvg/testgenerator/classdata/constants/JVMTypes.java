package de.nvg.testgenerator.classdata.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import de.nvg.testgenerator.MapBuilder;

public class JVMTypes {

	public static final String OBJECT = "Ljava/lang/Object;";

	public static final String COLLECTION = "Ljava/util/Collection;";
	public static final String LIST = "Ljava/util/List;";
	public static final String SET = "Ljava/util/Set;";
	public static final String QUEUE = "Ljava/util/Queue;";
	public static final String MAP = "Ljava/util/Map;";

	public static final String COLLECTIONS = "java.util.Collections";

	public static final String COLLECTION_METHOD_ADD = "add";
	public static final String QUEUE_METHOD_OFFER = "offer";
	public static final String MAP_METHOD_PUT = "put";

	public static final List<String> COLLECTION_TYPES = Collections
			.unmodifiableList(Arrays.asList(COLLECTION, LIST, SET, QUEUE, MAP));

	public static final Map<String, List<String>> COLLECTION_ADD_METHODS = MapBuilder
			.<String, List<String>>hashMapBuilder()//
			.add(COLLECTION, Collections.singletonList(COLLECTION_METHOD_ADD))
			.add(LIST, Collections.singletonList(COLLECTION_METHOD_ADD))
			.add(SET, Collections.singletonList(COLLECTION_METHOD_ADD))
			.add(QUEUE, Arrays.asList(COLLECTION_METHOD_ADD, QUEUE_METHOD_OFFER))
			.add(MAP, Collections.singletonList(MAP_METHOD_PUT)).toUnmodifiableMap();

}

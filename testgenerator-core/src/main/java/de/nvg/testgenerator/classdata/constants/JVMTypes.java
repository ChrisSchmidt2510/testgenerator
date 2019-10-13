package de.nvg.testgenerator.classdata.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JVMTypes {

	public static final String OBJECT = "Ljava/lang/Object;";

	public static final String COLLECTION = "Ljava/util/Collection;";
	public static final String LIST = "Ljava/util/List;";
	public static final String SET = "Ljava/util/Set;";
	public static final String QUEUE = "Ljava/util/Queue;";
	public static final String MAP = "Ljava/util/Map;";

	public static final String COLLECTIONS = "java.util.Collections";

	public static final String COLLECTION_METHOD_ADD = "add";

	public static final List<String> COLLECTION_TYPES = Collections
			.unmodifiableList(Arrays.asList(COLLECTION, LIST, SET, QUEUE, MAP));

}

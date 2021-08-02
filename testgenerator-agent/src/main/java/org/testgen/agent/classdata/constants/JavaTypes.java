package org.testgen.agent.classdata.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.testgen.core.MapBuilder;

public final class JavaTypes {
	public static final String OBJECT = "java.lang.Object";

	public static final String OBJECT_ARRAY = "java.lang.Object[]";

	public static final String OBJECT_METHOD_EQUALS = "equals";
	public static final String OBJECT_METHOD_HASHCODE = "hashCode";
	public static final String OBJECT_METHOD_TO_STRING = "toString";
	public static final String OBJECT_METHOD_FINALIZE = "finalize";
	public static final String OBJECT_METHOD_GET_CLASS = "getClass";
	public static final String OBJECT_METHOD_CLONE = "clone";
	public static final String OBJECT_METHOD_NOTIFY = "notify";
	public static final String OBJECT_METHOD_NOTIFY_ALL = "notifyAll";
	public static final String OBJECT_METHOD_WAIT = "wait";

	public static final List<String> OBJECT_STANDARD_METHODS = Collections.unmodifiableList(//
			Arrays.asList(OBJECT_METHOD_EQUALS, OBJECT_METHOD_HASHCODE, OBJECT_METHOD_FINALIZE, //
					OBJECT_METHOD_TO_STRING, OBJECT_METHOD_GET_CLASS, OBJECT_METHOD_CLONE, OBJECT_METHOD_NOTIFY,
					OBJECT_METHOD_NOTIFY_ALL, OBJECT_METHOD_WAIT));

	public static final String COLLECTION = "java.util.Collection";
	public static final String LIST = "java.util.List";
	public static final String SET = "java.util.Set";
	public static final String MAP = "java.util.Map";
	public static final String QUEUE = "java.util.Queue";
	public static final String DEQUE = "java.util.Deque";

	public static final String STRING = "java.lang.String";
	public static final String CLASS = "java.lang.Class";

	public static final String COLLECTION_METHOD_ADD = "add";
	public static final String COLLECTION_METHOD_ADD_DESC = "(Ljava/lang/Object;)Z";
	public static final String COLLECTION_METHOD_ADD_ALL = "addAll";
	public static final String QUEUE_METHOD_OFFER = "offer";
	public static final String DEQUE_METHOD_ADD_FIRST = "addFirst";
	public static final String DEQUE_METHOD_ADD_LAST = "addLast";
	public static final String DEQUE_METHOD_OFFER_FIRST = "offerFirst";
	public static final String DEQUE_METHOD_PUSH = "push";
	public static final String DEQUE_METHOD_OFFER_LAST = "offerLast";
	public static final String MAP_METHOD_PUT = "put";
	public static final String MAP_METHOD_PUT_ALL = "putAll";
	public static final String MAP_METHOD_PUT_IF_ABSENT = "putIfAbsent";
	public static final String MAP_METHOD_COMPUTE_IF_ABSENT = "computeIfAbsent";

	public static final String COLLECTIONS = "java.util.Collections";

	public static final List<String> COLLECTION_LIST = Collections
			.unmodifiableList(Arrays.asList(COLLECTION, LIST, SET, MAP, QUEUE, DEQUE));

	public static final Map<String, List<String>> COLLECTION_ADD_METHODS = MapBuilder
			.<String, List<String>>hashMapBuilder()//
			.add(COLLECTION, Arrays.asList(COLLECTION_METHOD_ADD, COLLECTION_METHOD_ADD_ALL))
			.add(LIST, Arrays.asList(COLLECTION_METHOD_ADD, COLLECTION_METHOD_ADD_ALL))
			.add(SET, Arrays.asList(COLLECTION_METHOD_ADD, COLLECTION_METHOD_ADD_ALL))
			.add(QUEUE, Arrays.asList(COLLECTION_METHOD_ADD, QUEUE_METHOD_OFFER))
			.add(DEQUE,
					Arrays.asList(COLLECTION_METHOD_ADD, DEQUE_METHOD_ADD_FIRST, DEQUE_METHOD_ADD_LAST,
							COLLECTION_METHOD_ADD_ALL, QUEUE_METHOD_OFFER, DEQUE_METHOD_OFFER_FIRST,
							DEQUE_METHOD_OFFER_LAST, DEQUE_METHOD_PUSH))
			.add(MAP, Arrays.asList(MAP_METHOD_PUT, MAP_METHOD_PUT_ALL, MAP_METHOD_PUT_IF_ABSENT,
					MAP_METHOD_COMPUTE_IF_ABSENT))
			.toUnmodifiableMap();

	public static boolean isArray(String dataType) {
		return dataType.endsWith("[]");
	}

	private JavaTypes() {
	}

}

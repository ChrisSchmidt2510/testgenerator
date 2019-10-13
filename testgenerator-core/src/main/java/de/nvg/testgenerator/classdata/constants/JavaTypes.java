package de.nvg.testgenerator.classdata.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class JavaTypes {

	private JavaTypes() {
	}

	public static final String OBJECT = "java.lang.Object";

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

	public static final List<String> COLLECTIONS = Collections
			.unmodifiableList(Arrays.asList(COLLECTION, LIST, SET, MAP, QUEUE));

}

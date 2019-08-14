package de.nvg.javaagent.classdata.model;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ClassDataStorage {

	private static final ClassDataStorage INSTANCE = new ClassDataStorage();

	private final Map<String, ClassData> classDataMap = new ConcurrentHashMap<>();
	// TODO vlt zu kurz gedacht, sollte aber nur bei parallelen
	// Classloading auftreten
	private final Map<String, Consumer<ClassData>> superClassInitializer = new ConcurrentHashMap<>();

	private ClassDataStorage() {
	}

	public static ClassDataStorage getInstance() {
		return INSTANCE;
	}

	public void addClassData(String className, ClassData classData) {
		Consumer<ClassData> consumer = superClassInitializer.get(className);

		if (consumer != null) {
			consumer.accept(classData);
			superClassInitializer.remove(className);
		}

		classDataMap.put(className, classData);
	}

	public ClassData getClassData(String className) {
		return classDataMap.get(className);
	}

	public void addSuperClassAfterLoading(String className, Consumer<ClassData> consumer) {
		superClassInitializer.put(className, consumer);
	}

	public Set<String> getSuperClassesToLoad() {
		return superClassInitializer.keySet();
	}

	public Map<String, ClassData> getClassDataMap() {
		return Collections.unmodifiableMap(classDataMap);
	}

}

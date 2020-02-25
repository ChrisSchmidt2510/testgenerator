package de.nvg.agent.classdata.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ClassDataStorage {

	private static final ClassDataStorage INSTANCE = new ClassDataStorage();

	private final Map<String, ClassData> classDataMap = new ConcurrentHashMap<>();

	private final Set<String> superClassesToLoad = new HashSet<>();

	private ClassDataStorage() {
	}

	public static ClassDataStorage getInstance() {
		return INSTANCE;
	}

	public void addClassData(String className, ClassData classData) {
		classDataMap.put(className, classData);
	}

	public ClassData getClassData(String className) {
		return classDataMap.get(className);
	}

	public Map<String, ClassData> getClassDataMap() {
		return Collections.unmodifiableMap(classDataMap);
	}

	public void addSuperclassToLoad(String superClass) {
		superClassesToLoad.add(superClass);
	}

	public boolean containsSuperclassToLoad(String superClass) {
		return superClassesToLoad.contains(superClass);
	}

	public void removeSuperclassToLoad(String superClass) {
		superClassesToLoad.remove(superClass);
	}

}

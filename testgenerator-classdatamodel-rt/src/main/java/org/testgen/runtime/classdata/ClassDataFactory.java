package org.testgen.runtime.classdata;

import java.util.HashMap;
import java.util.Map;

import org.testgen.core.MethodHandles;
import org.testgen.core.TestgeneratorConstants;
import org.testgen.core.logging.LogManager;
import org.testgen.core.logging.Logger;
import org.testgen.runtime.classdata.model.ClassData;

public final class ClassDataFactory {
	private final Map<Class<?>, Class<?>> classDataCreation = new HashMap<>();
	private final Map<Class<?>, ClassData> classDataMap = new HashMap<>();

	private static final ClassDataFactory INSTANCE = new ClassDataFactory();

	private static final Logger LOGGER = LogManager.getLogger(org.testgen.runtime.classdata.ClassDataFactory.class);

	private ClassDataFactory() {
	}

	public static ClassDataFactory getInstance() {
		return INSTANCE;
	}

	public void register(Class<?> provider, Class<?> container) {
		classDataCreation.put(provider, container);
	}

	public ClassData getClassData(Class<?> provider) {
		if (classDataMap.containsKey(provider)) {
			return classDataMap.get(provider);
		} else if (classDataCreation.containsKey(provider)) {
			Class<?> container = classDataCreation.get(provider);

			try {
				Object classDataContainer = container.newInstance();
				ClassData classData = MethodHandles.getFieldValue(classDataContainer,
						TestgeneratorConstants.FIELDNAME_CLASS_DATA);

				classDataMap.put(provider, classData);

				return classData;
			} catch (InstantiationException | IllegalAccessException e) {
				LOGGER.error("cant create ClassData for Class " + provider, e);
			}
		}

		return null;
	}
}

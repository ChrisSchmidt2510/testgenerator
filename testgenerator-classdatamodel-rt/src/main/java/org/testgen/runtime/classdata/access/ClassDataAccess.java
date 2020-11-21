package org.testgen.runtime.classdata.access;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import org.testgen.core.TestgeneratorConstants;
import org.testgen.runtime.classdata.ClassDataHolder;
import org.testgen.runtime.classdata.model.ClassData;

public final class ClassDataAccess {

	private ClassDataAccess() {
	}

	public static ClassData getClassData(Class<?> classDataClass) {
		if (ClassDataHolder.class.isAssignableFrom(classDataClass)) {
			try {
				return (ClassData) MethodHandles.lookup().findStatic(classDataClass,
						TestgeneratorConstants.CLASS_DATA_METHOD_GET_CLASS_DATA, MethodType.methodType(ClassData.class))
						.invokeExact();
			} catch (Throwable e) {
				throw new RuntimeException(String.format("cant invoke ClassData for Class %s", classDataClass), e);
			}
		} else {
			throw new IllegalArgumentException(
					String.format("Class %s has to implement %s", classDataClass, ClassDataHolder.class));
		}
	}

}

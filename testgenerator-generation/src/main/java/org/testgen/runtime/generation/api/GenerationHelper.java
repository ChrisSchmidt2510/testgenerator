package org.testgen.runtime.generation.api;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;

import org.testgen.core.MethodHandles;
import org.testgen.core.TestgeneratorConstants;
import org.testgen.runtime.classdata.access.ClassDataAccess;
import org.testgen.runtime.classdata.model.ClassData;
import org.testgen.runtime.classdata.model.FieldData;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;

public final class GenerationHelper {

	private GenerationHelper() {
	}

	public static ClassData getClassData(Object reference) {
		return ClassDataAccess.getClassData(reference.getClass());
	}

	public static Set<FieldData> getCalledFields(Object reference) {
		return MethodHandles.getFieldValue(reference, TestgeneratorConstants.FIELDNAME_CALLED_FIELDS);
	}

	public static SignatureType mapGenericTypeToSignature(Type type) {
		if (type instanceof ParameterizedType) {
			ParameterizedType genericType = (ParameterizedType) type;

			SignatureType mainType = mapTypeToSignature(genericType.getRawType());

			if (mainType != null) {
				for (Type innerType : genericType.getActualTypeArguments()) {
					SignatureType subType = mapGenericTypeToSignature(innerType);

					if (subType != null) {
						mainType.addSubType(subType);
					} else {
						return null;
					}
				}
			}

			return mainType;

		} else {
			return mapTypeToSignature(type);
		}
	}

	private static SignatureType mapTypeToSignature(Type type) {
		if (type instanceof Class<?>) {
			return new SignatureType((Class<?>) type);
		}

		return null;
	}

}

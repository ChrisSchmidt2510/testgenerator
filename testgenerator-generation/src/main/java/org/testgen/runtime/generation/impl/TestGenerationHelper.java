package org.testgen.runtime.generation.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;

import org.testgen.core.MethodHandles;
import org.testgen.core.TestgeneratorConstants;
import org.testgen.runtime.classdata.ClassDataFactory;
import org.testgen.runtime.classdata.model.ClassData;
import org.testgen.runtime.classdata.model.FieldData;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

public final class TestGenerationHelper {

	private TestGenerationHelper() {
	}

	public static ClassData getClassData(Object reference) {
		return ClassDataFactory.getInstance().getClassData(reference.getClass());
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

	public static TypeName getParameterizedTypeName(SignatureType signature) {

		if (signature.isSimpleSignature()) {
			return TypeName.get(signature.getType());

		} else {
			TypeName[] subTypes = new TypeName[signature.getSubTypes().size()];

			for (int i = 0; i < signature.getSubTypes().size(); i++) {
				SignatureType subSignature = signature.getSubTypes().get(i);

				subTypes[i] = getParameterizedTypeName(subSignature);
			}

			return ParameterizedTypeName.get(ClassName.get(signature.getType()), subTypes);
		}
	}

	private static SignatureType mapTypeToSignature(Type type) {
		if (type instanceof Class<?>) {
			return new SignatureType((Class<?>) type);
		}

		return null;
	}
}

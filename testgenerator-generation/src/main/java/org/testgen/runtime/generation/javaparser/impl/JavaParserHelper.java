package org.testgen.runtime.generation.javaparser.impl;

import java.util.function.Consumer;

import org.testgen.runtime.classdata.model.descriptor.SignatureType;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.type.ArrayType;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;

public class JavaParserHelper {

	private JavaParserHelper() {
	}

	public static IntegerLiteralExpr mapIntegerExpression(int value) {
		return new IntegerLiteralExpr(Integer.toString(value));
	}

	public static Type generateSignature(SignatureType signature, Consumer<Class<?>> importCallbackHandler) {

		Class<?> type = signature.getType();

		if (type.isArray())
			return generateArrayType(signature.getType(), signature, importCallbackHandler);

		else if (signature.isSimpleSignature()) {
			importCallbackHandler.accept(type);

			return new ClassOrInterfaceType(null, type.getSimpleName());

		} else {
			ClassOrInterfaceType baseType = new ClassOrInterfaceType(null, type.getSimpleName());

			importCallbackHandler.accept(type);

			NodeList<Type> typeArguments = new NodeList<>();

			for (SignatureType subSignature : signature.getSubTypes()) {
				typeArguments.add(generateSignature(subSignature, importCallbackHandler));
			}

			baseType.setTypeArguments(typeArguments);
			return baseType;
		}
	}

	public static Type generateArrayType(Class<?> arrayType, SignatureType signature,
			Consumer<Class<?>> importCallbackHandler) {

		if (arrayType.isArray())
			return new ArrayType(generateArrayType(arrayType.getComponentType(), signature, importCallbackHandler));

		if (arrayType.isPrimitive())
			return getPrimitiveType(arrayType);

		importCallbackHandler.accept(arrayType);

		ClassOrInterfaceType componentType = new ClassOrInterfaceType(null, arrayType.getSimpleName());

		NodeList<Type> typeArguments = new NodeList<>();

		for (SignatureType subType : signature.getSubTypes()) {
			typeArguments.add(generateSignature(subType, importCallbackHandler));
		}

		componentType.setTypeArguments(typeArguments);

		return componentType;
	}

	public static PrimitiveType getPrimitiveType(Class<?> primitive) {

		if (Boolean.TYPE.equals(primitive))
			return PrimitiveType.booleanType();

		else if (Character.TYPE.equals(primitive))
			return PrimitiveType.charType();

		else if (Byte.TYPE.equals(primitive))
			return PrimitiveType.byteType();

		else if (Short.TYPE.equals(primitive))
			return PrimitiveType.shortType();

		else if (Integer.TYPE.equals(primitive))
			return PrimitiveType.intType();

		else if (Float.TYPE.equals(primitive))
			return PrimitiveType.floatType();

		else if (Double.TYPE.equals(primitive))
			return PrimitiveType.doubleType();

		else if (Long.TYPE.equals(primitive))
			return PrimitiveType.longType();

		throw new IllegalArgumentException("invalid primitive type" + primitive);
	}

}

package org.testgen.runtime.generation.javaparser.impl;

import org.testgen.runtime.classdata.model.descriptor.SignatureType;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;

public class JavaParserHelper {

	private JavaParserHelper() {
	}

	public static ClassOrInterfaceType getClassOrInterfaceType(Class<?> type) {
		if (type.isMemberClass()) {
			return new ClassOrInterfaceType(getClassOrInterfaceType(type.getDeclaringClass()), type.getSimpleName());
		}

		return new ClassOrInterfaceType(null, type.getSimpleName());
	}

	public static IntegerLiteralExpr mapIntegerExpression(int value) {
		return new IntegerLiteralExpr(Integer.toString(value));
	}

	public static ClassOrInterfaceType generateGenericSignature(SignatureType signature) {

		if (signature.isSimpleSignature())
			return new ClassOrInterfaceType(null, signature.getType().getSimpleName());

		else {
			ClassOrInterfaceType baseType = new ClassOrInterfaceType(null, signature.getType().getSimpleName());

			NodeList<Type> typeArguments = new NodeList<>();

			for (SignatureType subSignature : signature.getSubTypes()) {
				typeArguments.add(generateGenericSignature(subSignature));
			}

			baseType.setTypeArguments(typeArguments);
			return baseType;
		}
	}

}

package org.testgen.runtime.generation.javaparser.impl;

import org.testgen.runtime.valuetracker.blueprint.BluePrint;

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

	public static Type mapToType(BluePrint bluePrint) {
		ClassOrInterfaceType type = new ClassOrInterfaceType(null, bluePrint.getSimpleClassName());

		return type.isBoxedType() ? type.toUnboxedType() : type;
	}

}

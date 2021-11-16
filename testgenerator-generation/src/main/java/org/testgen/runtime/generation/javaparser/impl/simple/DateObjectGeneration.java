package org.testgen.runtime.generation.javaparser.impl.simple;

import java.util.Date;

import org.testgen.logging.LogManager;
import org.testgen.logging.Logger;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.JavaDateBluePrint;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BinaryExpr.Operator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

public class DateObjectGeneration extends BasicSimpleObjectGeneration {

	private static final Logger LOGGER = LogManager.getLogger(DateObjectGeneration.class);

	@Override
	public boolean canGenerateBluePrint(SimpleBluePrint<?> bluePrint) {
		return bluePrint instanceof JavaDateBluePrint;
	}

	@Override
	public Expression createInlineExpression(SimpleBluePrint<?> bluePrint) {
		LOGGER.debug("create Inline SimpleBluePrint " + bluePrint);

		if (!bluePrint.isNotBuild())
			LOGGER.warn("you try to create a already builded SimpleBluePrint " + bluePrint);

		Class<?> type = bluePrint.getReferenceClass();

		importCallBackHandler.accept(type);

		JavaDateBluePrint date = (JavaDateBluePrint) bluePrint;

		NodeList<Expression> arguments = new NodeList<>();

		arguments.add(new BinaryExpr(mapIntegerExpression(date.getYear() + 1900), mapIntegerExpression(1900),
				Operator.MINUS));
		arguments.add(
				new BinaryExpr(mapIntegerExpression(date.getMonth() + 1), mapIntegerExpression(1), Operator.MINUS));
		arguments.add(mapIntegerExpression(date.getDay()));

		if (Date.class.equals(date.getReferenceClass())) {
			if (date.getHour() > 0) {
				arguments.add(mapIntegerExpression(date.getHour()));
				arguments.add(mapIntegerExpression(date.getMinute()));

				if (date.getSecond() > 0) {
					arguments.add(mapIntegerExpression(date.getSecond()));
				}
			}
		}

		return new ObjectCreationExpr(null, new ClassOrInterfaceType(null, type.getSimpleName()), arguments);
	}

}

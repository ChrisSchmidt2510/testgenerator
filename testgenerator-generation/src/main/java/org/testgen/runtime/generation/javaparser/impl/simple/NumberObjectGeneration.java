package org.testgen.runtime.generation.javaparser.impl.simple;

import java.math.BigDecimal;

import org.testgen.logging.LogManager;
import org.testgen.logging.Logger;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.NumberBluePrint;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.type.PrimitiveType;

public class NumberObjectGeneration extends BasicSimpleObjectGeneration {

	private static final Logger LOGGER = LogManager.getLogger(NumberObjectGeneration.class);

	@Override
	public boolean canGenerateBluePrint(SimpleBluePrint<?> bluePrint) {
		return bluePrint instanceof NumberBluePrint;
	}

	@Override
	public Expression createInlineObject(SimpleBluePrint<?> bluePrint) {
		LOGGER.debug("create Inline SimpleBluePrint " + bluePrint);

		if (!bluePrint.isNotBuild())
			LOGGER.warning("you try to create a already builded SimpleBluePrint " + bluePrint);

		NumberBluePrint number = (NumberBluePrint) bluePrint;

		Class<?> referenceClass = number.getReferenceClass();

		if (Integer.class.equals(referenceClass)) {
			return new IntegerLiteralExpr(number.valueCreation());

		} else if (Byte.class.equals(referenceClass)) {
			return new CastExpr(PrimitiveType.byteType(), new IntegerLiteralExpr(number.valueCreation()));

		} else if (Short.class.equals(referenceClass)) {
			return new CastExpr(PrimitiveType.shortType(), new IntegerLiteralExpr(number.valueCreation()));

		} else if (Float.class.equals(referenceClass) || Double.class.equals(referenceClass)) {
			return new DoubleLiteralExpr(number.valueCreation());

		} else if (Long.class.equals(referenceClass)) {
			return new LongLiteralExpr(number.valueCreation());

		} else if (BigDecimal.class.equals(referenceClass)) {
			importCallBackHandler.accept(BigDecimal.class);

			return new MethodCallExpr(
					new MethodCallExpr(new NameExpr(BigDecimal.class.getSimpleName()), "valueOf",
							NodeList.nodeList(new DoubleLiteralExpr(number.valueCreation()))),
					"setScale", NodeList.nodeList(mapIntegerExpression(number.getBigDecimalScale())));
		}

		throw new IllegalArgumentException("invalid NumberBluePrint " + bluePrint);
	}

}

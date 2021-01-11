package org.testgen.runtime.generation.javaparser.impl.simple;

import java.util.GregorianCalendar;

import org.testgen.logging.LogManager;
import org.testgen.logging.Logger;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.CalendarBluePrint;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BinaryExpr.Operator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

public class CalendarObjectGeneration extends BasicSimpleObjectGeneration {

	private static final Logger LOGGER = LogManager.getLogger(CalendarObjectGeneration.class);

	@Override
	public boolean canGenerateBluePrint(SimpleBluePrint<?> bluePrint) {
		return bluePrint instanceof CalendarBluePrint;
	}

	@Override
	public Expression createInlineObject(SimpleBluePrint<?> bluePrint) {
		LOGGER.debug("create Inline SimpleBluePrint " + bluePrint);

		if (!bluePrint.isNotBuild())
			LOGGER.warning("you try to create a already builded SimpleBluePrint " + bluePrint);

		if (GregorianCalendar.class.equals(bluePrint.getReferenceClass())) {
			importCallBackHandler.accept(bluePrint.getReferenceClass());

			CalendarBluePrint calendar = (CalendarBluePrint) bluePrint;

			NodeList<Expression> arguments = new NodeList<>();

			arguments.add(mapIntegerExpression(calendar.getYear()));
			arguments.add(new BinaryExpr(mapIntegerExpression(calendar.getMonth() + 1), mapIntegerExpression(1),
					Operator.MINUS));
			arguments.add(mapIntegerExpression(calendar.getDay()));

			if (calendar.getHour() > 0) {
				arguments.add(mapIntegerExpression(calendar.getHour()));
				arguments.add(mapIntegerExpression(calendar.getMinute()));

				if (calendar.getSecond() > 0) {
					arguments.add(mapIntegerExpression(calendar.getSecond()));
				}
			}

			return new ObjectCreationExpr(null, new ClassOrInterfaceType(null, GregorianCalendar.class.getSimpleName()),
					arguments);
		}

		throw new IllegalArgumentException("cant create inlines instance for " + bluePrint.getReferenceClass());
	}

}

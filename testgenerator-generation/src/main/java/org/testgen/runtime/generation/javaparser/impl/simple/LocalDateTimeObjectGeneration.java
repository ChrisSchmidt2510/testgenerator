package org.testgen.runtime.generation.javaparser.impl.simple;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;

import org.testgen.logging.LogManager;
import org.testgen.logging.Logger;
import org.testgen.runtime.valuetracker.blueprint.DateBluePrint;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.TimeBluePrint;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.LocalDateBluePrint;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.LocalDateTimeBluePrint;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.LocalTimeBluePrint;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;

public class LocalDateTimeObjectGeneration extends DefaultSimpleObjectGeneration {

	private static final Logger LOGGER = LogManager.getLogger(LocalDateTimeObjectGeneration.class);

	@Override
	public boolean canGenerateBluePrint(SimpleBluePrint<?> bluePrint) {
		return bluePrint instanceof LocalDateBluePrint || bluePrint instanceof LocalTimeBluePrint
				|| bluePrint instanceof LocalDateTimeBluePrint;
	}

	@Override
	public Expression createInlineObject(SimpleBluePrint<?> bluePrint) {
		LOGGER.debug("create Inline SimpleBluePrint " + bluePrint);

		if (!bluePrint.isNotBuild())
			LOGGER.warning("you try to create a already builded SimpleBluePrint " + bluePrint);

		if (bluePrint instanceof LocalDateBluePrint) {
			return mapLocalDate((DateBluePrint) bluePrint);
		} else if (bluePrint instanceof LocalTimeBluePrint) {
			return mapLocalTime((TimeBluePrint) bluePrint);
		} else
			return mapLocalDateTime((LocalDateTimeBluePrint) bluePrint);
	}

	private Expression mapLocalDate(DateBluePrint dateBluePrint) {
		importCallBackHandler.accept(LocalDate.class);
		importCallBackHandler.accept(Month.class);

		NodeList<Expression> arguments = new NodeList<>();

		arguments.add(mapIntegerExpression(dateBluePrint.getYear()));
		arguments.add(new FieldAccessExpr(new NameExpr(Month.class.getSimpleName()),
				Month.of(dateBluePrint.getMonth()).name()));
		arguments.add(mapIntegerExpression(dateBluePrint.getDay()));

		return new MethodCallExpr(new NameExpr(LocalDate.class.getSimpleName()), "of", arguments);
	}

	private Expression mapLocalTime(TimeBluePrint timeBluePrint) {
		importCallBackHandler.accept(LocalTime.class);

		NodeList<Expression> arguments = new NodeList<>();

		arguments.add(mapIntegerExpression(timeBluePrint.getHour()));
		arguments.add(mapIntegerExpression(timeBluePrint.getMinute()));

		if (timeBluePrint.getSecond() != 0)
			arguments.add(mapIntegerExpression(timeBluePrint.getSecond()));

		return new MethodCallExpr(new NameExpr(LocalTime.class.getSimpleName()), "of", arguments);
	}

	private Expression mapLocalDateTime(LocalDateTimeBluePrint lcd) {
		importCallBackHandler.accept(LocalDateTime.class);

		NodeList<Expression> arguments = new NodeList<>();
		arguments.add(mapLocalDate(lcd));
		arguments.add(mapLocalTime(lcd));

		return new MethodCallExpr(new NameExpr(LocalDateTime.class.getSimpleName()), "of", arguments);
	}

}

package org.testgen.runtime.generation.javaparser.impl.simple;

import org.testgen.logging.LogManager;
import org.testgen.logging.Logger;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.StringBluePrint;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.StringLiteralExpr;

public class StringObjectGeneration extends BasicSimpleObjectGeneration {

	private static final Logger LOGGER = LogManager.getLogger(StringObjectGeneration.class);

	@Override
	public boolean canGenerateBluePrint(SimpleBluePrint<?> bluePrint) {
		return bluePrint instanceof StringBluePrint;
	}

	@Override
	public Expression createInlineExpression(SimpleBluePrint<?> bluePrint) {
		LOGGER.debug("create Inline SimpleBluePrint " + bluePrint);

		if (!bluePrint.isNotBuild())
			LOGGER.warning("you try to create a already builded SimpleBluePrint " + bluePrint);

		return new StringLiteralExpr(bluePrint.valueCreation());
	}

}

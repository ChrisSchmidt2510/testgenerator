package org.testgen.runtime.generation.javaparser.impl.simple;

import org.testgen.logging.LogManager;
import org.testgen.logging.Logger;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.BooleanBluePrint;

import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;

public class BooleanObjectGeneration extends DefaultSimpleObjectGeneration {

	private static final Logger LOGGER = LogManager.getLogger(BooleanObjectGeneration.class);

	@Override
	public boolean canGenerateBluePrint(SimpleBluePrint<?> bluePrint) {
		return bluePrint instanceof BooleanBluePrint;
	}

	@Override
	public Expression createInlineObject(SimpleBluePrint<?> bluePrint) {
		LOGGER.debug("create Inline SimpleBluePrint " + bluePrint);

		if (!bluePrint.isNotBuild())
			LOGGER.warning("you try to create a already builded SimpleBluePrint " + bluePrint);

		return new BooleanLiteralExpr(Boolean.parseBoolean(bluePrint.valueCreation()));
	}
}

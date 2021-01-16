package org.testgen.runtime.generation.javaparser.impl.simple;

import org.testgen.logging.LogManager;
import org.testgen.logging.Logger;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.CharacterBluePrint;

import com.github.javaparser.ast.expr.CharLiteralExpr;
import com.github.javaparser.ast.expr.Expression;

public class CharacterObjectGeneration extends BasicSimpleObjectGeneration {

	private static final Logger LOGGER = LogManager.getLogger(CharacterObjectGeneration.class);

	@Override
	public boolean canGenerateBluePrint(SimpleBluePrint<?> bluePrint) {
		return bluePrint instanceof CharacterBluePrint;
	}

	@Override
	public Expression createInlineExpression(SimpleBluePrint<?> bluePrint) {
		LOGGER.debug("create Inline SimpleBluePrint " + bluePrint);

		if (!bluePrint.isNotBuild())
			LOGGER.warning("you try to create a already builded SimpleBluePrint " + bluePrint);

		return new CharLiteralExpr(bluePrint.valueCreation());
	}

}

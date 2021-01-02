package org.testgen.runtime.generation.javaparser.impl.simple;

import org.testgen.logging.LogManager;
import org.testgen.logging.Logger;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.EnumBluePrint;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;

public class EnumObjectGeneration extends DefaultSimpleObjectGeneration {

	private static final Logger LOGGER = LogManager.getLogger(EnumObjectGeneration.class);

	@Override
	public boolean canGenerateBluePrint(SimpleBluePrint<?> bluePrint) {
		return bluePrint instanceof EnumBluePrint;
	}

	@Override
	public Expression createInlineObject(SimpleBluePrint<?> bluePrint) {
		LOGGER.debug("create Inline SimpleBluePrint " + bluePrint);

		if (!bluePrint.isNotBuild())
			LOGGER.warning("you try to create a already builded SimpleBluePrint " + bluePrint);

		importCallBackHandler.accept(bluePrint.getReferenceClass());

		return new FieldAccessExpr(new NameExpr(bluePrint.getReferenceClass().getSimpleName()),
				bluePrint.valueCreation());
	}

}

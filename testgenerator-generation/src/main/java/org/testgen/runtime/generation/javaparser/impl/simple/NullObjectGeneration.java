package org.testgen.runtime.generation.javaparser.impl.simple;

import org.testgen.logging.LogManager;
import org.testgen.logging.Logger;
import org.testgen.runtime.generation.api.naming.NamingService;
import org.testgen.runtime.generation.api.simple.SimpleObjectGeneration;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.NullBluePrint;

import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

public class NullObjectGeneration
		implements SimpleObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> {

	private static final Logger LOGGER = LogManager.getLogger(NullObjectGeneration.class);

	private NamingService<BlockStmt> namingService = getNamingService();

	@Override
	public boolean canGenerateBluePrint(SimpleBluePrint<?> bluePrint) {
		return bluePrint instanceof NullBluePrint;
	}

	@Override
	public void createField(ClassOrInterfaceDeclaration compilationUnit, SimpleBluePrint<?> bluePrint,
			boolean withInitalizer) {
		LOGGER.error("cant define explicit type for null -> Object is used");

		String name = namingService.getFieldName(bluePrint);

		FieldDeclaration field = withInitalizer
				? compilationUnit.addFieldWithInitializer(Object.class, name, new NullLiteralExpr(), Keyword.PRIVATE)
				: compilationUnit.addField(Object.class, name, Keyword.PRIVATE);

		field.setJavadocComment("TODO set correct Type for " + name);

		bluePrint.setBuild();
	}

	@Override
	public void createObject(BlockStmt statementTree, SimpleBluePrint<?> bluePrint, boolean isField) {
		if (!isField)
			LOGGER.error("cant define explicit type for null -> Object is used");

		String name = isField ? namingService.getFieldName(bluePrint)
				: namingService.getLocalName(statementTree, bluePrint);

		Expression initalizer = new NullLiteralExpr();

		Expression objectCreation = isField
				? new AssignExpr(new FieldAccessExpr(new ThisExpr(), name), initalizer, AssignExpr.Operator.ASSIGN)
				: new VariableDeclarationExpr(
						new VariableDeclarator(new ClassOrInterfaceType(null, Object.class.getSimpleName()), //
								name, initalizer));

		objectCreation.setLineComment("TODO set correct Type for " + name);

		statementTree.addStatement(objectCreation);

		bluePrint.setBuild();
	}

	@Override
	public Expression createInlineObject(SimpleBluePrint<?> bluePrint) {
		return new NullLiteralExpr();
	}

}

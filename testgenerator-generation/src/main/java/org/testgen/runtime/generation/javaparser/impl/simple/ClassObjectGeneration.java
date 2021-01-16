package org.testgen.runtime.generation.javaparser.impl.simple;

import java.util.function.Consumer;

import org.testgen.logging.LogManager;
import org.testgen.logging.Logger;
import org.testgen.runtime.generation.api.naming.NamingService;
import org.testgen.runtime.generation.api.simple.SimpleObjectGeneration;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.ClassBluePrint;

import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.WildcardType;

public class ClassObjectGeneration
		implements SimpleObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> {

	private NamingService<BlockStmt> namingService = getNamingService();

	protected Consumer<Class<?>> importCallBackHandler = getImportCallBackHandler();

	private static final Logger LOGGER = LogManager.getLogger(ClassObjectGeneration.class);

	@Override
	public boolean canGenerateBluePrint(SimpleBluePrint<?> bluePrint) {
		return bluePrint instanceof ClassBluePrint;
	}

	@Override
	public void createField(ClassOrInterfaceDeclaration compilationUnit, SimpleBluePrint<?> bluePrint,
			boolean withInitalizer) {
		String name = namingService.getFieldName(bluePrint);

		ClassOrInterfaceType type = getClassType();

		if (withInitalizer)
			compilationUnit.addFieldWithInitializer(type, name, createInlineExpression(bluePrint), Keyword.PRIVATE);
		else
			compilationUnit.addField(type, name, Keyword.PRIVATE);

		bluePrint.setBuild();

	}

	@Override
	public void createObject(BlockStmt statementTree, SimpleBluePrint<?> bluePrint, boolean isField) {
		String name = isField ? namingService.getFieldName(bluePrint)
				: namingService.getLocalName(statementTree, bluePrint);

		Expression initalizer = createInlineExpression(bluePrint);

		ClassOrInterfaceType type = new ClassOrInterfaceType(null, Class.class.getSimpleName());
		type.setTypeArguments(new WildcardType());

		Expression objectCreation = isField
				? new AssignExpr(new FieldAccessExpr(new ThisExpr(), name), initalizer, AssignExpr.Operator.ASSIGN)
				: new VariableDeclarationExpr(new VariableDeclarator(getClassType(), //
						name, initalizer));

		statementTree.addStatement(objectCreation);

		bluePrint.setBuild();
	}

	@Override
	public Expression createInlineExpression(SimpleBluePrint<?> bluePrint) {
		LOGGER.debug("create Inline SimpleBluePrint " + bluePrint);

		if (!bluePrint.isNotBuild())
			LOGGER.warning("you try to create a already builded SimpleBluePrint " + bluePrint);

		importCallBackHandler.accept(bluePrint.getReferenceClass());

		return new ClassExpr(new ClassOrInterfaceType(null, bluePrint.valueCreation()));
	}

	private static ClassOrInterfaceType getClassType() {
		ClassOrInterfaceType type = new ClassOrInterfaceType(null, Class.class.getSimpleName());
		type.setTypeArguments(new WildcardType());

		return type;
	}

}

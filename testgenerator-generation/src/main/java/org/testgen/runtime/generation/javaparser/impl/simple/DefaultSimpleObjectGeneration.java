package org.testgen.runtime.generation.javaparser.impl.simple;

import java.util.function.Consumer;

import org.testgen.runtime.generation.api.naming.NamingService;
import org.testgen.runtime.generation.api.simple.SimpleObjectGeneration;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;

import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;

public abstract class DefaultSimpleObjectGeneration
		implements SimpleObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> {

	private NamingService<BlockStmt> namingService = getNamingService();

	protected Consumer<Class<?>> importCallBackHandler = getImportCallBackHandler();

	@Override
	public void createField(ClassOrInterfaceDeclaration compilationUnit, SimpleBluePrint<?> bluePrint,
			boolean withInitalizer) {

		String name = namingService.getFieldName(bluePrint);

		Type type = mapToType(bluePrint);

		if (withInitalizer)
			compilationUnit.addFieldWithInitializer(type, name, createInlineObject(bluePrint), Keyword.PRIVATE);
		else
			compilationUnit.addField(type, name, Keyword.PRIVATE);

		bluePrint.setBuild();
	}

	@Override
	public void createObject(BlockStmt statementTree, SimpleBluePrint<?> bluePrint, boolean isField) {
		String name = isField ? namingService.getFieldName(bluePrint)
				: namingService.getLocalName(statementTree, bluePrint);

		Expression initalizer = createInlineObject(bluePrint);

		Expression objectCreation = isField
				? new AssignExpr(new FieldAccessExpr(new ThisExpr(), name), initalizer, AssignExpr.Operator.ASSIGN)
				: new VariableDeclarationExpr(new VariableDeclarator(mapToType(bluePrint), //
						name, initalizer));

		statementTree.addStatement(objectCreation);

		bluePrint.setBuild();
	}

	protected Type mapToType(BluePrint bluePrint) {
		ClassOrInterfaceType type = new ClassOrInterfaceType(null, bluePrint.getSimpleClassName());

		return type.isBoxedType() ? type.toUnboxedType() : type;
	}

	protected IntegerLiteralExpr mapIntegerExpression(int value) {
		return new IntegerLiteralExpr(Integer.toString(value));
	}

}

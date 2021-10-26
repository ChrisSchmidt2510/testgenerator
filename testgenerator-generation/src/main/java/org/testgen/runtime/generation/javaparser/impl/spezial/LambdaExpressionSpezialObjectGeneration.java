package org.testgen.runtime.generation.javaparser.impl.spezial;

import java.util.Arrays;
import java.util.List;

import org.testgen.logging.LogManager;
import org.testgen.logging.Logger;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;
import org.testgen.runtime.generation.javaparser.impl.JavaParserHelper;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.complextypes.LambdaExpressionBluePrint;

import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.AssignExpr.Operator;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.EmptyStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.type.IntersectionType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;

public class LambdaExpressionSpezialObjectGeneration extends BasicSpezialObjectGeneration<LambdaExpressionBluePrint> {

	private static final Logger LOGGER = LogManager.getLogger(LambdaExpressionSpezialObjectGeneration.class);

	@Override
	public boolean canGenerateBluePrint(BluePrint bluePrint) {
		return bluePrint instanceof LambdaExpressionBluePrint;
	}

	@Override
	public void createField(ClassOrInterfaceDeclaration compilationUnit, LambdaExpressionBluePrint bluePrint,
			SignatureType signature) {
		String name = namingService.getFieldName(bluePrint);

		if (signature != null) {
			Type type = JavaParserHelper.generateSignature(signature, importCallBackHandler);
			compilationUnit.addField(type, name, Keyword.PRIVATE);

		} else {
			Class<?> type = bluePrint.getInterfaceClass();
			compilationUnit.addField(type, name, Keyword.PRIVATE);

			importCallBackHandler.accept(type);
		}
	}

	@Override
	public void createObject(BlockStmt codeBlock, LambdaExpressionBluePrint bluePrint, SignatureType signature,
			boolean isField) {
		LOGGER.debug("start generation of lambda expression " + bluePrint);

		createLocals(codeBlock, bluePrint.getPreExecuteBluePrints());

		Expression lambdaValue = new NullLiteralExpr();

		Class<?>[] interfaces = bluePrint.getReferenceClass().getInterfaces();

		if (interfaces.length > 1) {
			NodeList<ReferenceType> lambdaTypes = Arrays.stream(interfaces).map(this::getType)
					.collect(NodeList.toNodeList());

			lambdaValue = new CastExpr(new IntersectionType(lambdaTypes), lambdaValue);
		}

		Expression lambdaExpr = isField
				? new AssignExpr(new FieldAccessExpr(new ThisExpr(), namingService.getFieldName(bluePrint)),
						lambdaValue, Operator.ASSIGN)
				: new VariableDeclarationExpr(new VariableDeclarator(
						signature != null ? JavaParserHelper.generateSignature(signature, importCallBackHandler)
								: getType(bluePrint.getInterfaceClass()),
						namingService.getLocalName(codeBlock, bluePrint), lambdaValue));
		ExpressionStmt stmt = new ExpressionStmt(lambdaExpr);
		stmt.setLineComment("TODO add initialization");

		codeBlock.addStatement(stmt);
		
		bluePrint.setBuild();
	}

	private void createLocals(BlockStmt codeBlock, List<BluePrint> locals) {

		for (BluePrint local : locals) {
			boolean isField = namingService.existsField(local);

			boolean notBuild = local.isNotBuild();

			LOGGER.debug("create local " + local);

			if (notBuild) {

				if (local.isComplexBluePrint())
					createComplexObject(codeBlock, local.castToComplexBluePrint());

				else if (local.isCollectionBluePrint())
					collectionGenerationFactory.createCollection(codeBlock, local.castToCollectionBluePrint(), null,
							isField);

				else if (local.isArrayBluePrint())
					arrayGeneration.createArray(codeBlock, local.castToArrayBluePrint(), null, isField);

				else if (local.isSpezialBluePrint())
					spezialGenerationFactory.createObject(codeBlock, local, null, isField);

				else if (local.isSimpleBluePrint())
					simpleGenerationFactory.createObject(codeBlock, local.castToSimpleBluePrint(), isField);
			} else {
				EmptyStmt stmt = new EmptyStmt();
				stmt.setLineComment("already created local "
						+ (isField ? namingService.getFieldName(local) : namingService.getLocalName(codeBlock, local)));
				codeBlock.addStatement(stmt);
			}
		}
	}

}

package org.testgen.runtime.generation.javaparser.impl.spezial;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map.Entry;

import org.testgen.logging.LogManager;
import org.testgen.logging.Logger;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;
import org.testgen.runtime.generation.api.GenerationHelper;
import org.testgen.runtime.generation.javaparser.impl.JavaParserHelper;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.complextypes.ProxyBluePrint;

import com.github.javaparser.ast.ArrayCreationLevel;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.ArrayCreationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.AssignExpr.Operator;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.EmptyStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.WildcardType;

public class ProxySpezialObjectGeneration extends BasicSpezialObjectGeneration<ProxyBluePrint> {

	private static final Logger LOGGER = LogManager.getLogger(ProxySpezialObjectGeneration.class);

	@Override
	public boolean canGenerateBluePrint(BluePrint bluePrint) {
		return bluePrint instanceof ProxyBluePrint;
	}

	@Override
	public void createField(ClassOrInterfaceDeclaration compilationUnit, ProxyBluePrint bluePrint,
			SignatureType signature) {

		String name = namingService.getFieldName(bluePrint);

		if (signature != null) {
			Type type = JavaParserHelper.generateSignature(signature, importCallBackHandler);
			compilationUnit.addField(type, name, Keyword.PRIVATE);

		} else {
			Class<?> type = bluePrint.getType();
			compilationUnit.addField(type, name, Keyword.PRIVATE);

			importCallBackHandler.accept(type);
		}
	}

	@Override
	public void createObject(BlockStmt codeBlock, ProxyBluePrint bluePrint, SignatureType signature, boolean isField) {
		LOGGER.debug("start generation of proxy " + bluePrint);

		if (bluePrint.isNotBuild()) {
			createChilds(codeBlock, bluePrint);

			MethodCallExpr proxyCreationExpr = createProxyInstanceExpr(bluePrint);

			ClassOrInterfaceType proxyType = getType(bluePrint.getType());

			CastExpr castExpr = new CastExpr(proxyType, proxyCreationExpr);

			Expression expr = isField
					? new AssignExpr(new FieldAccessExpr(new ThisExpr(), namingService.getFieldName(bluePrint)),
							castExpr, Operator.ASSIGN)
					: new VariableDeclarationExpr(new VariableDeclarator(proxyType,
							namingService.getLocalName(codeBlock, bluePrint), castExpr));

			ExpressionStmt stmt = new ExpressionStmt(expr);
			stmt.setLineComment("TODO add initialization of invocationHandler: "
					+ bluePrint.getInvocationHandler().getClass().getSimpleName());
			codeBlock.addStatement(stmt);
			codeBlock.addStatement(new EmptyStmt());

			bluePrint.setBuild();
		}

	}

	private void createChilds(BlockStmt codeBlock, ProxyBluePrint proxyBluePrint) {
		for (Entry<Method, BluePrint> entry : proxyBluePrint.getProxyResults()) {

			BluePrint child = entry.getValue();

			boolean isField = namingService.existsField(child);

			boolean notBuild = child.isNotBuild();

			LOGGER.debug("create child " + child);

			String comment = "return value of proxy operation " + entry.getKey().toGenericString();

			EmptyStmt stmt = new EmptyStmt();
			codeBlock.addStatement(stmt);

			if (notBuild) {

				if (child.isComplexBluePrint()) {
					createComplexObject(codeBlock, child.castToComplexBluePrint());

				} else if (child.isCollectionBluePrint()) {
					SignatureType signature = GenerationHelper
							.mapGenericTypeToSignature(entry.getKey().getGenericReturnType());

					collectionGenerationFactory.createCollection(codeBlock, child.castToCollectionBluePrint(),
							signature, isField);

				} else if (child.isArrayBluePrint()) {
					SignatureType signature = GenerationHelper
							.mapGenericTypeToSignature(entry.getKey().getGenericReturnType());

					arrayGeneration.createArray(codeBlock, child.castToArrayBluePrint(), signature, isField);

				} else if (child.isSpezialBluePrint()) {
					SignatureType signature = GenerationHelper
							.mapGenericTypeToSignature(entry.getKey().getGenericReturnType());

					spezialGenerationFactory.createObject(codeBlock, child, signature, isField);

				} else if (child.isSimpleBluePrint()) {
					simpleGenerationFactory.createObject(codeBlock, child.castToSimpleBluePrint(), isField);
				} else
					throw new IllegalArgumentException("cant generate BluePrint: " + child);

			} else {
				comment +=" "+ (isField ? namingService.getFieldName(child) : namingService.getLocalName(codeBlock, child))
						+ " already created";
			}

			stmt.setLineComment(comment);
		}
	}

	private MethodCallExpr createProxyInstanceExpr(ProxyBluePrint bluePrint) {

		MethodCallExpr classLoaderExpr = new MethodCallExpr(new MethodCallExpr(new NameExpr("Thread"), "currentThread"),
				"getContextClassLoader");
		importCallBackHandler.accept(Thread.class);

		ClassOrInterfaceType arrayType = getType(Class.class);
		arrayType.setTypeArguments(new WildcardType());

		NodeList<Expression> proxyInterfaces = new NodeList<>();

		for (Class<?> interfaceClass : bluePrint.getInterfaceClasses()) {
			importCallBackHandler.accept(interfaceClass);

			proxyInterfaces.add(new ClassExpr(getType(interfaceClass)));
		}

		ArrayCreationExpr proxyInterfacesArray = new ArrayCreationExpr(arrayType,
				NodeList.nodeList(new ArrayCreationLevel()), new ArrayInitializerExpr(proxyInterfaces));

		MethodCallExpr proxyCreationExpr = new MethodCallExpr(new NameExpr("Proxy"), "newProxyInstance",
				NodeList.nodeList(classLoaderExpr, proxyInterfacesArray, new NullLiteralExpr()));

		importCallBackHandler.accept(Proxy.class);

		return proxyCreationExpr;
	}

}

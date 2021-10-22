package org.testgen.runtime.generation.javaparser.impl.spezial;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.Set;

import org.testgen.config.TestgeneratorConfig;
import org.testgen.logging.LogManager;
import org.testgen.logging.Logger;
import org.testgen.runtime.classdata.model.ClassData;
import org.testgen.runtime.classdata.model.FieldData;
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

		if (signature != null) {
			Type type = JavaParserHelper.generateSignature(signature, importCallBackHandler);
			compilationUnit.addField(type, namingService.getFieldName(bluePrint), Keyword.PRIVATE);
			
		} else {
			Class<?> type = bluePrint.getType();

			compilationUnit.addField(type, namingService.getFieldName(bluePrint), Keyword.PRIVATE);

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
			stmt.setLineComment("TODO add initalization for invocationHandler, name of invocationhandler was "
					+ bluePrint.getInvocationHandler().getClass().getSimpleName());
			codeBlock.addStatement(stmt);

			bluePrint.setBuild();
		}

	}

	private void createChilds(BlockStmt codeBlock, ProxyBluePrint proxyBluePrint) {
		for (Entry<Method, BluePrint> entry : proxyBluePrint.getProxyResults()) {

			BluePrint child = entry.getValue();

			boolean isField = namingService.existsField(child);

			boolean notBuild = child.isNotBuild();

			LOGGER.debug("create child " + child);

			EmptyStmt stmt = new EmptyStmt();
			stmt.setLineComment("return value of proxy operation " + entry.getKey().toGenericString());
			codeBlock.addStatement(stmt);

			if (child.isComplexBluePrint() && notBuild) {
				ClassData classData = GenerationHelper.getClassData(child.getReference());

				Set<FieldData> calledFields = Collections.emptySet();
				if (TestgeneratorConfig.traceReadFieldAccess()) {
					calledFields = GenerationHelper.getCalledFields(child.getReference());
				}

				complexObjectGeneration.createObject(codeBlock, child.castToComplexBluePrint(), isField, classData,
						calledFields);
			} else if (child.isCollectionBluePrint() && notBuild) {
				SignatureType signature = GenerationHelper
						.mapGenericTypeToSignature(entry.getKey().getGenericReturnType());

				collectionGenerationFactory.createCollection(codeBlock, child.castToCollectionBluePrint(), signature,
						isField);
			} else if (child.isArrayBluePrint() && notBuild) {
				SignatureType signature = GenerationHelper
						.mapGenericTypeToSignature(entry.getKey().getGenericReturnType());

				arrayGeneration.createArray(codeBlock, child.castToArrayBluePrint(), signature, isField);
			} else if (child.isSpezialBluePrint() && notBuild) {
				SignatureType signature = GenerationHelper
						.mapGenericTypeToSignature(entry.getKey().getGenericReturnType());

				spezialGenerationFactory.createObject(codeBlock, child, signature, isField);
			} else if (child.isSimpleBluePrint()) {
				simpleGenerationFactory.createObject(codeBlock, child.castToSimpleBluePrint(), isField);
			} else
				throw new IllegalArgumentException("cant generate BluePrint: " + child);

		}
	}

	private MethodCallExpr createProxyInstanceExpr(ProxyBluePrint bluePrint) {

		MethodCallExpr classLoaderExpr = new MethodCallExpr(new MethodCallExpr(new NameExpr("Thread"), "currentThread"),
				"getContextClassLoader");

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
				NodeList.nodeList(classLoaderExpr, proxyInterfacesArray));

		importCallBackHandler.accept(Proxy.class);

		return proxyCreationExpr;
	}

}

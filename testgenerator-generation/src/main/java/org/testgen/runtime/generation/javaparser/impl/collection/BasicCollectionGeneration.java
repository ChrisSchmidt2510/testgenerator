package org.testgen.runtime.generation.javaparser.impl.collection;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;

import org.testgen.config.TestgeneratorConfig;
import org.testgen.runtime.classdata.model.ClassData;
import org.testgen.runtime.classdata.model.FieldData;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;
import org.testgen.runtime.generation.api.ArrayGeneration;
import org.testgen.runtime.generation.api.ComplexObjectGeneration;
import org.testgen.runtime.generation.api.GenerationHelper;
import org.testgen.runtime.generation.api.collections.CollectionGeneration;
import org.testgen.runtime.generation.api.collections.CollectionGenerationFactory;
import org.testgen.runtime.generation.api.naming.NamingService;
import org.testgen.runtime.generation.api.simple.SimpleObjectGenerationFactory;
import org.testgen.runtime.generation.api.spezial.SpezialObjectGenerationFactory;
import org.testgen.runtime.valuetracker.blueprint.BasicCollectionBluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.AssignExpr.Operator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

public abstract class BasicCollectionGeneration
		implements CollectionGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> {

	protected ComplexObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> complexGeneration;

	protected ArrayGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> arrayGeneration;

	protected SimpleObjectGenerationFactory<ClassOrInterfaceDeclaration, BlockStmt, Expression> simpleGenerationFactory;

	protected CollectionGenerationFactory<ClassOrInterfaceDeclaration, BlockStmt, Expression> collectionGenerationFactory;

	protected SpezialObjectGenerationFactory<ClassOrInterfaceDeclaration, BlockStmt, Expression, BluePrint> spezialGenerationFactory;
	
	protected NamingService<BlockStmt> namingService = getNamingService();

	protected Consumer<Class<?>> importCallbackHandler;

	@Override
	public void setSimpleObjectGenerationFactory(
			SimpleObjectGenerationFactory<ClassOrInterfaceDeclaration, BlockStmt, Expression> simpleGenerationFactory) {
		this.simpleGenerationFactory = simpleGenerationFactory;
	}

	@Override
	public void setCollectionGenerationFactory(
			CollectionGenerationFactory<ClassOrInterfaceDeclaration, BlockStmt, Expression> collectionGenerationFactory) {
		this.collectionGenerationFactory = collectionGenerationFactory;
	}
	
	@Override
	public void setSpezialGenerationFactory(
			SpezialObjectGenerationFactory<ClassOrInterfaceDeclaration, BlockStmt, Expression, BluePrint> spezialGenerationFactory) {
		this.spezialGenerationFactory = spezialGenerationFactory;
	}

	@Override
	public void setArrayGeneration(
			ArrayGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> arrayGeneration) {
		this.arrayGeneration = arrayGeneration;
	}

	@Override
	public void setComplexObjectGeneration(
			ComplexObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> complexObjectGeneration) {
		this.complexGeneration = complexObjectGeneration;
	}

	@Override
	public void setImportCallBackHandler(Consumer<Class<?>> importCallBackHandler) {
		this.importCallbackHandler = importCallBackHandler;
	}

	@Override
	public void addCollectionToField(BlockStmt statementTree, BasicCollectionBluePrint<?> bluePrint,
			boolean isField, Expression accessExpr) {
		String name = isField ? namingService.getFieldName(bluePrint)
				: namingService.getLocalName(statementTree, bluePrint);

		Expression collectionExpr = isField ? new FieldAccessExpr(new ThisExpr(), name) : new NameExpr(name);

		statementTree.addStatement(new AssignExpr(accessExpr, collectionExpr, Operator.ASSIGN));
	}

	protected void generateComplexChildOfCollection(BlockStmt statementTree, BluePrint child, SignatureType signature) {
		boolean isField = namingService.existsField(child);

		if (child.isComplexBluePrint() && child.isNotBuild()) {
			ClassData classData = GenerationHelper.getClassData(child.getReference());

			Set<FieldData> calledFields = Collections.emptySet();
			if (TestgeneratorConfig.traceReadFieldAccess()) {
				calledFields = GenerationHelper.getCalledFields(child.getReference());
			}

			complexGeneration.createObject(statementTree, child.castToComplexBluePrint(), isField, classData,
					calledFields);

		} else if (child.isCollectionBluePrint() && child.isNotBuild()) {
			BasicCollectionBluePrint<?> collection = child.castToCollectionBluePrint();

			collectionGenerationFactory.createCollection(statementTree, collection, signature, //
					isField);

		} else if (child.isArrayBluePrint() && child.isNotBuild()) {
			arrayGeneration.createArray(statementTree, child.castToArrayBluePrint(), signature, isField);
		}
		
		else if(child.isSpezialBluePrint() && child.isNotBuild()) {
			spezialGenerationFactory.createObject(statementTree, child, signature, isField);
		}
	}

	protected ClassOrInterfaceType emptyGenericParameters(Class<?> clazz) {
		importCallbackHandler.accept(clazz);

		ClassOrInterfaceType type = new ClassOrInterfaceType(null, clazz.getSimpleName());
		type.setTypeArguments(new NodeList<>());

		return type;
	}

	/**
	 * Returns the Expression for the element of the {@link Collection}.
	 * 
	 * @param statementTree codeBlock needed for Name of the bluePrint
	 * @param bluePrint     element of the {@link Collection}
	 * @return
	 */
	protected Expression getExpressionForElement(BlockStmt statementTree, BluePrint bluePrint) {

		if (bluePrint.isComplexType() || bluePrint.isBuild())
			return namingService.existsField(bluePrint)
					? new FieldAccessExpr(new ThisExpr(), namingService.getFieldName(bluePrint))
					: new NameExpr(namingService.getLocalName(statementTree, bluePrint));

		return simpleGenerationFactory.createInlineExpression(bluePrint.castToSimpleBluePrint());

	}

}

package org.testgen.runtime.generation.javaparser.impl;

import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;

import org.testgen.config.TestgeneratorConfig;
import org.testgen.logging.LogManager;
import org.testgen.logging.Logger;
import org.testgen.runtime.classdata.model.ClassData;
import org.testgen.runtime.classdata.model.FieldData;
import org.testgen.runtime.classdata.model.SetterMethodData;
import org.testgen.runtime.classdata.model.SetterType;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;
import org.testgen.runtime.generation.api.ArrayGeneration;
import org.testgen.runtime.generation.api.ComplexObjectGeneration;
import org.testgen.runtime.generation.api.GenerationHelper;
import org.testgen.runtime.generation.api.collections.CollectionGenerationFactory;
import org.testgen.runtime.generation.api.naming.NamingService;
import org.testgen.runtime.generation.api.simple.SimpleObjectGenerationFactory;
import org.testgen.runtime.generation.api.spezial.SpezialObjectGenerationFactory;
import org.testgen.runtime.valuetracker.blueprint.BasicCollectionBluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.complextypes.ArrayBluePrint;

import com.github.javaparser.ast.ArrayCreationLevel;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.ArrayCreationExpr;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.AssignExpr.Operator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.EmptyStmt;
import com.github.javaparser.ast.type.ArrayType;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;

public class JavaParserArrayGeneration implements ArrayGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> {

	private static final Logger LOGGER = LogManager.getLogger(JavaParserArrayGeneration.class);

	private ComplexObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> complexGeneration;

	private SimpleObjectGenerationFactory<ClassOrInterfaceDeclaration, BlockStmt, Expression> simpleGenerationFactory;

	private CollectionGenerationFactory<ClassOrInterfaceDeclaration, BlockStmt, Expression> collectionGenerationFactory;

	private SpezialObjectGenerationFactory<ClassOrInterfaceDeclaration, BlockStmt, Expression, BluePrint> spezialGenerationFactory;
	
	private NamingService<BlockStmt> namingService = getNamingService();

	private Consumer<Class<?>> importCallbackHandler;

	@Override
	public void createField(ClassOrInterfaceDeclaration compilationUnit, ArrayBluePrint bluePrint,
			SignatureType signature) {

		String name = namingService.getFieldName(bluePrint);

		Type type = createArrayType(signature, bluePrint);

		ArrayCreationExpr initalizer = createArrayInitalizer(bluePrint);

		compilationUnit.addFieldWithInitializer(type, name, initalizer, Keyword.PRIVATE);
	}

	@Override
	public void createArray(BlockStmt statementTree, ArrayBluePrint bluePrint, SignatureType signature,
			boolean isField) {
		LOGGER.debug("starting generation of Array: " + bluePrint);

		if (bluePrint.isNotBuild()) {
			createComplexElements(statementTree, bluePrint, signature);

			String name = isField ? namingService.getFieldName(bluePrint)
					: namingService.getLocalName(statementTree, bluePrint);

			if (!isField) {
				Type arrayType = createArrayType(signature, bluePrint);
				ArrayCreationExpr initializer = createArrayInitalizer(bluePrint);

				statementTree.addStatement(
						new VariableDeclarationExpr(new VariableDeclarator(arrayType, name, initializer)));
			}

			Expression accessExpr = isField ? new FieldAccessExpr(new ThisExpr(), name) : new NameExpr(name);

			for (int i = 0; i < bluePrint.getElements().length; i++) {
				BluePrint element = bluePrint.getElements()[i];

				if (element != null) {
					ArrayAccessExpr arrayIndexExpr = new ArrayAccessExpr(accessExpr,
							JavaParserHelper.mapIntegerExpression(i));

					statementTree.addStatement(new AssignExpr(arrayIndexExpr,
							getExpressionForElement(statementTree, element), Operator.ASSIGN));
				}

			}

			statementTree.addStatement(new EmptyStmt());

			bluePrint.setBuild();
		}

	}

	@Override
	public void createComplexElements(BlockStmt statementTree, ArrayBluePrint bluePrint, SignatureType signature) {
		LOGGER.debug("generate complex Types of array" + bluePrint);

		SignatureType genericType = signature != null && !signature.isSimpleSignature() ? signature.getSubTypes().get(0)
				: null;

		genericType = genericType != null && genericType.isSimpleSignature() ? null : genericType;

		for (BluePrint child : bluePrint.getPreExecuteBluePrints()) {

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
				createArray(statementTree, child.castToArrayBluePrint(), signature, isField);
			}
			
			else if(child.isSpezialBluePrint()&& child.isNotBuild()) {
				spezialGenerationFactory.createObject(statementTree, child, signature, isField);
			}
		}

	}

	@Override
	public void addArrayToObject(BlockStmt statementTree, ArrayBluePrint bluePrint, SetterMethodData setter,
			boolean isField, Expression accessExpr) {

		SetterType type = setter.getType();

		if (SetterType.VALUE_SETTER == type) {
			Expression arrayExpr = isField ? //
					new FieldAccessExpr(new ThisExpr(), namingService.getFieldName(bluePrint))
					: new NameExpr(namingService.getLocalName(statementTree, bluePrint));

			statementTree.addStatement(new MethodCallExpr(accessExpr, setter.getName(), NodeList.nodeList(arrayExpr)));
		} else if (SetterType.VALUE_GETTER == type) {

			if ((isField && namingService.existsField(bluePrint)) || namingService.existsLocal(statementTree, bluePrint)
					|| bluePrint.isBuild()) {

				Expression valueAccessExpr = isField
						? new FieldAccessExpr(new ThisExpr(), namingService.getFieldName(bluePrint))
						: new NameExpr(namingService.getLocalName(statementTree, bluePrint));

				Expression targetAccessExpr = new MethodCallExpr(accessExpr, setter.getName());

				for (int i = 0; i < bluePrint.size(); i++) {
					BluePrint element = bluePrint.getElements()[i];

					if (element != null) {

						ArrayAccessExpr arrayAccessTarget = new ArrayAccessExpr(targetAccessExpr,
								JavaParserHelper.mapIntegerExpression(i));

						ArrayAccessExpr arrayAccessValue = new ArrayAccessExpr(valueAccessExpr,
								JavaParserHelper.mapIntegerExpression(i));

						statementTree
								.addStatement(new AssignExpr(arrayAccessTarget, arrayAccessValue, Operator.ASSIGN));

					}
				}
			} else {
				Type arrayType = createArrayType(null, bluePrint);
				String name = namingService.getLocalName(statementTree, bluePrint);
				MethodCallExpr initalizer = new MethodCallExpr(accessExpr, setter.getName());

				statementTree
						.addStatement(new VariableDeclarationExpr(new VariableDeclarator(arrayType, name, initalizer)));

				NameExpr nameExpr = new NameExpr(name);

				for (int i = 0; i < bluePrint.getElements().length; i++) {
					BluePrint element = bluePrint.getElements()[i];

					if (element != null) {
						ArrayAccessExpr arrayIndexExpr = new ArrayAccessExpr(nameExpr,
								JavaParserHelper.mapIntegerExpression(i));

						statementTree.addStatement(new AssignExpr(arrayIndexExpr,
								getExpressionForElement(statementTree, element), Operator.ASSIGN));
					}
				}

			}
		} else
			throw new IllegalArgumentException("invalid SetterType for arrays: " + type);
	}

	@Override
	public void addArrayToField(BlockStmt statementTree, ArrayBluePrint bluePrint, boolean isField,
			Expression accessExpr) {
		String name = isField ? namingService.getFieldName(bluePrint)
				: namingService.getLocalName(statementTree, bluePrint);

		Expression collectionExpr = isField ? new FieldAccessExpr(new ThisExpr(), name) : new NameExpr(name);

		statementTree.addStatement(new AssignExpr(accessExpr, collectionExpr, Operator.ASSIGN));
	}

	private Type createArrayType(SignatureType signature, ArrayBluePrint bluePrint) {
		if (signature != null) {
			return JavaParserHelper.generateSignature(signature, importCallbackHandler);
		}

		Class<?> baseType = bluePrint.getBaseType();

		Type baseArrayType;

		if (baseType.isPrimitive())
			baseArrayType = JavaParserHelper.getPrimitiveType(baseType);

		else {
			baseArrayType = new ClassOrInterfaceType(null, baseType.getSimpleName());

			importCallbackHandler.accept(baseType);
		}

		ArrayType arrayType = new ArrayType(baseArrayType);

		int i = 1;

		while (i++ < bluePrint.getDimensions()) {
			arrayType = new ArrayType(arrayType);
		}

		return arrayType;
	}

	private ArrayCreationExpr createArrayInitalizer(ArrayBluePrint bluePrint) {
		NodeList<ArrayCreationLevel> arrayCreationLevel = new NodeList<>();

		arrayCreationLevel.add(new ArrayCreationLevel(bluePrint.size()));

		while (arrayCreationLevel.size() < bluePrint.getDimensions()) {
			arrayCreationLevel.add(new ArrayCreationLevel());
		}

		Class<?> baseType = bluePrint.getBaseType();

		Type arrayType;
		if (baseType.isPrimitive())
			arrayType = JavaParserHelper.getPrimitiveType(baseType);
		else {
			arrayType = new ClassOrInterfaceType(null, baseType.getSimpleName());

			importCallbackHandler.accept(baseType);
		}

		return new ArrayCreationExpr(arrayType, arrayCreationLevel, null);
	}

	/**
	 * Returns the Expression for the element of the array.
	 * 
	 * @param statementTree codeBlock needed for Name of the bluePrint
	 * @param bluePrint     element of the array
	 * @return
	 */
	private Expression getExpressionForElement(BlockStmt statementTree, BluePrint bluePrint) {

		if (bluePrint.isComplexType() || bluePrint.isBuild())
			return namingService.existsField(bluePrint)
					? new FieldAccessExpr(new ThisExpr(), namingService.getFieldName(bluePrint))
					: new NameExpr(namingService.getLocalName(statementTree, bluePrint));

		return simpleGenerationFactory.createInlineExpression(bluePrint.castToSimpleBluePrint());
	}

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
	public void setComplexObjectGeneration(
			ComplexObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> complexObjectGeneration) {
		this.complexGeneration = complexObjectGeneration;
	}

	@Override
	public void setImportCallBackHandler(Consumer<Class<?>> importCallBackHandler) {
		this.importCallbackHandler = importCallBackHandler;
	}

	@Override
	public void setSpezialGenerationFactory(
			SpezialObjectGenerationFactory<ClassOrInterfaceDeclaration, BlockStmt, Expression, BluePrint> spezialGenerationFactory) {
		this.spezialGenerationFactory = spezialGenerationFactory;
	}
}

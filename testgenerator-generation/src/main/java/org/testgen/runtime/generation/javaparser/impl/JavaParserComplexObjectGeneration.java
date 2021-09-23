package org.testgen.runtime.generation.javaparser.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import org.testgen.config.TestgeneratorConfig;
import org.testgen.logging.LogManager;
import org.testgen.logging.Logger;
import org.testgen.runtime.classdata.model.ClassData;
import org.testgen.runtime.classdata.model.ConstructorData;
import org.testgen.runtime.classdata.model.FieldData;
import org.testgen.runtime.classdata.model.SetterMethodData;
import org.testgen.runtime.classdata.model.SetterType;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;
import org.testgen.runtime.generation.api.ArrayGeneration;
import org.testgen.runtime.generation.api.ComplexObjectGeneration;
import org.testgen.runtime.generation.api.GenerationHelper;
import org.testgen.runtime.generation.api.collections.CollectionGenerationFactory;
import org.testgen.runtime.generation.api.naming.NamingService;
import org.testgen.runtime.generation.api.naming.NamingServiceProvider;
import org.testgen.runtime.generation.api.simple.SimpleObjectGenerationFactory;
import org.testgen.runtime.valuetracker.blueprint.BasicCollectionBluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.complextypes.ArrayBluePrint;
import org.testgen.runtime.valuetracker.blueprint.complextypes.ComplexBluePrint;

import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.AssignExpr.Operator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.EmptyStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;

public class JavaParserComplexObjectGeneration
		implements ComplexObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> {

	private static final Logger LOGGER = LogManager.getLogger(JavaParserComplexObjectGeneration.class);

	private SimpleObjectGenerationFactory<ClassOrInterfaceDeclaration, BlockStmt, Expression> simpleObjectGenerationFactory;

	private CollectionGenerationFactory<ClassOrInterfaceDeclaration, BlockStmt, Expression> collectionGenerationFactory;

	private ArrayGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> arrayGeneration;

	private Consumer<Class<?>> importCallBackHandler;

	private NamingService<BlockStmt> namingService = NamingServiceProvider.getNamingService();

	@Override
	public void createField(ClassOrInterfaceDeclaration compilationUnit, ComplexBluePrint bluePrint,
			SignatureType signature) {

		Type type;

		if (signature != null)
			type = JavaParserHelper.generateSignature(signature, importCallBackHandler);
		else {
			importCallBackHandler.accept(bluePrint.getReferenceClass());

			type = new ClassOrInterfaceType(null, bluePrint.getSimpleClassName());
		}

		compilationUnit.addField(type, namingService.getFieldName(bluePrint), Keyword.PRIVATE);
	}

	@Override
	public void createObject(BlockStmt statementTree, ComplexBluePrint bluePrint, boolean isField, ClassData classData,
			Set<FieldData> calledFields) {

		if (bluePrint.isNotBuild()) {
			LOGGER.debug("start generating Complex-Object " + bluePrint);

			Set<BluePrint> usedBluePrints = new HashSet<>();

			createComplexTypes(statementTree, bluePrint, classData, calledFields);

			NodeList<Expression> arguments = new NodeList<>();

			Expression objInitalizer = null;

			if (!classData.hasDefaultConstructor()) {
				ConstructorData constructor = classData.getConstructor();

				LOGGER.debug("create Constructor for Complex-Object " + bluePrint);

				if (constructor.isNotEmpty()) {
					for (Entry<Integer, FieldData> constructorField : constructor.getConstructorFields().entrySet()) {
						calledFields.remove(constructorField.getValue());

						BluePrint constructorFieldBp = bluePrint
								.getBluePrintForName(constructorField.getValue().getName());

						usedBluePrints.add(constructorFieldBp);

						if (constructorFieldBp.isSimpleBluePrint()) {
							SimpleBluePrint<?> simpleBluePrint = constructorFieldBp.castToSimpleBluePrint();

							Expression inlinedSimpleObject = simpleObjectGenerationFactory
									.createInlineExpression(simpleBluePrint);

							arguments.add(inlinedSimpleObject);
						} else {
							String argumentName = namingService.getLocalName(statementTree, constructorFieldBp);

							arguments.add(new NameExpr(argumentName));
						}
					}

				}
			}

			ClassOrInterfaceType type = new ClassOrInterfaceType(null, bluePrint.getSimpleClassName());

			String comment = null;

			if (!classData.hasDefaultConstructor() && !classData.getConstructor().isNotEmpty()) {
				objInitalizer = new NullLiteralExpr();
				comment = "TODO add initalization for class: " + bluePrint.getReferenceClass().getSimpleName();

			} else if (classData.isInnerClass()) {
				BluePrint outerClassBp = getBluePrintForClassData(bluePrint, classData.getOuterClass());

				usedBluePrints.add(outerClassBp);

				NameExpr outerClassName = new NameExpr(namingService.getLocalName(statementTree, outerClassBp));

				objInitalizer = new ObjectCreationExpr(outerClassName, type, arguments);

			} else {
				objInitalizer = new ObjectCreationExpr(null, type, arguments);
			}

			importCallBackHandler.accept(bluePrint.getReference().getClass());

			String name = isField ? namingService.getFieldName(bluePrint)
					: namingService.getLocalName(statementTree, bluePrint);

			Expression objectCreation = isField
					? new AssignExpr(new FieldAccessExpr(new ThisExpr(), name), objInitalizer, Operator.ASSIGN)
					: new VariableDeclarationExpr(new VariableDeclarator(type, name, objInitalizer));

			ExpressionStmt stmt = new ExpressionStmt(objectCreation);
			if (comment != null)
				stmt.setLineComment(comment);

			statementTree.addStatement(stmt);

			addChildsToObject(statementTree, bluePrint, classData, calledFields, usedBluePrints);

			statementTree.addStatement(new EmptyStmt());

			bluePrint.setBuild();
		}

	}

	@Override
	public void createComplexTypes(BlockStmt codeBlock, ComplexBluePrint bluePrint, ClassData classData,
			Set<FieldData> calledFields) {

		for (BluePrint bp : bluePrint.getPreExecuteBluePrints()) {
			Optional<FieldData> calledField = calledFields.stream()
					.filter(field -> field.getName().equals(bp.getName())).findAny();

			if (calledField.isPresent() || !TestgeneratorConfig.traceReadFieldAccess() || //
					(classData.isInnerClass()
							&& classData.getOuterClass().getName().equals(bp.getClassNameOfReference()))) {

				if (bp.isComplexBluePrint()) {
					createComplexObject(codeBlock, bp);

				} else if (bp.isCollectionBluePrint()) {
					BasicCollectionBluePrint<?> collection = bp.castToCollectionBluePrint();

					SignatureType signature = null;
					SetterMethodData setter = null;
					if (TestgeneratorConfig.traceReadFieldAccess()) {
						FieldData field = classData.getFieldInHierarchie(calledField.get());
						signature = field.getSignature();

						setter = classData.getSetterInHierarchie(field);

					} else {
						FieldData field = classData.getCollectionFieldInHierarchie(collection.getName());
						signature = field.getSignature();

						setter = classData.getSetterInHierarchie(field);
					}

					if (SetterType.COLLECTION_SETTER == setter.getType())
						collectionGenerationFactory.createComplexElements(codeBlock, collection, signature);

					else
						collectionGenerationFactory.createCollection(codeBlock, collection, signature, false);

				} else if (bp.isArrayBluePrint()) {
					ArrayBluePrint arrayBluePrint = bp.castToArrayBluePrint();

					SignatureType signature = null;
					SetterMethodData setter = null;
					if (TestgeneratorConfig.traceReadFieldAccess()) {
						FieldData field = classData.getFieldInHierarchie(calledField.get());
						signature = field.getSignature();

						setter = classData.getSetterInHierarchie(field);

					} else {
						FieldData field = classData.getFieldInHierarchie(arrayBluePrint.getName(),
								arrayBluePrint.getReferenceClass());
						signature = field.getSignature();

						setter = classData.getSetterInHierarchie(field);
					}

					if (SetterType.VALUE_SETTER == setter.getType())
						arrayGeneration.createArray(codeBlock, arrayBluePrint, signature, false);
				}
			}
		}

	}

	@Override
	public void addChildToObject(BlockStmt codeBlock, BluePrint bluePrint, SetterMethodData setter,
			Expression accessExpr) {

		boolean isField = namingService.existsField(bluePrint);

		if (setter == null) {
			EmptyStmt stmt = new EmptyStmt();
			stmt.setLineComment(String.format("TODO no setter found for Field: %s Value: %s", bluePrint.getName(),
					getExpressionForBluePrint(bluePrint, codeBlock)));

			codeBlock.addStatement(stmt);
		} else if (bluePrint.isCollectionBluePrint())
			collectionGenerationFactory.addCollectionToObject(codeBlock, bluePrint.castToCollectionBluePrint(), isField,
					setter, accessExpr);

		else if (bluePrint.isArrayBluePrint())
			arrayGeneration.addArrayToObject(codeBlock, bluePrint.castToArrayBluePrint(), setter, isField, accessExpr);

		else if (SetterType.VALUE_SETTER == setter.getType()
				&& (bluePrint.isComplexBluePrint() || bluePrint.isSimpleBluePrint())) {
			Expression param = getExpressionForBluePrint(bluePrint, codeBlock);
			codeBlock.addStatement(new MethodCallExpr(accessExpr, setter.getName(), NodeList.nodeList(param)));
		}
	}

	@Override
	public void addChildToField(BlockStmt codeBlock, BluePrint bluePrint, Expression accessExpr) {

		boolean isField = namingService.existsField(bluePrint);

		if (bluePrint.isCollectionBluePrint())
			collectionGenerationFactory.addCollectionToField(codeBlock, bluePrint.castToCollectionBluePrint(), isField,
					accessExpr);

		else if (bluePrint.isArrayBluePrint())
			arrayGeneration.addArrayToField(codeBlock, bluePrint.castToArrayBluePrint(), isField, accessExpr);

		// for ComplexBluePrints and SimpleBluePrints
		else if (bluePrint.isComplexBluePrint() || bluePrint.isSimpleBluePrint()) {
			Expression param = getExpressionForBluePrint(bluePrint, codeBlock);
			codeBlock.addStatement(new AssignExpr(accessExpr, param, Operator.ASSIGN));
		}
	}

	private Expression getExpressionForBluePrint(BluePrint bluePrint, BlockStmt codeBlock) {
		if (bluePrint.isComplexType() || bluePrint.isBuild())
			return namingService.existsField(bluePrint)
					? new FieldAccessExpr(new ThisExpr(), namingService.getFieldName(bluePrint))
					: new NameExpr(namingService.getLocalName(codeBlock, bluePrint));
		else
			return simpleObjectGenerationFactory.createInlineExpression(bluePrint.castToSimpleBluePrint());
	}

	private void addChildsToObject(BlockStmt codeBlock, ComplexBluePrint bluePrint, ClassData classData,
			Set<FieldData> calledFields, Set<BluePrint> usedBluePrints) {

		Expression accessExpr = namingService.existsField(bluePrint)
				? new FieldAccessExpr(new ThisExpr(), namingService.getFieldName(bluePrint))
				: new NameExpr(namingService.getLocalName(codeBlock, bluePrint));

		if (TestgeneratorConfig.traceReadFieldAccess()) {

			for (FieldData field : calledFields) {
				LOGGER.info("add Field " + field + " to Object " + bluePrint);

				BluePrint bpField = bluePrint.getBluePrintForName(field.getName());

				FieldData originalField = classData.getFieldInHierarchie(field);

				if (originalField.isPublic())
					addChildToField(codeBlock, bpField, accessExpr);

				else {
					SetterMethodData setter = classData.getSetterInHierarchie(field);

					addChildToObject(codeBlock, bpField, setter, accessExpr);
				}
			}

		} else {
			for (BluePrint child : bluePrint.getChildBluePrints()) {

				if (!usedBluePrints.contains(child)) {

					FieldData field = child.isCollectionBluePrint()
							? classData.getCollectionFieldInHierarchie(child.getName())
							: classData.getFieldInHierarchie(child.getName(), child.getReference().getClass());

					LOGGER.info("add Field " + field + " to Object " + bluePrint);

					if (field.isPublic())
						addChildToField(codeBlock, bluePrint, accessExpr);

					else {
						SetterMethodData setter = classData.getSetterInHierarchie(field);

						addChildToObject(codeBlock, child, setter, accessExpr);
					}
				}
			}
		}
	}

	private void createComplexObject(BlockStmt code, BluePrint bluePrint) {
		if (bluePrint.isNotBuild()) {
			ClassData classData = GenerationHelper.getClassData(bluePrint.getReference());

			Set<FieldData> calledFields = Collections.emptySet();
			if (TestgeneratorConfig.traceReadFieldAccess()) {
				calledFields = GenerationHelper.getCalledFields(bluePrint.getReference());
			}

			createObject(code, bluePrint.castToComplexBluePrint(), false, classData, calledFields);
		}
	}

	/**
	 * Filters for BluePrint of the outerClass and returns the Name of it in the
	 * generated SourceCode. This works cause the BluePrint is already generated in
	 * the Method
	 * {@link JavaParserComplexObjectGeneration#createComplexTypes(BlockStmt, List, ClassData, Set)}
	 */
	private BluePrint getBluePrintForClassData(ComplexBluePrint parent, ClassData outerClass) {
		BluePrint outerClassBP = parent.getChildBluePrints().stream()
				.filter(bp -> outerClass.getName().equals(bp.getClassNameOfReference())).findFirst()
				.orElseThrow(() -> new IllegalArgumentException(
						String.format("no BluePrint found for ClassData %s", outerClass)));

		return outerClassBP;
	}

	@Override
	public void setSimpleObjectGenerationFactory(
			SimpleObjectGenerationFactory<ClassOrInterfaceDeclaration, BlockStmt, Expression> simpleGenerationFactory) {
		this.simpleObjectGenerationFactory = simpleGenerationFactory;
	}

	@Override
	public void setCollectionGenerationFactory(
			CollectionGenerationFactory<ClassOrInterfaceDeclaration, BlockStmt, Expression> collectionGenerationFactory) {
		this.collectionGenerationFactory = collectionGenerationFactory;
	}

	@Override
	public void setArrayGeneration(
			ArrayGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> arrayGeneration) {
		this.arrayGeneration = arrayGeneration;
	}

	@Override
	public void setImportCallBackHandler(Consumer<Class<?>> importCallBackHandler) {
		this.importCallBackHandler = importCallBackHandler;
	}

}

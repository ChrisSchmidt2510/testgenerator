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
import org.testgen.runtime.valuetracker.blueprint.AbstractBasicCollectionBluePrint;
import org.testgen.runtime.valuetracker.blueprint.ArrayBluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.ComplexBluePrint;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.AssignExpr.Operator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

public class JavaParserComplexObjectGeneration
		implements ComplexObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> {

	private static final Logger LOGGER = LogManager.getLogger(JavaParserComplexObjectGeneration.class);

	private SimpleObjectGenerationFactory<ClassOrInterfaceDeclaration, BlockStmt, Expression> simpleObjectGenerationFactory = getSimpleObjectGenerationFactory();

	private CollectionGenerationFactory<ClassOrInterfaceDeclaration, BlockStmt, Expression> collectionGenerationFactory = getCollectionGenerationFactory();

	private ArrayGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> arrayGeneration = getArrayGeneration();

	private Consumer<Class<?>> importCallBackHandler = getImportCallBackHandler();

	private NamingService<BlockStmt> namingService = NamingServiceProvider.getNamingService();

	@Override
	public void createField(ClassOrInterfaceDeclaration compilationUnit, ComplexBluePrint bluePrint,
			SignatureType signature) {
		// TODO Auto-generated method stub

	}

	@Override
	public void createObject(BlockStmt statementTree, ComplexBluePrint bluePrint, boolean isField, ClassData classData,
			Set<FieldData> calledFields) {

		if (bluePrint.isNotBuild()) {
			LOGGER.debug("start generating Complex-Object " + bluePrint);

			Set<BluePrint> usedBluePrints = new HashSet<>();

			createComplexTypesOfObject(statementTree, bluePrint.getPreExecuteBluePrints(), classData, calledFields);

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
									.createInlineObject(simpleBluePrint);

							arguments.add(inlinedSimpleObject);
						} else {
							String argumentName = namingService.getLocalName(statementTree, constructorFieldBp);

							arguments.add(new NameExpr(argumentName));
						}
					}

					ClassOrInterfaceType type = new ClassOrInterfaceType(null, bluePrint.getSimpleClassName());

					if (classData.isInnerClass()) {
						NameExpr outerClassName = new NameExpr(
								getBluePrintForClassData(bluePrint, classData.getOuterClass(), statementTree));

						objInitalizer = new ObjectCreationExpr(outerClassName, type, arguments);
					} else {
						objInitalizer = new ObjectCreationExpr(null, type, arguments);
					}

				} else {
					objInitalizer = new NullLiteralExpr();
					objInitalizer.addOrphanComment(new LineComment(
							"TODO add initalization for class: " + bluePrint.getClassNameOfReference()));
				}
			}

			importCallBackHandler.accept(bluePrint.getReference().getClass());

			String name = isField ? namingService.getFieldName(bluePrint)
					: namingService.getLocalName(statementTree, bluePrint);

			Expression objectCreation = isField
					? new AssignExpr(new ThisExpr(new Name(name)), objInitalizer, Operator.ASSIGN)
					: new VariableDeclarationExpr(
							new VariableDeclarator(new ClassOrInterfaceType(null, bluePrint.getSimpleClassName()), //
									name, objInitalizer));

			statementTree.addStatement(new ExpressionStmt(objectCreation));
		}

	}

	private void createComplexTypesOfObject(BlockStmt code, List<BluePrint> complexChilds, ClassData classData,
			Set<FieldData> calledFields) {

		for (BluePrint bp : complexChilds) {
			Optional<FieldData> calledField = calledFields.stream()
					.filter(field -> field.getName().equals(bp.getName())).findAny();

			if (calledField.isPresent() || !TestgeneratorConfig.traceReadFieldAccess() || //
					(classData.isInnerClass()
							&& classData.getOuterClass().getName().equals(bp.getClassNameOfReference()))) {
				if (bp.isComplexBluePrint()) {
					createComplexObject(code, bp);

				} else if (bp.isCollectionBluePrint()) {
					AbstractBasicCollectionBluePrint<?> collection = bp.castToCollectionBluePrint();

					SetterMethodData setter = null;
					SignatureType signature = null;
					if (TestgeneratorConfig.traceReadFieldAccess()) {
						FieldData field = classData.getFieldInHierarchie(calledField.get());
						signature = field.getSignature();
						setter = classData.getSetterInHierarchie(field);

					} else {
						FieldData field = classData.getCollectionFieldInHierarchie(collection.getName());
						signature = field.getSignature();
						setter = classData.getSetterInHierarchie(field);
					}

					collectionGenerationFactory.createCollection(code, collection, signature,
							setter != null && SetterType.COLLECTION_SETTER == setter.getType(), false);
				} else if (bp.isArrayBluePrint()) {
					ArrayBluePrint arrayBluePrint = bp.castToArrayBluePrint();

					FieldData field = classData.getFieldInHierarchie(arrayBluePrint.getName(),
							arrayBluePrint.getType());
					SetterMethodData setter = classData.getSetterInHierarchie(field);

					arrayGeneration.createArray(code, arrayBluePrint,
							setter != null && SetterType.VALUE_GETTER == setter.getType(), false);
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
	 * {@link JavaParserComplexObjectGeneration#createComplexTypesOfObject(BlockStmt, List, ClassData, Set)}
	 */
	private String getBluePrintForClassData(ComplexBluePrint parent, ClassData outerClass, BlockStmt blockStmt) {
		BluePrint outerClassBP = parent.getChildBluePrints().stream()
				.filter(bp -> outerClass.getName().equals(bp.getClassNameOfReference())).findFirst()
				.orElseThrow(() -> new IllegalArgumentException(
						String.format("no BluePrint found for ClassData %s", outerClass)));

		return namingService.getLocalName(blockStmt, outerClassBP);
	}

}

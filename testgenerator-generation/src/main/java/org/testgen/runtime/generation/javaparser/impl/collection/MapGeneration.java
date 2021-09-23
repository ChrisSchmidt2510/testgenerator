package org.testgen.runtime.generation.javaparser.impl.collection;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.testgen.logging.LogManager;
import org.testgen.logging.Logger;
import org.testgen.runtime.classdata.model.SetterMethodData;
import org.testgen.runtime.classdata.model.SetterType;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;
import org.testgen.runtime.generation.javaparser.impl.JavaParserHelper;
import org.testgen.runtime.valuetracker.blueprint.BasicCollectionBluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.collections.MapBluePrint;

import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.EmptyStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;

public class MapGeneration extends BasicCollectionGeneration {

	private static final Logger LOGGER = LogManager.getLogger(MapGeneration.class);

	@Override
	public boolean canGenerateBluePrint(BasicCollectionBluePrint<?> bluePrint) {
		return bluePrint instanceof MapBluePrint && Map.class.equals(bluePrint.getInterfaceClass());
	}

	@Override
	public void createField(ClassOrInterfaceDeclaration compilationUnit, BasicCollectionBluePrint<?> bluePrint,
			SignatureType signature) {

		MapBluePrint map = (MapBluePrint) bluePrint;

		Type type = createInterfaceType(map, signature);
		ObjectCreationExpr initalizer = new ObjectCreationExpr(null, createImplementationType(map),
				NodeList.nodeList());

		String name = namingService.getFieldName(map);

		compilationUnit.addFieldWithInitializer(type, name, initalizer, Keyword.PRIVATE);
	}

	@Override
	public void createCollection(BlockStmt statementTree, BasicCollectionBluePrint<?> bluePrint,
			SignatureType signature, boolean isField) {
		LOGGER.debug("starting generation of Map:" + bluePrint);

		if (bluePrint.isNotBuild()) {

			createComplexElements(statementTree, bluePrint, signature);

			String name = isField ? namingService.getFieldName(bluePrint)
					: namingService.getLocalName(statementTree, bluePrint);

			MapBluePrint map = (MapBluePrint) bluePrint;

			if (!isField) {
				Type interfaceType = createInterfaceType(map, signature);

				ClassOrInterfaceType implementationType = createImplementationType(map);

				ObjectCreationExpr objectCreation = new ObjectCreationExpr(null, implementationType,
						NodeList.nodeList());

				statementTree.addStatement(
						new VariableDeclarationExpr(new VariableDeclarator(interfaceType, name, objectCreation)));
			}

			Expression accessExpr = isField ? new FieldAccessExpr(new ThisExpr(), name) : new NameExpr(name);

			for (Entry<BluePrint, BluePrint> mapEntry : map.getBluePrints()) {
				LOGGER.debug("Start generating entries of map " + mapEntry);

				Expression exprKey = getExpressionForElement(statementTree, mapEntry.getKey());
				Expression exprValue = getExpressionForElement(statementTree, mapEntry.getValue());

				statementTree.addStatement(new MethodCallExpr(accessExpr, "put", //
						NodeList.nodeList(exprKey, exprValue)));
			}

			statementTree.addStatement(new EmptyStmt());

			bluePrint.setBuild();
		}

	}

	@Override
	public void createComplexElements(BlockStmt statementTree, BasicCollectionBluePrint<?> bluePrint,
			SignatureType signature) {

		SignatureType signatureKey = signature != null ? signature.getSubTypes().get(0) : null;

		MapBluePrint map = (MapBluePrint) bluePrint;

		for (BluePrint key : map.getComplexKeys()) {
			generateComplexChildOfCollection(statementTree, key,
					signatureKey != null && signatureKey.isSimpleSignature() ? null : signatureKey);
		}

		SignatureType signatureValue = signature != null && signature.getSubTypes().size() == 2
				? signature.getSubTypes().get(1)
				: null;

		for (BluePrint value : map.getComplexValues()) {
			generateComplexChildOfCollection(statementTree, value,
					signatureValue != null && signatureValue.isSimpleSignature() ? null : signatureValue);
		}

	}

	@Override
	public void addCollectionToObject(BlockStmt statementTree, BasicCollectionBluePrint<?> bluePrint,
			boolean isField, SetterMethodData setter, Expression accessExpr) {

		String name = isField ? namingService.getFieldName(bluePrint)
				: namingService.getLocalName(statementTree, bluePrint);

		SetterType setterType = setter.getType();

		Expression mapExpr = isField ? new FieldAccessExpr(new ThisExpr(), name) : new NameExpr(name);

		if (SetterType.VALUE_SETTER == setterType)
			statementTree.addStatement(new MethodCallExpr(accessExpr, setter.getName(), NodeList.nodeList(mapExpr)));

		else if (SetterType.VALUE_GETTER == setterType)
			statementTree.addStatement(new MethodCallExpr(new MethodCallExpr(accessExpr, setter.getName()), "putAll",
					NodeList.nodeList(mapExpr)));

		else if (SetterType.COLLECTION_SETTER == setterType) {
			MapBluePrint map = (MapBluePrint) bluePrint;

			for (Entry<BluePrint, BluePrint> mapEntry : map.getBluePrints()) {
				Expression exprKey = getExpressionForElement(statementTree, mapEntry.getKey());
				Expression exprValue = getExpressionForElement(statementTree, mapEntry.getValue());

				statementTree.addStatement(new MethodCallExpr(accessExpr, setter.getName(), //
						NodeList.nodeList(exprKey, exprValue)));
			}

		}
	}

	private Type createInterfaceType(MapBluePrint map, SignatureType signature) {
		Class<?> interfaceClass = map.getInterfaceClass();

		importCallbackHandler.accept(interfaceClass);

		if (signature != null) {

			return JavaParserHelper.generateSignature(signature, importCallbackHandler);
		} else {
			ClassOrInterfaceType type = new ClassOrInterfaceType(null, interfaceClass.getSimpleName());

			ClassOrInterfaceType object = new ClassOrInterfaceType(null, Object.class.getSimpleName());
			type.setTypeArguments(object, object);

			return type;
		}
	}

	private ClassOrInterfaceType createImplementationType(MapBluePrint map) {
		Class<?> implementationClass = map.getImplementationClass();

		if (Modifier.isPublic(implementationClass.getModifiers()))
			return emptyGenericParameters(implementationClass);

		return emptyGenericParameters(HashMap.class);
	}

}

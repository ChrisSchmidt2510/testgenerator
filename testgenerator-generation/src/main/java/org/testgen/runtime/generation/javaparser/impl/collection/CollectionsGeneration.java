package org.testgen.runtime.generation.javaparser.impl.collection;

import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.testgen.logging.LogManager;
import org.testgen.logging.Logger;
import org.testgen.runtime.classdata.model.SetterMethodData;
import org.testgen.runtime.classdata.model.SetterType;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;
import org.testgen.runtime.generation.javaparser.impl.JavaParserHelper;
import org.testgen.runtime.valuetracker.blueprint.BasicCollectionBluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.collections.CollectionBluePrint;

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

public class CollectionsGeneration extends BasicCollectionGeneration {

	private static final Logger LOGGER = LogManager.getLogger(CollectionsGeneration.class);

	private static final List<Class<?>> SUPPORTED_TYPES = Collections
			.unmodifiableList(Arrays.asList(List.class, Set.class, Queue.class, Deque.class, Collection.class));

	@Override
	public boolean canGenerateBluePrint(BasicCollectionBluePrint<?> bluePrint) {
		return bluePrint instanceof CollectionBluePrint && SUPPORTED_TYPES.contains(bluePrint.getInterfaceClass());
	}

	@Override
	public void createField(ClassOrInterfaceDeclaration compilationUnit, BasicCollectionBluePrint<?> bluePrint,
			SignatureType signature) {
		String fieldName = namingService.getFieldName(bluePrint);

		CollectionBluePrint collection = (CollectionBluePrint) bluePrint;

		Type type = createInterfaceType(collection, signature);
		ClassOrInterfaceType implType = createImplementationType(collection);

		ObjectCreationExpr objectCreation = new ObjectCreationExpr(null, implType, NodeList.nodeList());

		compilationUnit.addFieldWithInitializer(type, fieldName, objectCreation, Keyword.PRIVATE);
	}

	@Override
	public void createCollection(BlockStmt statementTree, BasicCollectionBluePrint<?> bluePrint,
			SignatureType signature, boolean isField) {
		LOGGER.debug("starting generation of Collection:" + bluePrint);

		if (bluePrint.isNotBuild()) {

			createComplexElements(statementTree, bluePrint, signature);

			CollectionBluePrint collection = (CollectionBluePrint) bluePrint;

			String name = isField ? namingService.getFieldName(collection)
					: namingService.getLocalName(statementTree, collection);

			if (!isField) {
				Type interfaceType = createInterfaceType(collection, signature);

				ClassOrInterfaceType implementationType = createImplementationType(collection);

				ObjectCreationExpr objectCreation = new ObjectCreationExpr(null, implementationType,
						NodeList.nodeList());

				statementTree.addStatement(
						new VariableDeclarationExpr(new VariableDeclarator(interfaceType, name, objectCreation)));
			}

			Expression accessExpr = isField ? new FieldAccessExpr(new ThisExpr(), name) : new NameExpr(name);

			for (BluePrint child : collection.getBluePrints()) {
				LOGGER.debug("starting generation of Child: " + child);

				Expression expr = getExpressionForElement(statementTree, child);
				statementTree.addStatement(new MethodCallExpr(accessExpr, "add", //
						NodeList.nodeList(expr)));
			}

			statementTree.addStatement(new EmptyStmt());

			bluePrint.setBuild();
		}

	}

	@Override
	public void createComplexElements(BlockStmt statementTree, BasicCollectionBluePrint<?> bluePrint,
			SignatureType signature) {

		SignatureType genericType = signature != null ? signature.getSubTypes().get(0) : null;

		for (BluePrint child : bluePrint.getPreExecuteBluePrints()) {
			generateComplexChildOfCollection(statementTree, child,
					genericType != null && genericType.isSimpleSignature() ? null : genericType);
		}

	}

	@Override
	public void addCollectionToObject(BlockStmt statementTree, BasicCollectionBluePrint<?> bluePrint,
			boolean isField, SetterMethodData setter, Expression accessExpr) {
		String name = isField ? namingService.getFieldName(bluePrint)
				: namingService.getLocalName(statementTree, bluePrint);

		SetterType setterType = setter.getType();

		Expression collectionExpr = isField ? new FieldAccessExpr(new ThisExpr(), name) : new NameExpr(name);

		if (SetterType.VALUE_SETTER == setterType)
			statementTree
					.addStatement(new MethodCallExpr(accessExpr, setter.getName(), NodeList.nodeList(collectionExpr)));

		else if (SetterType.VALUE_GETTER == setterType)
			statementTree.addStatement(new MethodCallExpr(new MethodCallExpr(accessExpr, setter.getName()), "addAll",
					NodeList.nodeList(collectionExpr)));

		else if (SetterType.COLLECTION_SETTER == setterType) {
			CollectionBluePrint collection = (CollectionBluePrint) bluePrint;

			for (BluePrint child : collection.getBluePrints()) {

				Expression expr = getExpressionForElement(statementTree, child);

				statementTree.addStatement((new MethodCallExpr(accessExpr, setter.getName(), //
						NodeList.nodeList(expr))));
			}
		}

	}

	private Type createInterfaceType(CollectionBluePrint bluePrint, SignatureType signature) {

		if (signature != null) {

			importCallbackHandler.accept(signature.getType());

			return JavaParserHelper.generateSignature(signature, importCallbackHandler);
		} else {
			importCallbackHandler.accept(bluePrint.getInterfaceClass());

			ClassOrInterfaceType type = new ClassOrInterfaceType(null, bluePrint.getInterfaceClass().getSimpleName());
			type.setTypeArguments(new ClassOrInterfaceType(null, Object.class.getSimpleName()));

			return type;
		}
	}

	private ClassOrInterfaceType createImplementationType(CollectionBluePrint bluePrint) {
		Class<?> implementationClass = bluePrint.getImplementationClass();

		if (Modifier.isPublic(implementationClass.getModifiers()))
			return emptyGenericParameters(implementationClass);

		Class<?> interfaceClass = bluePrint.getInterfaceClass();

		if (List.class.equals(interfaceClass) || Collection.class.equals(interfaceClass))
			return emptyGenericParameters(ArrayList.class);

		else if (Set.class.equals(interfaceClass))
			return emptyGenericParameters(HashSet.class);

		else if (Queue.class.equals(interfaceClass) || Deque.class.equals(interfaceClass))
			return emptyGenericParameters(ArrayDeque.class);

		throw new IllegalArgumentException("unsupported Interfaceclass " + interfaceClass);
	}

}

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
import org.testgen.runtime.classdata.model.FieldData;
import org.testgen.runtime.classdata.model.SetterMethodData;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;
import org.testgen.runtime.generation.javaparser.impl.JavaParserHelper;
import org.testgen.runtime.valuetracker.blueprint.AbstractBasicCollectionBluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.collections.CollectionBluePrint;

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
import com.github.javaparser.ast.type.ClassOrInterfaceType;

public class CollectionsGeneration extends DefaultCollectionGeneration {

	private static final Logger LOGGER = LogManager.getLogger(CollectionsGeneration.class);

	private static final List<Class<?>> SUPPORTED_TYPES = Collections
			.unmodifiableList(Arrays.asList(List.class, Set.class, Queue.class, Deque.class, Collection.class));

	@Override
	public boolean canGenerateBluePrint(AbstractBasicCollectionBluePrint<?> bluePrint) {
		return bluePrint instanceof CollectionBluePrint && SUPPORTED_TYPES.contains(bluePrint.getInterfaceClass());
	}

	@Override
	public void createField(ClassOrInterfaceDeclaration compilationUnit, AbstractBasicCollectionBluePrint<?> bluePrint,
			SignatureType signature) {

	}

	@Override
	public void createCollection(BlockStmt statementTree, AbstractBasicCollectionBluePrint<?> bluePrint,
			SignatureType signature, boolean onlyCreateCollectionElements, boolean isField) {
		LOGGER.debug("starting generation of Collection:" + bluePrint);

		if (bluePrint.isNotBuild()) {
			CollectionBluePrint collection = (CollectionBluePrint) bluePrint;

			SignatureType genericType = signature != null ? signature.getSubTypes().get(0) : null;

			for (BluePrint child : collection.getBluePrints()) {
				generateComplexChildOfCollection(statementTree, child,
						genericType != null && genericType.isSimpleSignature() ? null : genericType);
			}

			String name = isField ? namingService.getFieldName(collection)
					: namingService.getLocalName(statementTree, collection);

			if (!isField) {
				ClassOrInterfaceType interfaceType = createInterfaceType(collection, signature);

				ClassOrInterfaceType implementationType = createImplementationType(collection);

				ObjectCreationExpr objectCreation = new ObjectCreationExpr(null, implementationType,
						NodeList.nodeList());

				statementTree.addStatement(
						new VariableDeclarationExpr(new VariableDeclarator(interfaceType, name, objectCreation)));
			}

			Expression accessExpr = isField ? new FieldAccessExpr(new ThisExpr(), name) : new NameExpr(name);

			for (BluePrint child : collection.getBluePrints()) {

				if (child.isComplexType()) {
					String localName = namingService.getLocalName(statementTree, child);
					MethodCallExpr addMethod = new MethodCallExpr(accessExpr, "add",
							NodeList.nodeList(new NameExpr(localName)));

					statementTree.addStatement(addMethod);

				} else if (child.isSimpleBluePrint()) {
					SimpleBluePrint<?> simpleBluePrint = child.castToSimpleBluePrint();

					Expression expr = simpleGenerationFactory.createInlineObject(simpleBluePrint);
					MethodCallExpr addMethod = new MethodCallExpr(accessExpr, "add", NodeList.nodeList(expr));

					statementTree.addStatement(addMethod);
				}
			}
		}

	}

	@Override
	public void addCollectionToObject(BlockStmt statementTree, AbstractBasicCollectionBluePrint<?> collectionBP,
			SetterMethodData setter, String objectName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addCollectionToObject(BlockStmt statementTree, AbstractBasicCollectionBluePrint<?> collectionBP,
			FieldData field, String objectName) {
		// TODO Auto-generated method stub

	}

	private ClassOrInterfaceType createInterfaceType(CollectionBluePrint bluePrint, SignatureType signature) {
		importCallbackHandler.accept(bluePrint.getInterfaceClass());

		if (signature != null) {

			return JavaParserHelper.generateGenericSignature(signature);
		} else {
			ClassOrInterfaceType type = new ClassOrInterfaceType(null, bluePrint.getInterfaceClass().getSimpleName());
			type.setTypeArguments(new ClassOrInterfaceType(null, Object.class.getSimpleName()));

			return type;
		}
	}

	private ClassOrInterfaceType createImplementationType(CollectionBluePrint bluePrint) {
		Class<?> implementationClass = bluePrint.getImplementationClass();

		if (Modifier.isPublic(implementationClass.getModifiers()))
			return mapClass(implementationClass);

		Class<?> interfaceClass = bluePrint.getInterfaceClass();

		if (List.class.equals(interfaceClass) || Collection.class.equals(interfaceClass))
			return mapClass(ArrayList.class);

		else if (Set.class.equals(interfaceClass))
			return mapClass(HashSet.class);

		else if (Queue.class.equals(interfaceClass) || Deque.class.equals(interfaceClass))
			return mapClass(ArrayDeque.class);

		throw new IllegalArgumentException("unsupported Interfaceclass " + interfaceClass);
	}

	private ClassOrInterfaceType mapClass(Class<?> clazz) {
		importCallbackHandler.accept(clazz);

		ClassOrInterfaceType type = new ClassOrInterfaceType(null, clazz.getSimpleName());
		type.setTypeArguments(new NodeList<>());

		return type;
	}

}

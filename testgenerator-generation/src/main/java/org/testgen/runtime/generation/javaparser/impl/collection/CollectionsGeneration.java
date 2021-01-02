package org.testgen.runtime.generation.javaparser.impl.collection;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.testgen.runtime.classdata.model.FieldData;
import org.testgen.runtime.classdata.model.SetterMethodData;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;
import org.testgen.runtime.generation.api.ArrayGeneration;
import org.testgen.runtime.generation.api.ComplexObjectGeneration;
import org.testgen.runtime.generation.api.collections.CollectionGeneration;
import org.testgen.runtime.generation.api.collections.CollectionGenerationFactory;
import org.testgen.runtime.generation.api.naming.NamingService;
import org.testgen.runtime.generation.api.simple.SimpleObjectGenerationFactory;
import org.testgen.runtime.valuetracker.blueprint.AbstractBasicCollectionBluePrint;
import org.testgen.runtime.valuetracker.blueprint.collections.CollectionBluePrint;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;

public class CollectionsGeneration implements CollectionGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> {

	private static final List<Class<?>> SUPPORTED_TYPES = Collections
			.unmodifiableList(Arrays.asList(List.class, Set.class, Deque.class, Collection.class));

	private ComplexObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> complexGeneration = getComplexObjectGeneration();

	private ArrayGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> arrayGeneration = getArrayGeneration();

	private SimpleObjectGenerationFactory<ClassOrInterfaceDeclaration, BlockStmt, Expression> simpleGenerationFactory = getSimpleObjectGenerationFactory();

	private CollectionGenerationFactory<ClassOrInterfaceDeclaration, BlockStmt, Expression> collectionGenerationFactory = getCollectionGenerationFactory();

	private NamingService<BlockStmt> namingService = getNamingService();

	private Consumer<Class<?>> importCallbackHandler = getImportCallBackHandler();

	@Override
	public boolean canGenerateBluePrint(AbstractBasicCollectionBluePrint<?> bluePrint) {
		return bluePrint instanceof CollectionBluePrint && SUPPORTED_TYPES.contains(bluePrint.getInterfaceClass());
	}

	@Override
	public void createField(ClassOrInterfaceDeclaration compilationUnit, AbstractBasicCollectionBluePrint<?> bluePrint,
			SignatureType signature) {
		// TODO Auto-generated method stub

	}

	@Override
	public void createCollection(BlockStmt statementTree, AbstractBasicCollectionBluePrint<?> basicCollectionBP,
			SignatureType signature, boolean onlyCreateCollectionElements, boolean isField) {
		// TODO Auto-generated method stub

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

}

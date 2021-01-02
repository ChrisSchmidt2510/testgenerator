package org.testgen.runtime.generation.javaparser.impl.collection;

import java.util.Map;
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
import org.testgen.runtime.valuetracker.blueprint.collections.MapBluePrint;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;

public class MapGeneration implements CollectionGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> {

	private ComplexObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> complexGeneration = getComplexObjectGeneration();

	private ArrayGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> arrayGeneration = getArrayGeneration();

	private SimpleObjectGenerationFactory<ClassOrInterfaceDeclaration, BlockStmt, Expression> simpleGenerationFactory = getSimpleObjectGenerationFactory();

	private CollectionGenerationFactory<ClassOrInterfaceDeclaration, BlockStmt, Expression> collectionGenerationFactory = getCollectionGenerationFactory();

	private NamingService<BlockStmt> namingService = getNamingService();

	private Consumer<Class<?>> importCallbackHandler = getImportCallBackHandler();

	@Override
	public boolean canGenerateBluePrint(AbstractBasicCollectionBluePrint<?> bluePrint) {
		return bluePrint instanceof MapBluePrint && Map.class.equals(bluePrint.getInterfaceClass());
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

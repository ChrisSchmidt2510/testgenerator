package org.testgen.runtime.generation.javaparser.impl.collection;

import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;

import org.testgen.config.TestgeneratorConfig;
import org.testgen.runtime.classdata.model.ClassData;
import org.testgen.runtime.classdata.model.FieldData;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;
import org.testgen.runtime.generation.api.ArrayGeneration;
import org.testgen.runtime.generation.api.ComplexObjectGeneration;
import org.testgen.runtime.generation.api.collections.CollectionGeneration;
import org.testgen.runtime.generation.api.collections.CollectionGenerationFactory;
import org.testgen.runtime.generation.api.naming.NamingService;
import org.testgen.runtime.generation.api.simple.SimpleObjectGenerationFactory;
import org.testgen.runtime.generation.javapoet.impl.TestGenerationHelper;
import org.testgen.runtime.valuetracker.blueprint.AbstractBasicCollectionBluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.storage.ValueStorage;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;

public abstract class DefaultCollectionGeneration
		implements CollectionGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> {

	protected ComplexObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> complexGeneration = getComplexObjectGeneration();

	protected ArrayGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> arrayGeneration = getArrayGeneration();

	protected SimpleObjectGenerationFactory<ClassOrInterfaceDeclaration, BlockStmt, Expression> simpleGenerationFactory = getSimpleObjectGenerationFactory();

	protected CollectionGenerationFactory<ClassOrInterfaceDeclaration, BlockStmt, Expression> collectionGenerationFactory = getCollectionGenerationFactory();

	protected NamingService<BlockStmt> namingService = getNamingService();

	protected Consumer<Class<?>> importCallbackHandler = getImportCallBackHandler();

	protected void generateComplexChildOfCollection(BlockStmt statementTree, BluePrint child, SignatureType signature) {
		if (child.isComplexBluePrint() && child.isNotBuild()) {
			ClassData classData = TestGenerationHelper.getClassData(child.getReference());

			Set<FieldData> calledFields = Collections.emptySet();
			if (TestgeneratorConfig.traceReadFieldAccess()) {
				calledFields = TestGenerationHelper.getCalledFields(child.getReference());
			}

			complexGeneration.createObject(statementTree, child.castToComplexBluePrint(),
					ValueStorage.getInstance().getMethodParameters().contains(child), classData, calledFields);

		} else if (child.isCollectionBluePrint() && child.isNotBuild()) {
			AbstractBasicCollectionBluePrint<?> collection = child.castToCollectionBluePrint();

			collectionGenerationFactory.createCollection(statementTree, collection, signature, //
					false, ValueStorage.getInstance().getMethodParameters().contains(child));

		} else if (child.isArrayBluePrint() && child.isNotBuild()) {
			arrayGeneration.createArray(statementTree, child.castToArrayBluePrint(), false,
					ValueStorage.getInstance().getMethodParameters().contains(child));

		}
	}

}

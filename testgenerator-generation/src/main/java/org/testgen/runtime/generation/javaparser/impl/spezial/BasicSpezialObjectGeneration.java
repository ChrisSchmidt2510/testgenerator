package org.testgen.runtime.generation.javaparser.impl.spezial;

import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;

import org.testgen.config.TestgeneratorConfig;
import org.testgen.runtime.classdata.model.ClassData;
import org.testgen.runtime.classdata.model.FieldData;
import org.testgen.runtime.generation.api.ArrayGeneration;
import org.testgen.runtime.generation.api.ComplexObjectGeneration;
import org.testgen.runtime.generation.api.GenerationHelper;
import org.testgen.runtime.generation.api.collections.CollectionGenerationFactory;
import org.testgen.runtime.generation.api.naming.NamingService;
import org.testgen.runtime.generation.api.simple.SimpleObjectGenerationFactory;
import org.testgen.runtime.generation.api.spezial.SpezialObjectGeneration;
import org.testgen.runtime.generation.api.spezial.SpezialObjectGenerationFactory;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.complextypes.ComplexBluePrint;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

public abstract class BasicSpezialObjectGeneration<B extends BluePrint> implements SpezialObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression, B>{

	protected SimpleObjectGenerationFactory<ClassOrInterfaceDeclaration, BlockStmt, Expression> simpleGenerationFactory;

	protected CollectionGenerationFactory<ClassOrInterfaceDeclaration, BlockStmt, Expression> collectionGenerationFactory;
	
	protected ArrayGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> arrayGeneration;
	
	protected ComplexObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> complexObjectGeneration;
	
	protected SpezialObjectGenerationFactory<ClassOrInterfaceDeclaration, BlockStmt, Expression, BluePrint> spezialGenerationFactory;
	
	protected Consumer<Class<?>> importCallBackHandler;
	
	protected NamingService<BlockStmt> namingService = getNamingService();
	
	protected ClassOrInterfaceType getType(Class<?> type) {
		importCallBackHandler.accept(type);
		
		return new ClassOrInterfaceType(null, type.getSimpleName());
	}
	
	protected void createComplexObject(BlockStmt codeBlock, ComplexBluePrint bluePrint) {
		ClassData classData = GenerationHelper.getClassData(bluePrint.getReference());

		Set<FieldData> calledFields = Collections.emptySet();
		if (TestgeneratorConfig.traceReadFieldAccess()) {
			calledFields = GenerationHelper.getCalledFields(bluePrint.getReference());
		}

		complexObjectGeneration.createObject(codeBlock, bluePrint.castToComplexBluePrint(), namingService.existsField(bluePrint), classData,
				calledFields);
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
		this.complexObjectGeneration = complexObjectGeneration;
	}

	@Override
	public void setImportCallBackHandler(Consumer<Class<?>> importCallBackHandler) {
		this.importCallBackHandler = importCallBackHandler;
	}

	@Override
	public void setSpezialObjectGenerationFactory(
			SpezialObjectGenerationFactory<ClassOrInterfaceDeclaration, BlockStmt, Expression, BluePrint> spezialGenerationFactory) {
		this.spezialGenerationFactory = spezialGenerationFactory;
	}

	@Override
	public void setArrayGenerationFactory(
			ArrayGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> arrayGeneration) {
		this.arrayGeneration = arrayGeneration;
	}
}

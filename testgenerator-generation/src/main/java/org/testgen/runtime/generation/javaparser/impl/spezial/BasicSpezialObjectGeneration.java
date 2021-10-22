package org.testgen.runtime.generation.javaparser.impl.spezial;

import java.util.function.Consumer;

import org.testgen.runtime.generation.api.ArrayGeneration;
import org.testgen.runtime.generation.api.ComplexObjectGeneration;
import org.testgen.runtime.generation.api.collections.CollectionGenerationFactory;
import org.testgen.runtime.generation.api.naming.NamingService;
import org.testgen.runtime.generation.api.simple.SimpleObjectGenerationFactory;
import org.testgen.runtime.generation.api.spezial.SpezialObjectGeneration;
import org.testgen.runtime.generation.api.spezial.SpezialObjectGenerationFactory;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;

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
		return new ClassOrInterfaceType(null, type.getSimpleName());
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

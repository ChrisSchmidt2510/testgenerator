package org.testgen.runtime.generation.javaparser.impl.collection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Consumer;

import org.testgen.runtime.classdata.model.SetterMethodData;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;
import org.testgen.runtime.generation.api.ArrayGeneration;
import org.testgen.runtime.generation.api.ComplexObjectGeneration;
import org.testgen.runtime.generation.api.collections.CollectionGeneration;
import org.testgen.runtime.generation.api.collections.CollectionGenerationFactory;
import org.testgen.runtime.generation.api.simple.SimpleObjectGenerationFactory;
import org.testgen.runtime.valuetracker.blueprint.BasicCollectionBluePrint;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;

public class JavaParserCollectionGenerationFactory
		implements CollectionGenerationFactory<ClassOrInterfaceDeclaration, BlockStmt, Expression> {

	private final List<CollectionGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression>> generators = new ArrayList<>();

	public JavaParserCollectionGenerationFactory() {
		@SuppressWarnings("rawtypes")
		ServiceLoader<CollectionGeneration> loader = ServiceLoader.load(CollectionGeneration.class);

		loader.forEach(generators::add);

		generators.forEach(gen -> gen.setCollectionGenerationFactory(this));
	}

	CollectionGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> of(
			BasicCollectionBluePrint<?> bluePrint) {
		Optional<CollectionGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression>> generatorOptional = generators
				.stream().filter(gen -> gen.canGenerateBluePrint(bluePrint))
				.max((gen1, gen2) -> Integer.compare(gen1.getPriority(), gen2.getPriority()));

		if (generatorOptional.isPresent()) {
			return generatorOptional.get();
		}

		throw new IllegalArgumentException("cant generate AbstractBasicCollectionBluePrint " + bluePrint);
	}
	
	@Override
	public void createField(ClassOrInterfaceDeclaration compilationUnit, BasicCollectionBluePrint<?> bluePrint,
			SignatureType signature) {
		CollectionGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> generator = of(bluePrint);
		
		generator.createField(compilationUnit, bluePrint, signature);
	}

	@Override
	public void createCollection(BlockStmt statementTree, BasicCollectionBluePrint<?> bluePrint,
			SignatureType signature, boolean isField) {
		CollectionGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> generator = of(bluePrint);

		generator.createCollection(statementTree, bluePrint, signature, isField);
	}

	@Override
	public void createComplexElements(BlockStmt statementTree, BasicCollectionBluePrint<?> bluePrint,
			SignatureType signature) {
		CollectionGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> generator = of(bluePrint);

		generator.createComplexElements(statementTree, bluePrint, signature);
	}

	@Override
	public void addCollectionToObject(BlockStmt statementTree, BasicCollectionBluePrint<?> bluePrint,
			boolean isField, SetterMethodData setter, Expression accessExpr) {
		CollectionGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> generator = of(bluePrint);

		generator.addCollectionToObject(statementTree, bluePrint, isField, setter, accessExpr);
	}

	@Override
	public void addCollectionToField(BlockStmt statementTree, BasicCollectionBluePrint<?> bluePrint,
			boolean isField, Expression accessExpr) {
		CollectionGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> generator = of(bluePrint);

		generator.addCollectionToField(statementTree, bluePrint, isField, accessExpr);
	}

	@Override
	public void setSimpleObjectGenerationFactory(
			SimpleObjectGenerationFactory<ClassOrInterfaceDeclaration, BlockStmt, Expression> simpleGenerationFactory) {
		generators.forEach(gen -> gen.setSimpleObjectGenerationFactory(simpleGenerationFactory));
	}

	@Override
	public void setComplexObjectGeneration(
			ComplexObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> complexObjectGeneration) {
		generators.forEach(gen -> gen.setComplexObjectGeneration(complexObjectGeneration));

	}

	@Override
	public void setImportCallBackHandler(Consumer<Class<?>> importCallBackHandler) {
		generators.forEach(gen -> gen.setImportCallBackHandler(importCallBackHandler));
	}

	@Override
	public void setArrayGeneration(
			ArrayGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> arrayGeneration) {
		generators.forEach(gen -> gen.setArrayGeneration(arrayGeneration));

	}

}

package org.testgen.runtime.generation.javaparser.impl.spezial;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Consumer;

import org.testgen.runtime.classdata.model.descriptor.SignatureType;
import org.testgen.runtime.generation.api.ArrayGeneration;
import org.testgen.runtime.generation.api.ComplexObjectGeneration;
import org.testgen.runtime.generation.api.collections.CollectionGenerationFactory;
import org.testgen.runtime.generation.api.simple.SimpleObjectGenerationFactory;
import org.testgen.runtime.generation.api.spezial.SpezialObjectGeneration;
import org.testgen.runtime.generation.api.spezial.SpezialObjectGenerationFactory;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;

public class JavaParserSpezialGenerationFactory
		implements SpezialObjectGenerationFactory<ClassOrInterfaceDeclaration, BlockStmt, Expression, BluePrint> {

	private List<SpezialObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression, BluePrint>> factories = new ArrayList<>();

	public JavaParserSpezialGenerationFactory() {
		@SuppressWarnings("rawtypes")
		ServiceLoader<SpezialObjectGeneration> loader = ServiceLoader.load(SpezialObjectGeneration.class);

		loader.forEach(factories::add);

		factories.forEach(factory -> factory.setSpezialObjectGenerationFactory(this));
	}

	SpezialObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression, BluePrint> of(BluePrint bluePrint) {
		Optional<SpezialObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression, BluePrint>> factory = factories
				.stream().filter(gen -> gen.canGenerateBluePrint(bluePrint))
				.max((gen1, gen2) -> Integer.compare(gen1.getPriority(), gen2.getPriority()));
		
		if(factory.isPresent())
			return factory.get();
		
		throw new IllegalArgumentException("cant generate SpezialBluePrint " + bluePrint);
	}

	@Override
	public void createField(ClassOrInterfaceDeclaration compilationUnit, BluePrint bluePrint, SignatureType signature) {
		SpezialObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression, BluePrint> factory = of(bluePrint);
		
		factory.createField(compilationUnit, bluePrint, signature);
	}

	@Override
	public void createObject(BlockStmt codeBlock, BluePrint bluePrint, SignatureType signature, boolean isField) {
		SpezialObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression, BluePrint> factory = of(bluePrint);
		
		factory.createObject(codeBlock, bluePrint, signature, isField);
	}

	@Override
	public void setSimpleObjectGenerationFactory(
			SimpleObjectGenerationFactory<ClassOrInterfaceDeclaration, BlockStmt, Expression> simpleGenerationFactory) {
		factories.forEach(fac -> fac.setSimpleObjectGenerationFactory(simpleGenerationFactory));
	}

	@Override
	public void setCollectionGenerationFactory(
			CollectionGenerationFactory<ClassOrInterfaceDeclaration, BlockStmt, Expression> collectionGenerationFactory) {
		factories.forEach(fac -> fac.setCollectionGenerationFactory(collectionGenerationFactory));
	}

	@Override
	public void setArrayGenerationFactory(
			ArrayGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> arrayGeneration) {
		factories.forEach(fac -> fac.setArrayGenerationFactory(arrayGeneration));
	}

	@Override
	public void setComplexObjectGeneration(
			ComplexObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> complexObjectGeneration) {
		factories.forEach(fac -> fac.setComplexObjectGeneration(complexObjectGeneration));
	}

	@Override
	public void setImportCallBackHandler(Consumer<Class<?>> importCallBackHandler) {
		factories.forEach(fac -> fac.setImportCallBackHandler(importCallBackHandler));
	}

}

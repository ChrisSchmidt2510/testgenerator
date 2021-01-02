package org.testgen.runtime.generation.javaparser.impl.simple;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;

import org.testgen.runtime.generation.api.simple.SimpleObjectGeneration;
import org.testgen.runtime.generation.api.simple.SimpleObjectGenerationFactory;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;

public class JavaParserSimpleObjectGenerationFactory
		implements SimpleObjectGenerationFactory<ClassOrInterfaceDeclaration, BlockStmt, Expression> {

	private final List<SimpleObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression>> generators = new ArrayList<>();

	public JavaParserSimpleObjectGenerationFactory() {
		@SuppressWarnings("rawtypes")
		ServiceLoader<SimpleObjectGeneration> loader = ServiceLoader.load(SimpleObjectGeneration.class);

		loader.forEach(generators::add);
	}

	@Override
	public SimpleObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> of(SimpleBluePrint<?> bluePrint) {
		Optional<SimpleObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression>> optional = generators
				.stream().filter(gen -> gen.canGenerateBluePrint(bluePrint)).findAny();

		if (optional.isPresent()) {
			return optional.get();
		}

		throw new IllegalArgumentException("cant generate BluePrint " + bluePrint);
	}

}

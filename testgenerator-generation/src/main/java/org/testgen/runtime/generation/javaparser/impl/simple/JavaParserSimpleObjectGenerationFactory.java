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

	SimpleObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> of(SimpleBluePrint<?> bluePrint) {
		Optional<SimpleObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression>> optional = generators
				.stream().filter(gen -> gen.canGenerateBluePrint(bluePrint)).findAny();

		if (optional.isPresent()) {
			return optional.get();
		}

		throw new IllegalArgumentException("cant generate BluePrint " + bluePrint);
	}

	@Override
	public void createField(ClassOrInterfaceDeclaration compilationUnit, SimpleBluePrint<?> bluePrint,
			boolean withInitalizer) {
		SimpleObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> generator = of(bluePrint);

		generator.createField(compilationUnit, bluePrint, withInitalizer);
	}

	@Override
	public void createObject(BlockStmt statementTree, SimpleBluePrint<?> bluePrint, boolean isField) {
		SimpleObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> generator = of(bluePrint);

		generator.createObject(statementTree, bluePrint, isField);
	}

	@Override
	public Expression createInlineExpression(SimpleBluePrint<?> bluePrint) {
		SimpleObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> generator = of(bluePrint);

		return generator.createInlineExpression(bluePrint);
	}

}

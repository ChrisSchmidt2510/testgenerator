package org.testgen.runtime.generation.javaparser.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.testgen.runtime.classdata.model.ClassData;
import org.testgen.runtime.classdata.model.FieldData;
import org.testgen.runtime.classdata.model.descriptor.DescriptorType;
import org.testgen.runtime.generation.api.ArrayGeneration;
import org.testgen.runtime.generation.api.ComplexObjectGeneration;
import org.testgen.runtime.generation.api.TestClassGeneration;
import org.testgen.runtime.generation.api.collections.CollectionGeneration;
import org.testgen.runtime.generation.api.simple.SimpleObjectGeneration;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.ProxyBluePrint;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.VoidType;

public class JavaParserTestClassGeneration
		implements TestClassGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> {

	private static final String TEST = "Test";

	private CompilationUnit cu;

	private MethodDeclaration method;

	@Override
	public ClassOrInterfaceDeclaration createTestClass(Class<?> testClass) {
		cu = new CompilationUnit(testClass.getPackage().getName());

		return cu.addClass(testClass.getSimpleName() + TEST);
	}

	@Override
	public void prepareTestObject(ClassOrInterfaceDeclaration compilationUnit, BluePrint testObject,
			ClassData classData, Set<FieldData> calledFields) {

		method = new MethodDeclaration(Modifier.createModifierList(Keyword.PUBLIC), new VoidType(), null);

	}

	@Override
	public void prepareMethodParameters(ClassOrInterfaceDeclaration compilationUnit,
			Collection<BluePrint> methodParameters, List<DescriptorType> methodTypeTable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void prepareProxyObjects(ClassOrInterfaceDeclaration compilationUnit,
			Map<ProxyBluePrint, List<BluePrint>> proxyObjects) {
		// TODO Auto-generated method stub

	}

	@Override
	public void generateTestMethod(ClassOrInterfaceDeclaration compilationUnit, String methodName,
			boolean withProxyObjects) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addDocumentation(ClassOrInterfaceDeclaration compilationUnit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void toFile(ClassOrInterfaceDeclaration compilationUnit) {
		// TODO Auto-generated method stub

	}

	@Override
	public ComplexObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> createComplexObjectGeneration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SimpleObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> createSimpleObjectGeneration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CollectionGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> createCollectionGeneration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> createArrayGeneration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Consumer<Class<?>> importCallBackHandler() {
		return clazz -> cu.addImport(clazz);
	}

}

package org.testgen.runtime.generation.javaparser.impl.spezial;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.testgen.runtime.generation.api.naming.NamingServiceProvider;
import org.testgen.runtime.generation.api.simple.SimpleObjectGenerationFactory;
import org.testgen.runtime.generation.api.spezial.SpezialObjectGeneration;
import org.testgen.runtime.generation.javaparser.impl.simple.JavaParserSimpleObjectGenerationFactory;
import org.testgen.runtime.valuetracker.CurrentlyBuildedBluePrints;
import org.testgen.runtime.valuetracker.blueprint.complextypes.ProxyBluePrint;
import org.testgen.runtime.valuetracker.blueprint.complextypes.ProxyBluePrint.ProxyBluePrintFactory;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;

public class ProxySpezialObjectGenerationTest {

	private ProxyBluePrintFactory bluePrintFactory = new ProxyBluePrintFactory();
	private CurrentlyBuildedBluePrints currentlyBuildedBluePrints = new CurrentlyBuildedBluePrints();

	private Set<Class<?>> imports = new HashSet<>();
	private SpezialObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression, ProxyBluePrint> spezialGeneration = new ProxySpezialObjectGeneration();
	
	private SimpleObjectGenerationFactory<ClassOrInterfaceDeclaration, BlockStmt, Expression> simpleGenerationFactory = new JavaParserSimpleObjectGenerationFactory();
	
	@Before
	public void init() {
		spezialGeneration.setImportCallBackHandler(imports::add);
		simpleGenerationFactory.setImportCallBackHandler(imports::add);
		
		spezialGeneration.setSimpleObjectGenerationFactory(simpleGenerationFactory);
	}
	
	@After
	public void cleanUp() {
		imports.clear();

		NamingServiceProvider.getNamingService().clearFields();
	}
	
	@Test
	public void testCreateField() {
		Object proxyWithMultipleInterfaces = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
				new Class<?>[] { Greeter.class, Serializable.class }, new CustomInvocationHandler());
	
		ProxyBluePrint bluePrint = (ProxyBluePrint) bluePrintFactory.createBluePrint("proxy", proxyWithMultipleInterfaces, currentlyBuildedBluePrints, null);
		
		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");
		
		spezialGeneration.createField(cu, bluePrint, null);
		Assert.assertEquals("private Greeter proxy;", cu.getFields().get(0).toString());
	}

	@Test
	public void testSample() throws IOException {
		CompilationUnit cu = StaticJavaParser.parse(Paths.get(
				"D:\\git\\testgenerator\\testgenerator-value-tracker\\src\\test\\java\\org\\testgen\\runtime\\valuetracker\\blueprint\\complextypes\\ProxyBluePrintTest.java"));
		System.out.println(cu);
	}

	public class CustomInvocationHandler implements InvocationHandler {

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			// TODO Auto-generated method stub
			return null;
		}

	}

	public interface Greeter {

		public String greet(String name);
	}

}

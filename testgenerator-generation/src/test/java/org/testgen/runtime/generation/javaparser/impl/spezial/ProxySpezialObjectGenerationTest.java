package org.testgen.runtime.generation.javaparser.impl.spezial;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.testgen.runtime.generation.api.naming.NamingServiceProvider;
import org.testgen.runtime.generation.api.simple.SimpleObjectGenerationFactory;
import org.testgen.runtime.generation.api.spezial.SpezialObjectGeneration;
import org.testgen.runtime.generation.javaparser.impl.TestgeneratorPrettyPrinter;
import org.testgen.runtime.generation.javaparser.impl.simple.JavaParserSimpleObjectGenerationFactory;
import org.testgen.runtime.valuetracker.CurrentlyBuildedBluePrints;
import org.testgen.runtime.valuetracker.blueprint.complextypes.ProxyBluePrint;
import org.testgen.runtime.valuetracker.blueprint.complextypes.ProxyBluePrint.ProxyBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.StringBluePrint.StringBluePrintFactory;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.printer.PrettyPrinterConfiguration;

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

		ProxyBluePrint bluePrint = (ProxyBluePrint) bluePrintFactory.createBluePrint("proxy",
				proxyWithMultipleInterfaces, currentlyBuildedBluePrints, null);

		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		spezialGeneration.createField(cu, bluePrint, null);
		Assert.assertEquals("private Greeter proxy;", cu.getFields().get(0).toString());
		Assert.assertTrue(imports.contains(Greeter.class));
	}

	@Test
	public void testCreateObject() {
		Object proxyWithMultipleInterfaces = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
				new Class<?>[] { Greeter.class, Serializable.class }, new CustomInvocationHandler());

		ProxyBluePrint bluePrint = (ProxyBluePrint) bluePrintFactory.createBluePrint("proxy",
				proxyWithMultipleInterfaces, currentlyBuildedBluePrints, null);

		BlockStmt codeBlock = new BlockStmt();

		spezialGeneration.createObject(codeBlock, bluePrint, null, false);

		String expected = "// TODO add initialization of invocationHandler: CustomInvocationHandler\r\n" + //
				"Greeter proxy = (Greeter) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[] { Greeter.class, Serializable.class }, null);";
		Assert.assertEquals(expected, codeBlock.getStatement(0).toString());
		Assert.assertTrue(bluePrint.isBuild());
		Assert.assertTrue(imports.contains(Greeter.class) && imports.contains(Serializable.class)
				&& imports.contains(Proxy.class) && imports.contains(Thread.class) && imports.contains(Class.class));

		imports.clear();
		bluePrint.resetBuildState();

		spezialGeneration.createObject(codeBlock, bluePrint, null, true);

		expected = "// TODO add initialization of invocationHandler: CustomInvocationHandler\r\n" + //
				"this.proxy = (Greeter) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[] { Greeter.class, Serializable.class }, null);";
		Assert.assertEquals(expected, codeBlock.getStatement(2).toString());
		Assert.assertTrue(bluePrint.isBuild());
		Assert.assertTrue(imports.contains(Greeter.class) && imports.contains(Serializable.class)
				&& imports.contains(Proxy.class) && imports.contains(Thread.class) && imports.contains(Class.class));
	}

	@Test
	public void testCreateObjectWithChilds() {
		Object proxyWithMultipleInterfaces = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
				new Class<?>[] { Greeter.class, Serializable.class }, new CustomInvocationHandler());

		StringBluePrintFactory strFactory = new StringBluePrintFactory();

		ProxyBluePrint bluePrint = (ProxyBluePrint) bluePrintFactory.createBluePrint("proxy",
				proxyWithMultipleInterfaces, currentlyBuildedBluePrints,
				(name, value) -> strFactory.createBluePrint(name, (String) value));
		
		try {
			Method method = Greeter.class.getDeclaredMethod("greet", String.class);
			bluePrint.addProxyResult(method, "Hello World");
		} catch (NoSuchMethodException | SecurityException e) {
			Assert.fail(e.getMessage());
		}
		
		BlockStmt codeBlock = new BlockStmt();
		
		spezialGeneration.createObject(codeBlock, bluePrint, null, false);
		
		String expected ="{\r\n"+//
		"    // return value of proxy operation public abstract java.lang.String org.testgen.runtime.generation.javaparser.impl.spezial.ProxySpezialObjectGenerationTest$Greeter.greet(java.lang.String)\r\n"+//
		"    String greet = \"Hello World\";\r\n"+//
		"\r\n"+//
		"    // TODO add initialization of invocationHandler: CustomInvocationHandler\r\n"+//
		"    Greeter proxy = (Greeter) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[] { Greeter.class, Serializable.class }, null);\r\n"+//
		"\r\n"+//
		"}";
		
		PrettyPrinterConfiguration printerConfig = new PrettyPrinterConfiguration()
				.setVisitorFactory(TestgeneratorPrettyPrinter::new);
		
		Assert.assertEquals(expected, codeBlock.toString(printerConfig));
		Assert.assertTrue(bluePrint.isBuild());
		Assert.assertTrue(imports.contains(Greeter.class) && imports.contains(Serializable.class)
				&& imports.contains(Proxy.class) && imports.contains(Thread.class) && imports.contains(Class.class));
		
		bluePrint.resetBuildState();
		imports.clear();
		
		BlockStmt block = new BlockStmt();
		
		spezialGeneration.createObject(block, bluePrint, null, true);
		
		expected ="{\r\n"+//
				"    // return value of proxy operation public abstract java.lang.String org.testgen.runtime.generation.javaparser.impl.spezial.ProxySpezialObjectGenerationTest$Greeter.greet(java.lang.String)\r\n"+//
				"    String greet = \"Hello World\";\r\n"+//
				"\r\n"+//
				"    // TODO add initialization of invocationHandler: CustomInvocationHandler\r\n"+//
				"    this.proxy = (Greeter) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[] { Greeter.class, Serializable.class }, null);\r\n"+//
				"\r\n"+//
				"}";
		Assert.assertEquals(expected, block.toString(printerConfig));
		Assert.assertTrue(bluePrint.isBuild());
		Assert.assertTrue(imports.contains(Greeter.class) && imports.contains(Serializable.class)
				&& imports.contains(Proxy.class) && imports.contains(Thread.class) && imports.contains(Class.class));
	
	bluePrint.resetBuildState();
	imports.clear();
	
	bluePrint.getProxyResults().get(0).getValue().setBuild();
	
	BlockStmt codeBlock2 = new BlockStmt();
	
	spezialGeneration.createObject(codeBlock2, bluePrint, null, false);
	
	expected = "{\r\n" + 
			"    // return value of proxy operation public abstract java.lang.String org.testgen.runtime.generation.javaparser.impl.spezial.ProxySpezialObjectGenerationTest$Greeter.greet(java.lang.String) greet already created\r\n" + 
			"    // TODO add initialization of invocationHandler: CustomInvocationHandler\r\n" + 
			"    Greeter proxy = (Greeter) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[] { Greeter.class, Serializable.class }, null);\r\n" + 
			"\r\n" + 
			"}";
	Assert.assertEquals(expected, codeBlock2.toString(printerConfig));
	Assert.assertTrue(bluePrint.isBuild());
	Assert.assertTrue(imports.contains(Greeter.class) && imports.contains(Serializable.class)
			&& imports.contains(Proxy.class) && imports.contains(Thread.class) && imports.contains(Class.class));

	}

	public static class CustomInvocationHandler implements InvocationHandler {

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

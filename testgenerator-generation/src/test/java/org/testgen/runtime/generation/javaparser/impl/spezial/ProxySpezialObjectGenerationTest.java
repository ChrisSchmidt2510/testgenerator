package org.testgen.runtime.generation.javaparser.impl.spezial;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testgen.core.CurrentlyBuiltQueue;
import org.testgen.runtime.generation.api.naming.NamingServiceProvider;
import org.testgen.runtime.generation.api.simple.SimpleObjectGenerationFactory;
import org.testgen.runtime.generation.api.spezial.SpezialObjectGeneration;
import org.testgen.runtime.generation.javaparser.impl.TestgeneratorPrettyPrinter;
import org.testgen.runtime.generation.javaparser.impl.simple.JavaParserSimpleObjectGenerationFactory;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.complextypes.ProxyBluePrint;
import org.testgen.runtime.valuetracker.blueprint.complextypes.ProxyBluePrint.ProxyBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.StringBluePrint.StringBluePrintFactory;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.printer.DefaultPrettyPrinter;
import com.github.javaparser.printer.configuration.DefaultPrinterConfiguration;

public class ProxySpezialObjectGenerationTest {

	private ProxyBluePrintFactory bluePrintFactory = new ProxyBluePrintFactory();
	private CurrentlyBuiltQueue<BluePrint> currentlyBuiltQueue = new CurrentlyBuiltQueue<>();

	private Set<Class<?>> imports = new HashSet<>();
	private SpezialObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression, ProxyBluePrint> spezialGeneration = new ProxySpezialObjectGeneration();

	private SimpleObjectGenerationFactory<ClassOrInterfaceDeclaration, BlockStmt, Expression> simpleGenerationFactory = new JavaParserSimpleObjectGenerationFactory();

	private DefaultPrettyPrinter printer = new DefaultPrettyPrinter((config) -> new TestgeneratorPrettyPrinter(config),
			new DefaultPrinterConfiguration());

	@BeforeEach
	public void init() {
		spezialGeneration.setImportCallBackHandler(imports::add);
		simpleGenerationFactory.setImportCallBackHandler(imports::add);

		spezialGeneration.setSimpleObjectGenerationFactory(simpleGenerationFactory);
	}

	@AfterEach
	public void cleanUp() {
		imports.clear();

		NamingServiceProvider.getNamingService().clearFields();
	}

	@Test
	public void testCreateField() {
		Object proxyWithMultipleInterfaces = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
				new Class<?>[] { Greeter.class, Serializable.class }, new CustomInvocationHandler());

		ProxyBluePrint bluePrint = (ProxyBluePrint) bluePrintFactory.createBluePrint("proxy",
				proxyWithMultipleInterfaces, currentlyBuiltQueue, null);

		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		spezialGeneration.createField(cu, bluePrint, null);
		assertEquals("private Greeter proxy;", cu.getFields().get(0).toString());
		assertTrue(imports.contains(Greeter.class));
	}

	@Test
	public void testCreateObject() {
		Object proxyWithMultipleInterfaces = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
				new Class<?>[] { Greeter.class, Serializable.class }, new CustomInvocationHandler());

		ProxyBluePrint bluePrint = (ProxyBluePrint) bluePrintFactory.createBluePrint("proxy",
				proxyWithMultipleInterfaces, currentlyBuiltQueue, null);

		BlockStmt codeBlock = new BlockStmt();

		spezialGeneration.createObject(codeBlock, bluePrint, null, false);

		String expected = "// TODO add initialization of invocationHandler: CustomInvocationHandler\r\n" + //
				"Greeter proxy = (Greeter) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[] { Greeter.class, Serializable.class }, null);";
		assertEquals(expected, codeBlock.getStatement(0).toString());
		assertTrue(bluePrint.isBuild());
		assertTrue(imports.contains(Greeter.class) && imports.contains(Serializable.class)
				&& imports.contains(Proxy.class) && imports.contains(Thread.class) && imports.contains(Class.class));

		imports.clear();
		bluePrint.resetBuildState();

		spezialGeneration.createObject(codeBlock, bluePrint, null, true);

		expected = "// TODO add initialization of invocationHandler: CustomInvocationHandler\r\n" + //
				"this.proxy = (Greeter) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[] { Greeter.class, Serializable.class }, null);";
		assertEquals(expected, codeBlock.getStatement(2).toString());
		assertTrue(bluePrint.isBuild());
		assertTrue(imports.contains(Greeter.class) && imports.contains(Serializable.class)
				&& imports.contains(Proxy.class) && imports.contains(Thread.class) && imports.contains(Class.class));
	}

	@Test
	public void testCreateObjectWithChilds() {
		Object proxyWithMultipleInterfaces = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
				new Class<?>[] { Greeter.class, Serializable.class }, new CustomInvocationHandler());

		StringBluePrintFactory strFactory = new StringBluePrintFactory();

		ProxyBluePrint bluePrint = (ProxyBluePrint) bluePrintFactory.createBluePrint("proxy",
				proxyWithMultipleInterfaces, currentlyBuiltQueue,
				(name, value) -> strFactory.createBluePrint(name, (String) value));

		try {
			Method method = Greeter.class.getDeclaredMethod("greet", String.class);
			bluePrint.addProxyResult(method, "Hello World");
		} catch (NoSuchMethodException | SecurityException e) {
			fail(e);
		}

		BlockStmt codeBlock = new BlockStmt();

		spezialGeneration.createObject(codeBlock, bluePrint, null, false);

		String expected = "{\r\n" + //
				"    // return value of proxy operation public abstract java.lang.String org.testgen.runtime.generation.javaparser.impl.spezial.ProxySpezialObjectGenerationTest$Greeter.greet(java.lang.String)\r\n"
				+ //
				"    String greet = \"Hello World\";\r\n" + //
				"\r\n" + //
				"    // TODO add initialization of invocationHandler: CustomInvocationHandler\r\n" + //
				"    Greeter proxy = (Greeter) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[] { Greeter.class, Serializable.class }, null);\r\n"
				+ //
				"\r\n" + //
				"}";

		assertEquals(expected, printer.print(codeBlock));
		assertTrue(bluePrint.isBuild());
		assertTrue(imports.contains(Greeter.class) && imports.contains(Serializable.class)
				&& imports.contains(Proxy.class) && imports.contains(Thread.class) && imports.contains(Class.class));

		bluePrint.resetBuildState();
		imports.clear();

		BlockStmt block = new BlockStmt();

		spezialGeneration.createObject(block, bluePrint, null, true);

		expected = "{\r\n" + //
				"    // return value of proxy operation public abstract java.lang.String org.testgen.runtime.generation.javaparser.impl.spezial.ProxySpezialObjectGenerationTest$Greeter.greet(java.lang.String)\r\n"
				+ //
				"    String greet = \"Hello World\";\r\n" + //
				"\r\n" + //
				"    // TODO add initialization of invocationHandler: CustomInvocationHandler\r\n" + //
				"    this.proxy = (Greeter) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[] { Greeter.class, Serializable.class }, null);\r\n"
				+ //
				"\r\n" + //
				"}";
		assertEquals(expected, printer.print(block));
		assertTrue(bluePrint.isBuild());
		assertTrue(imports.contains(Greeter.class) && imports.contains(Serializable.class)
				&& imports.contains(Proxy.class) && imports.contains(Thread.class) && imports.contains(Class.class));

		bluePrint.resetBuildState();
		imports.clear();

		bluePrint.getProxyResults().get(0).getValue().setBuild();

		BlockStmt codeBlock2 = new BlockStmt();

		spezialGeneration.createObject(codeBlock2, bluePrint, null, false);

		expected = "{\r\n"
				+ "    // return value of proxy operation public abstract java.lang.String org.testgen.runtime.generation.javaparser.impl.spezial.ProxySpezialObjectGenerationTest$Greeter.greet(java.lang.String) greet already created\r\n"
				+ "    // TODO add initialization of invocationHandler: CustomInvocationHandler\r\n"
				+ "    Greeter proxy = (Greeter) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[] { Greeter.class, Serializable.class }, null);\r\n"
				+ "\r\n" + "}";
		assertEquals(expected, printer.print(codeBlock2));
		assertTrue(bluePrint.isBuild());
		assertTrue(imports.contains(Greeter.class) && imports.contains(Serializable.class)
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

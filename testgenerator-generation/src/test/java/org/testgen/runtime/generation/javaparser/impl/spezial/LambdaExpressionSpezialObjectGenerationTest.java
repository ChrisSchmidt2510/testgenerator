package org.testgen.runtime.generation.javaparser.impl.spezial;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;
import org.testgen.runtime.generation.api.naming.NamingServiceProvider;
import org.testgen.runtime.generation.api.simple.SimpleObjectGenerationFactory;
import org.testgen.runtime.generation.api.spezial.SpezialObjectGeneration;
import org.testgen.runtime.generation.javaparser.impl.TestgeneratorPrettyPrinter;
import org.testgen.runtime.generation.javaparser.impl.simple.JavaParserSimpleObjectGenerationFactory;
import org.testgen.runtime.valuetracker.CurrentlyBuildedBluePrints;
import org.testgen.runtime.valuetracker.blueprint.complextypes.LambdaExpressionBluePrint;
import org.testgen.runtime.valuetracker.blueprint.complextypes.LambdaExpressionBluePrint.LambdaExpressionBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.NumberBluePrint.NumberBluePrintFactory;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.printer.PrettyPrinterConfiguration;

public class LambdaExpressionSpezialObjectGenerationTest {

	private LambdaExpressionBluePrintFactory factory = new LambdaExpressionBluePrintFactory();
	private CurrentlyBuildedBluePrints currentlyBuildedBluePrints = new CurrentlyBuildedBluePrints();

	private Set<Class<?>> imports = new HashSet<>();

	private SpezialObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression, LambdaExpressionBluePrint> spezialGeneration = new LambdaExpressionSpezialObjectGeneration();

	private SimpleObjectGenerationFactory<ClassOrInterfaceDeclaration, BlockStmt, Expression> simpleGenerationFactory = new JavaParserSimpleObjectGenerationFactory();

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
		@SuppressWarnings("unchecked")
		Function<Integer, String> mapper = (Function<Integer, String> & Serializable) i -> i.toString();

		LambdaExpressionBluePrint bluePrint = (LambdaExpressionBluePrint) factory.createBluePrint("mapper", mapper,
				currentlyBuildedBluePrints, null);

		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		SignatureType signature = new SignatureType(Function.class);
		signature.addSubType(new SignatureType(Integer.class));
		signature.addSubType(new SignatureType(String.class));

		spezialGeneration.createField(cu, bluePrint, signature);
		assertEquals("private Function<Integer, String> mapper;", cu.getFields().get(0).toString());
		assertTrue(
				imports.contains(Function.class) && imports.contains(Integer.class) && imports.contains(String.class));

		imports.clear();

		spezialGeneration.createField(cu, bluePrint, null);
		assertEquals("private Function mapper;", cu.getFields().get(1).toString());
		assertTrue(imports.contains(Function.class));
	}

	@Test
	public void testCreateObject() {
		@SuppressWarnings("unchecked")
		Consumer<String> consumer = (Consumer<String> & Serializable) s -> System.out.print(s);
		LambdaExpressionBluePrint bluePrint = (LambdaExpressionBluePrint) factory.createBluePrint("consumer", consumer,
				currentlyBuildedBluePrints, null);

		BlockStmt codeBlock = new BlockStmt();

		SignatureType signature = new SignatureType(Consumer.class);
		signature.addSubType(new SignatureType(String.class));

		spezialGeneration.createObject(codeBlock, bluePrint, signature, false);

		String expected = "// TODO add initialization\r\n" + //
				"Consumer<String> consumer = (Consumer & Serializable) null;";
		assertEquals(expected, codeBlock.getStatements().get(0).toString());
		assertTrue(imports.contains(Consumer.class) && imports.contains(String.class)
				&& imports.contains(Serializable.class));
		assertTrue(bluePrint.isBuild());
		
		imports.clear();
		bluePrint.resetBuildState();

		spezialGeneration.createObject(codeBlock, bluePrint, signature, true);

		expected = "// TODO add initialization\r\n" + //
				"this.consumer = (Consumer & Serializable) null;";
		assertEquals(expected, codeBlock.getStatements().get(2).toString());
		assertTrue(imports.contains(Consumer.class)
				&& imports.contains(Serializable.class));
		assertTrue(bluePrint.isBuild());
		
		imports.clear();
		bluePrint.resetBuildState();
		
		spezialGeneration.createObject(codeBlock, bluePrint, null, false);
		
		expected = "// TODO add initialization\r\n" + //
				"Consumer consumer = (Consumer & Serializable) null;";
		assertEquals(expected, codeBlock.getStatements().get(4).toString());
		assertTrue(imports.contains(Consumer.class)
				&& imports.contains(Serializable.class));
		assertTrue(bluePrint.isBuild());
	}
	
	@Test
	public void testCreateObjectWithLocals() {
		int a = 42;
		Runnable runnable = () -> System.out.println(a);
		
		NumberBluePrintFactory numberFactory = new NumberBluePrintFactory();
		
		LambdaExpressionBluePrint bluePrint = (LambdaExpressionBluePrint) factory.createBluePrint("runnable", runnable, currentlyBuildedBluePrints, (name, value)-> numberFactory.createBluePrint(name, (Number) value));
		
		BlockStmt codeBlock = new BlockStmt();
		
		SignatureType signature = new SignatureType(Runnable.class);
		
		spezialGeneration.createObject(codeBlock, bluePrint, signature, false);
		
		String expected = "{\r\n"+//
		"    int arg$1 = 42;\r\n"+//
		"\r\n"+//
		"    // TODO add initialization\r\n"+//
		"    Runnable runnable = null;\r\n"+//
		"\r\n"+//
		"}";
		
		PrettyPrinterConfiguration printerConfig = new PrettyPrinterConfiguration()
				.setVisitorFactory(TestgeneratorPrettyPrinter::new);
		
		assertEquals(expected, codeBlock.toString(printerConfig));
		assertTrue(imports.contains(Runnable.class));
		assertTrue(bluePrint.isBuild());
		
		bluePrint.resetBuildState();
		imports.clear();
		
		BlockStmt block = new BlockStmt();
		
		spezialGeneration.createObject(block, bluePrint, signature, true);
		
		expected = "{\r\n"+//
				"    int arg$1 = 42;\r\n"+//
				"\r\n"+//
				"    // TODO add initialization\r\n"+//
				"    this.runnable = null;\r\n"+//
				"\r\n"+//
				"}";
		assertEquals(expected, block.toString(printerConfig));
		assertTrue(imports.isEmpty());
		assertTrue(bluePrint.isBuild());
	}
}

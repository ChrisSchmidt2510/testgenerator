package org.testgen.runtime.generation.javaparser.impl.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.DelayQueue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testgen.core.CurrentlyBuiltQueue;
import org.testgen.runtime.classdata.model.SetterMethodData;
import org.testgen.runtime.classdata.model.SetterType;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;
import org.testgen.runtime.generation.api.collections.CollectionGeneration;
import org.testgen.runtime.generation.api.naming.NamingServiceProvider;
import org.testgen.runtime.generation.javaparser.impl.TestgeneratorPrettyPrinter;
import org.testgen.runtime.generation.javaparser.impl.simple.JavaParserSimpleObjectGenerationFactory;
import org.testgen.runtime.valuetracker.blueprint.BasicCollectionBluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.complextypes.collections.CollectionBluePrint.CollectionBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.StringBluePrint.StringBluePrintFactory;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.printer.DefaultPrettyPrinter;
import com.github.javaparser.printer.configuration.DefaultPrinterConfiguration;

public class CollectionsGenerationTest {

	private Set<Class<?>> imports = new HashSet<>();
	private CollectionGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> collectionGeneration = new CollectionsGeneration();

	private CollectionBluePrintFactory collectionFactory = new CollectionBluePrintFactory();

	private CurrentlyBuiltQueue<BluePrint> currentlyBuiltQueue = new CurrentlyBuiltQueue<>();

	private DefaultPrettyPrinter printer = new DefaultPrettyPrinter((config) -> new TestgeneratorPrettyPrinter(config),
			new DefaultPrinterConfiguration());

	@BeforeEach
	public void init() {
		collectionGeneration.setImportCallBackHandler(imports::add);

		collectionGeneration.setSimpleObjectGenerationFactory(new JavaParserSimpleObjectGenerationFactory());
	}

	@AfterEach
	public void cleanUp() {
		imports.clear();

		NamingServiceProvider.getNamingService().clearFields();
	}

	@Test
	public void testCreateFieldSet() {
		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		BasicCollectionBluePrint<?> collection = collectionFactory
				.createBluePrint("value", new HashSet<>(), currentlyBuiltQueue, null).castToCollectionBluePrint();

		SignatureType nestedSignature = new SignatureType(Integer.class);
		SignatureType signature = new SignatureType(Set.class);
		signature.addSubType(nestedSignature);

		collectionGeneration.createField(cu, collection, signature);
		assertEquals("private Set<Integer> value = new HashSet<>();", cu.getFields().get(0).toString());

		collectionGeneration.createField(cu, collection, null);
		assertEquals("private Set<Object> value = new HashSet<>();", cu.getFields().get(1).toString());

		assertTrue(imports.contains(Set.class) && imports.contains(HashSet.class));
	}

	@Test
	public void testCreateFieldCollection() {
		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		BasicCollectionBluePrint<?> collection = collectionFactory
				.createBluePrint("value", new Vector<>(), currentlyBuiltQueue, null).castToCollectionBluePrint();

		SignatureType nestedSignature = new SignatureType(LocalDate.class);
		SignatureType signature = new SignatureType(Collection.class);
		signature.addSubType(nestedSignature);

		collectionGeneration.createField(cu, collection, signature);
		assertEquals("private Collection<LocalDate> value = new Vector<>();", cu.getFields().get(0).toString());

		collectionGeneration.createField(cu, collection, null);
		assertEquals("private List<Object> value = new Vector<>();", cu.getFields().get(1).toString());

		assertTrue(imports.contains(Collection.class) && imports.contains(Vector.class));
	}

	@Test
	public void testCreateFieldList() {
		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		BasicCollectionBluePrint<?> collection = collectionFactory
				.createBluePrint("value", new ArrayList<>(), currentlyBuiltQueue, null).castToCollectionBluePrint();

		SignatureType nestedSignature = new SignatureType(LocalDate.class);
		SignatureType signature = new SignatureType(List.class);
		signature.addSubType(nestedSignature);

		collectionGeneration.createField(cu, collection, signature);
		assertEquals("private List<LocalDate> value = new ArrayList<>();", cu.getFields().get(0).toString());

		collectionGeneration.createField(cu, collection, null);
		assertEquals("private List<Object> value = new ArrayList<>();", cu.getFields().get(1).toString());

		assertTrue(imports.contains(List.class) && imports.contains(ArrayList.class));
	}

	@Test
	public void testCreateFieldDeque() {
		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		BasicCollectionBluePrint<?> collection = collectionFactory
				.createBluePrint("value", new ArrayDeque<>(), currentlyBuiltQueue, null).castToCollectionBluePrint();

		SignatureType nestedSignature = new SignatureType(BigDecimal.class);
		SignatureType signature = new SignatureType(Deque.class);
		signature.addSubType(nestedSignature);

		collectionGeneration.createField(cu, collection, signature);
		assertEquals("private Deque<BigDecimal> value = new ArrayDeque<>();", cu.getFields().get(0).toString());

		collectionGeneration.createField(cu, collection, null);
		assertEquals("private Deque<Object> value = new ArrayDeque<>();", cu.getFields().get(1).toString());

		assertTrue(imports.contains(Deque.class) && imports.contains(ArrayDeque.class));
	}

	@Test
	public void testCreateFieldQueue() {
		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		BasicCollectionBluePrint<?> collection = collectionFactory
				.createBluePrint("value", new DelayQueue<>(), currentlyBuiltQueue, null).castToCollectionBluePrint();

		SignatureType nestedSignature = new SignatureType(String.class);
		SignatureType signature = new SignatureType(Queue.class);
		signature.addSubType(nestedSignature);

		collectionGeneration.createField(cu, collection, signature);
		assertEquals("private Queue<String> value = new DelayQueue<>();", cu.getFields().get(0).toString());

		collectionGeneration.createField(cu, collection, null);
		assertEquals("private Queue<Object> value = new DelayQueue<>();", cu.getFields().get(1).toString());

		assertTrue(imports.contains(Queue.class) && imports.contains(DelayQueue.class));
	}

	@Test
	public void testCreateCollection() {

		List<String> list = new ArrayList<>();
		list.add("foo");
		list.add("oof");
		list.add("why");

		StringBluePrintFactory factory = new StringBluePrintFactory();

		BasicCollectionBluePrint<?> bluePrint = collectionFactory.createBluePrint("list", list, currentlyBuiltQueue,
				(name, value) -> factory.createBluePrint(name, (String) value)).castToCollectionBluePrint();

		BlockStmt block = new BlockStmt();

		SignatureType subType = new SignatureType(String.class);
		SignatureType genericType = new SignatureType(List.class);
		genericType.addSubType(subType);

		collectionGeneration.createCollection(block, bluePrint, genericType, false);

		String expected = "{\r\n" + //
				"    List<String> list = new ArrayList<>();\r\n"//
				+ "    list.add(\"foo\");\r\n"//
				+ "    list.add(\"oof\");\r\n"//
				+ "    list.add(\"why\");\r\n"//
				+ "\r\n"//
				+ "}";

		assertEquals(expected, printer.print(block));

		assertTrue(imports.contains(List.class) && imports.contains(ArrayList.class));

		bluePrint.resetBuildState();

		BlockStmt newBlock = new BlockStmt();

		expected = "{\r\n" //
				+ "    this.list.add(\"foo\");\r\n"//
				+ "    this.list.add(\"oof\");\r\n"//
				+ "    this.list.add(\"why\");\r\n"//
				+ "\r\n"//
				+ "}";

		collectionGeneration.createCollection(newBlock, bluePrint, genericType, true);
		assertEquals(expected, printer.print(newBlock));
	}

	@Test
	public void testAddCollectionToObject() {
		List<String> value = new ArrayList<String>();
		value.add("word");

		StringBluePrintFactory stringFactory = new StringBluePrintFactory();

		BasicCollectionBluePrint<?> collection = collectionFactory
				.createBluePrint("value", value, currentlyBuiltQueue,
						(name, childValue) -> stringFactory.createBluePrint(name, (String) childValue))
				.castToCollectionBluePrint();

		BlockStmt block = new BlockStmt();

		NameExpr accessExpr = new NameExpr("object");

		SetterMethodData setter = new SetterMethodData("setValue", "(Ljava/util/List;)V", false,
				SetterType.VALUE_SETTER);

		collectionGeneration.addCollectionToObject(block, collection, true, setter, accessExpr);
		assertEquals("object.setValue(this.value);", block.getStatement(0).toString());

		collectionGeneration.addCollectionToObject(block, collection, false, setter, accessExpr);
		assertEquals("object.setValue(value);", block.getStatement(1).toString());

		SetterMethodData getter = new SetterMethodData("getValue", "()Ljava/util/List;", false,
				SetterType.VALUE_GETTER);

		collectionGeneration.addCollectionToObject(block, collection, true, getter, accessExpr);
		assertEquals("object.getValue().addAll(this.value);", block.getStatement(2).toString());

		collectionGeneration.addCollectionToObject(block, collection, false, getter, accessExpr);
		assertEquals("object.getValue().addAll(value);", block.getStatement(3).toString());

		SetterMethodData collectionSetter = new SetterMethodData("add", "(Ljava/lang/String;)V", false,
				SetterType.COLLECTION_SETTER);

		collectionGeneration.addCollectionToObject(block, collection, true, collectionSetter, accessExpr);
		assertEquals("object.add(\"word\");", block.getStatement(4).toString());

		collectionGeneration.addCollectionToObject(block, collection, false, collectionSetter, accessExpr);
		assertEquals("object.add(\"word\");", block.getStatement(5).toString());
	}

	@Test
	public void testAddCollectionToField() {
		List<String> value = new ArrayList<String>();
		value.add("foo");

		StringBluePrintFactory stringFactory = new StringBluePrintFactory();

		BasicCollectionBluePrint<?> collection = collectionFactory
				.createBluePrint("value", value, currentlyBuiltQueue,
						(name, childValue) -> stringFactory.createBluePrint(name, (String) childValue))
				.castToCollectionBluePrint();

		BlockStmt block = new BlockStmt();

		FieldAccessExpr accessExpr = new FieldAccessExpr(new ThisExpr(), "object");

		collectionGeneration.addCollectionToField(block, collection, true, accessExpr);
		assertEquals("this.object = this.value;", block.getStatement(0).toString());

		collectionGeneration.addCollectionToField(block, collection, false, accessExpr);
		assertEquals("this.object = value;", block.getStatement(1).toString());
	}
}

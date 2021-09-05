package org.testgen.runtime.generation.javaparser.impl.collection;

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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.testgen.runtime.classdata.model.SetterMethodData;
import org.testgen.runtime.classdata.model.SetterType;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;
import org.testgen.runtime.generation.api.collections.CollectionGeneration;
import org.testgen.runtime.generation.api.naming.NamingServiceProvider;
import org.testgen.runtime.generation.javaparser.impl.TestgeneratorPrettyPrinter;
import org.testgen.runtime.generation.javaparser.impl.simple.JavaParserSimpleObjectGenerationFactory;
import org.testgen.runtime.valuetracker.CurrentlyBuildedBluePrints;
import org.testgen.runtime.valuetracker.blueprint.AbstractBasicCollectionBluePrint;
import org.testgen.runtime.valuetracker.blueprint.collections.CollectionBluePrint.CollectionBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.StringBluePrint.StringBluePrintFactory;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.printer.PrettyPrinterConfiguration;

public class CollectionsGenerationTest {

	private Set<Class<?>> imports = new HashSet<>();
	private CollectionGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> collectionGeneration = new CollectionsGeneration();

	private CollectionBluePrintFactory collectionFactory = new CollectionBluePrintFactory();

	private CurrentlyBuildedBluePrints currentlyBuildedBluePrints = new CurrentlyBuildedBluePrints();

	@Before
	public void init() {
		collectionGeneration.setImportCallBackHandler(imports::add);

		collectionGeneration.setSimpleObjectGenerationFactory(new JavaParserSimpleObjectGenerationFactory());
	}

	@After
	public void cleanUp() {
		imports.clear();

		NamingServiceProvider.getNamingService().clearFields();
	}

	@Test
	public void testCreateFieldSet() {
		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		AbstractBasicCollectionBluePrint<?> collection = collectionFactory
				.createBluePrint("value", new HashSet<>(), currentlyBuildedBluePrints, null).castToCollectionBluePrint();

		SignatureType nestedSignature = new SignatureType(Integer.class);
		SignatureType signature = new SignatureType(Set.class);
		signature.addSubType(nestedSignature);

		collectionGeneration.createField(cu, collection, signature);
		Assert.assertEquals("private Set<Integer> value = new HashSet<>();", cu.getFields().get(0).toString());

		collectionGeneration.createField(cu, collection, null);
		Assert.assertEquals("private Set<Object> value = new HashSet<>();", cu.getFields().get(1).toString());

		Assert.assertTrue(imports.contains(Set.class) && imports.contains(HashSet.class));
	}

	@Test
	public void testCreateFieldCollection() {
		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		AbstractBasicCollectionBluePrint<?> collection = collectionFactory
				.createBluePrint("value", new Vector<>(), currentlyBuildedBluePrints, null).castToCollectionBluePrint();

		SignatureType nestedSignature = new SignatureType(LocalDate.class);
		SignatureType signature = new SignatureType(Collection.class);
		signature.addSubType(nestedSignature);

		collectionGeneration.createField(cu, collection, signature);
		Assert.assertEquals("private Collection<LocalDate> value = new Vector<>();", cu.getFields().get(0).toString());

		collectionGeneration.createField(cu, collection, null);
		Assert.assertEquals("private List<Object> value = new Vector<>();", cu.getFields().get(1).toString());

		Assert.assertTrue(imports.contains(Collection.class) && imports.contains(Vector.class));
	}

	@Test
	public void testCreateFieldList() {
		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		AbstractBasicCollectionBluePrint<?> collection = collectionFactory
				.createBluePrint("value", new ArrayList<>(), currentlyBuildedBluePrints, null).castToCollectionBluePrint();

		SignatureType nestedSignature = new SignatureType(LocalDate.class);
		SignatureType signature = new SignatureType(List.class);
		signature.addSubType(nestedSignature);

		collectionGeneration.createField(cu, collection, signature);
		Assert.assertEquals("private List<LocalDate> value = new ArrayList<>();", cu.getFields().get(0).toString());

		collectionGeneration.createField(cu, collection, null);
		Assert.assertEquals("private List<Object> value = new ArrayList<>();", cu.getFields().get(1).toString());

		Assert.assertTrue(imports.contains(List.class) && imports.contains(ArrayList.class));
	}

	@Test
	public void testCreateFieldDeque() {
		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		AbstractBasicCollectionBluePrint<?> collection = collectionFactory
				.createBluePrint("value", new ArrayDeque<>(), currentlyBuildedBluePrints, null).castToCollectionBluePrint();

		SignatureType nestedSignature = new SignatureType(BigDecimal.class);
		SignatureType signature = new SignatureType(Deque.class);
		signature.addSubType(nestedSignature);

		collectionGeneration.createField(cu, collection, signature);
		Assert.assertEquals("private Deque<BigDecimal> value = new ArrayDeque<>();", cu.getFields().get(0).toString());

		collectionGeneration.createField(cu, collection, null);
		Assert.assertEquals("private Deque<Object> value = new ArrayDeque<>();", cu.getFields().get(1).toString());

		Assert.assertTrue(imports.contains(Deque.class) && imports.contains(ArrayDeque.class));
	}

	@Test
	public void testCreateFieldQueue() {
		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		AbstractBasicCollectionBluePrint<?> collection = collectionFactory
				.createBluePrint("value", new DelayQueue<>(), currentlyBuildedBluePrints, null).castToCollectionBluePrint();

		SignatureType nestedSignature = new SignatureType(String.class);
		SignatureType signature = new SignatureType(Queue.class);
		signature.addSubType(nestedSignature);

		collectionGeneration.createField(cu, collection, signature);
		Assert.assertEquals("private Queue<String> value = new DelayQueue<>();", cu.getFields().get(0).toString());

		collectionGeneration.createField(cu, collection, null);
		Assert.assertEquals("private Queue<Object> value = new DelayQueue<>();", cu.getFields().get(1).toString());

		Assert.assertTrue(imports.contains(Queue.class) && imports.contains(DelayQueue.class));
	}

	@Test
	public void testCreateCollection() {

		List<String> list = new ArrayList<>();
		list.add("foo");
		list.add("oof");
		list.add("why");

		StringBluePrintFactory factory = new StringBluePrintFactory();

		AbstractBasicCollectionBluePrint<?> bluePrint = collectionFactory.createBluePrint("list", list, currentlyBuildedBluePrints,
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

		PrettyPrinterConfiguration printerConfig = new PrettyPrinterConfiguration()
				.setVisitorFactory(TestgeneratorPrettyPrinter::new);

		Assert.assertEquals(expected, block.toString(printerConfig));

		Assert.assertTrue(imports.contains(List.class) && imports.contains(ArrayList.class));

		bluePrint.resetBuildState();

		BlockStmt newBlock = new BlockStmt();

		expected = "{\r\n" //
				+ "    this.list.add(\"foo\");\r\n"//
				+ "    this.list.add(\"oof\");\r\n"//
				+ "    this.list.add(\"why\");\r\n"//
				+ "\r\n"//
				+ "}";

		collectionGeneration.createCollection(newBlock, bluePrint, genericType, true);
		Assert.assertEquals(expected, newBlock.toString(printerConfig));
	}

	@Test
	public void testAddCollectionToObject() {
		List<String> value = new ArrayList<String>();
		value.add("word");

		StringBluePrintFactory stringFactory = new StringBluePrintFactory();

		AbstractBasicCollectionBluePrint<?> collection = collectionFactory
				.createBluePrint("value", value, currentlyBuildedBluePrints,
						(name, childValue) -> stringFactory.createBluePrint(name, (String) childValue))
				.castToCollectionBluePrint();

		BlockStmt block = new BlockStmt();

		NameExpr accessExpr = new NameExpr("object");

		SetterMethodData setter = new SetterMethodData("setValue", "(Ljava/util/List;)V", false,
				SetterType.VALUE_SETTER);

		collectionGeneration.addCollectionToObject(block, collection, true, setter, accessExpr);
		Assert.assertEquals("object.setValue(this.value);", block.getStatement(0).toString());

		collectionGeneration.addCollectionToObject(block, collection, false, setter, accessExpr);
		Assert.assertEquals("object.setValue(value);", block.getStatement(1).toString());

		SetterMethodData getter = new SetterMethodData("getValue", "()Ljava/util/List;", false,
				SetterType.VALUE_GETTER);

		collectionGeneration.addCollectionToObject(block, collection, true, getter, accessExpr);
		Assert.assertEquals("object.getValue().addAll(this.value);", block.getStatement(2).toString());

		collectionGeneration.addCollectionToObject(block, collection, false, getter, accessExpr);
		Assert.assertEquals("object.getValue().addAll(value);", block.getStatement(3).toString());

		SetterMethodData collectionSetter = new SetterMethodData("add", "(Ljava/lang/String;)V", false,
				SetterType.COLLECTION_SETTER);

		collectionGeneration.addCollectionToObject(block, collection, true, collectionSetter, accessExpr);
		Assert.assertEquals("object.add(\"word\");", block.getStatement(4).toString());

		collectionGeneration.addCollectionToObject(block, collection, false, collectionSetter, accessExpr);
		Assert.assertEquals("object.add(\"word\");", block.getStatement(5).toString());
	}

	@Test
	public void testAddCollectionToField() {
		List<String> value = new ArrayList<String>();
		value.add("foo");

		StringBluePrintFactory stringFactory = new StringBluePrintFactory();

		AbstractBasicCollectionBluePrint<?> collection = collectionFactory
				.createBluePrint("value", value, currentlyBuildedBluePrints,
						(name, childValue) -> stringFactory.createBluePrint(name, (String) childValue))
				.castToCollectionBluePrint();

		BlockStmt block = new BlockStmt();

		FieldAccessExpr accessExpr = new FieldAccessExpr(new ThisExpr(), "object");

		collectionGeneration.addCollectionToField(block, collection, true, accessExpr);
		Assert.assertEquals("this.object = this.value;", block.getStatement(0).toString());

		collectionGeneration.addCollectionToField(block, collection, false, accessExpr);
		Assert.assertEquals("this.object = value;", block.getStatement(1).toString());
	}
}

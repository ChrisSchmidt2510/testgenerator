package org.testgen.runtime.generation.javaparser.impl.collection;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiFunction;

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
import org.testgen.runtime.valuetracker.blueprint.AbstractBasicCollectionBluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.collections.MapBluePrint.MapBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.NumberBluePrint.NumberBluePrintFactory;
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

public class MapGenerationTest {

	private Set<Class<?>> imports = new HashSet<>();
	private CollectionGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> collectionGeneration = new MapGeneration();

	private MapBluePrintFactory mapFactory = new MapBluePrintFactory();

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
	public void testCreateField() {
		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		AbstractBasicCollectionBluePrint<?> bluePrint = mapFactory.createBluePrint("value", new TreeMap<>(), null)
				.castToCollectionBluePrint();

		SignatureType nestedValue = new SignatureType(LocalDate.class);
		SignatureType value = new SignatureType(List.class);
		value.addSubType(nestedValue);

		SignatureType key = new SignatureType(String.class);

		SignatureType signature = new SignatureType(Map.class);
		signature.addSubType(key);
		signature.addSubType(value);

		collectionGeneration.createField(cu, bluePrint, signature);
		Assert.assertEquals("private Map<String, List<LocalDate>> value = new TreeMap<>();",
				cu.getFields().get(0).toString());

		Assert.assertTrue(imports.contains(Map.class));
		Assert.assertTrue(imports.contains(String.class));
		Assert.assertTrue(imports.contains(List.class));
		Assert.assertTrue(imports.contains(LocalDate.class));
	}

	@Test
	public void testCreateCollection() {
		Map<Integer, String> map = new HashMap<>();
		map.put(5, "foo");
		map.put(10, "oof");

		StringBluePrintFactory strFactory = new StringBluePrintFactory();
		NumberBluePrintFactory numFactory = new NumberBluePrintFactory();

		BiFunction<String, Object, BluePrint> mapper = (name, value) -> {
			if (strFactory.createBluePrintForType(value))
				return strFactory.createBluePrint(name, (String) value);

			return numFactory.createBluePrint(name, (Number) value);
		};

		AbstractBasicCollectionBluePrint<?> bluePrint = mapFactory.createBluePrint("value", map, mapper)
				.castToCollectionBluePrint();

		BlockStmt block = new BlockStmt();

		SignatureType value = new SignatureType(String.class);

		SignatureType key = new SignatureType(Integer.class);

		SignatureType signature = new SignatureType(Map.class);
		signature.addSubType(key);
		signature.addSubType(value);

		String expectedField = "{\r\n"//
				+ "    this.value.put(5, \"foo\");\r\n"//
				+ "    this.value.put(10, \"oof\");\r\n"//
				+ "\r\n"//
				+ "}";

		PrettyPrinterConfiguration printerConfig = new PrettyPrinterConfiguration()
				.setVisitorFactory(TestgeneratorPrettyPrinter::new);

		collectionGeneration.createCollection(block, bluePrint, signature, true);
		Assert.assertEquals(expectedField, block.toString(printerConfig));

		bluePrint.resetBuildState();

		BlockStmt newBlock = new BlockStmt();

		String expectedLocal = "{\r\n"//
				+ "    Map<Integer, String> value = new HashMap<>();\r\n"//
				+ "    value.put(5, \"foo\");\r\n"//
				+ "    value.put(10, \"oof\");\r\n"//
				+ "\r\n"//
				+ "}";

		collectionGeneration.createCollection(newBlock, bluePrint, signature, false);
		Assert.assertEquals(expectedLocal, newBlock.toString(printerConfig));

	}

	@Test
	public void testCreateComplexElementsNestedSignature() {
		SignatureType nestedKeyType = new SignatureType(Integer.class);

		SignatureType key = new SignatureType(Set.class);
		key.addSubType(nestedKeyType);

		SignatureType nestedValueType = new SignatureType(String.class);

		SignatureType value = new SignatureType(List.class);
		value.addSubType(nestedValueType);

		SignatureType signature = new SignatureType(Map.class);
		signature.addSubType(key);
		signature.addSubType(value);

		// TODO implement completly after finishing complexObjectGenerator
	}

	@Test
	public void testAddCollectionToObject() {
		Map<Integer, String> map = new HashMap<>();
		map.put(10, "exel");

		StringBluePrintFactory strFactory = new StringBluePrintFactory();
		NumberBluePrintFactory numFactory = new NumberBluePrintFactory();

		BiFunction<String, Object, BluePrint> mapper = (name, value) -> {
			if (strFactory.createBluePrintForType(value))
				return strFactory.createBluePrint(name, (String) value);

			return numFactory.createBluePrint(name, (Number) value);
		};

		AbstractBasicCollectionBluePrint<?> bluePrint = mapFactory.createBluePrint("value", map, mapper)
				.castToCollectionBluePrint();

		NameExpr accessExpr = new NameExpr("object");

		BlockStmt block = new BlockStmt();

		SetterMethodData setter = new SetterMethodData("setMap", "(Ljava/util/Map;)V", false, SetterType.VALUE_SETTER);

		collectionGeneration.addCollectionToObject(block, bluePrint, true, setter, accessExpr);
		Assert.assertEquals("object.setMap(this.value);", block.getStatement(0).toString());

		collectionGeneration.addCollectionToObject(block, bluePrint, false, setter, accessExpr);
		Assert.assertEquals("object.setMap(value);", block.getStatement(1).toString());

		SetterMethodData getter = new SetterMethodData("getMap", "()Ljava/util/Map;", false, SetterType.VALUE_GETTER);

		collectionGeneration.addCollectionToObject(block, bluePrint, true, getter, accessExpr);
		Assert.assertEquals("object.getMap().putAll(this.value);", block.getStatement(2).toString());

		collectionGeneration.addCollectionToObject(block, bluePrint, false, getter, accessExpr);
		Assert.assertEquals("object.getMap().putAll(value);", block.getStatement(3).toString());

		SetterMethodData collectionSetter = new SetterMethodData("addEntry", "(Ljava/lang/Integer;Ljava/lang/String;)V",
				false, SetterType.COLLECTION_SETTER);

		collectionGeneration.addCollectionToObject(block, bluePrint, false, collectionSetter, accessExpr);
		Assert.assertEquals("object.addEntry(10, \"exel\");", block.getStatement(4).toString());
	}

	@Test
	public void testAddCollectionToField() {
		Map<Integer, String> map = new HashMap<>();

		AbstractBasicCollectionBluePrint<?> bluePrint = mapFactory.createBluePrint("value", map, null)
				.castToCollectionBluePrint();

		Expression accessExpr = new FieldAccessExpr(new ThisExpr(), "object");

		BlockStmt block = new BlockStmt();

		collectionGeneration.addCollectionToField(block, bluePrint, true, accessExpr);
		Assert.assertEquals("this.object = this.value;", block.getStatement(0).toString());

		collectionGeneration.addCollectionToField(block, bluePrint, false, accessExpr);
		Assert.assertEquals("this.object = value;", block.getStatement(1).toString());

	}

}

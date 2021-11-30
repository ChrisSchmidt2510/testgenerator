package org.testgen.runtime.generation.javaparser.impl.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testgen.runtime.classdata.model.SetterMethodData;
import org.testgen.runtime.classdata.model.SetterType;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;
import org.testgen.runtime.generation.api.collections.CollectionGeneration;
import org.testgen.runtime.generation.api.naming.NamingServiceProvider;
import org.testgen.runtime.generation.javaparser.impl.TestgeneratorPrettyPrinter;
import org.testgen.runtime.generation.javaparser.impl.simple.JavaParserSimpleObjectGenerationFactory;
import org.testgen.runtime.valuetracker.CurrentlyBuildedBluePrints;
import org.testgen.runtime.valuetracker.blueprint.BasicCollectionBluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.complextypes.collections.CollectionBluePrint.CollectionBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.complextypes.collections.MapBluePrint.MapBluePrintFactory;
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
import com.github.javaparser.printer.DefaultPrettyPrinter;
import com.github.javaparser.printer.configuration.DefaultPrinterConfiguration;

public class MapGenerationTest {

	private Set<Class<?>> imports = new HashSet<>();
	private CollectionGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> collectionGeneration = new MapGeneration();

	private MapBluePrintFactory mapFactory = new MapBluePrintFactory();

	private CurrentlyBuildedBluePrints currentlyBuildedBluePrints = new CurrentlyBuildedBluePrints();
	
	private DefaultPrettyPrinter printer= new DefaultPrettyPrinter(
			(config) -> new TestgeneratorPrettyPrinter(config), new DefaultPrinterConfiguration());

	@BeforeEach
	public void init() {
		Consumer<Class<?>> importCallBackHandler = imports::add;

		JavaParserSimpleObjectGenerationFactory simpleGenerationFactory = new JavaParserSimpleObjectGenerationFactory();
		simpleGenerationFactory.setImportCallBackHandler(importCallBackHandler);

		JavaParserCollectionGenerationFactory collectionGenerationFactory = new JavaParserCollectionGenerationFactory();
		collectionGenerationFactory.setImportCallBackHandler(importCallBackHandler);
		collectionGenerationFactory.setSimpleObjectGenerationFactory(simpleGenerationFactory);

		collectionGeneration.setImportCallBackHandler(importCallBackHandler);
		collectionGeneration.setSimpleObjectGenerationFactory(simpleGenerationFactory);
		collectionGeneration.setCollectionGenerationFactory(collectionGenerationFactory);
	}

	@AfterEach
	public void cleanUp() {
		imports.clear();

		NamingServiceProvider.getNamingService().clearFields();
	}

	@Test
	public void testCreateField() {
		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		BasicCollectionBluePrint<?> bluePrint = mapFactory
				.createBluePrint("value", new TreeMap<>(), currentlyBuildedBluePrints, null)
				.castToCollectionBluePrint();

		SignatureType nestedValue = new SignatureType(LocalDate.class);
		SignatureType value = new SignatureType(List.class);
		value.addSubType(nestedValue);

		SignatureType key = new SignatureType(String.class);

		SignatureType signature = new SignatureType(Map.class);
		signature.addSubType(key);
		signature.addSubType(value);

		collectionGeneration.createField(cu, bluePrint, signature);
		assertEquals("private Map<String, List<LocalDate>> value = new TreeMap<>();",
				cu.getFields().get(0).toString());

		assertTrue(imports.contains(Map.class));
		assertTrue(imports.contains(String.class));
		assertTrue(imports.contains(List.class));
		assertTrue(imports.contains(LocalDate.class));
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

		BasicCollectionBluePrint<?> bluePrint = mapFactory
				.createBluePrint("value", map, currentlyBuildedBluePrints, mapper).castToCollectionBluePrint();

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

		collectionGeneration.createCollection(block, bluePrint, signature, true);
		assertEquals(expectedField, printer.print(block));

		bluePrint.resetBuildState();

		BlockStmt newBlock = new BlockStmt();

		String expectedLocal = "{\r\n"//
				+ "    Map<Integer, String> value = new HashMap<>();\r\n"//
				+ "    value.put(5, \"foo\");\r\n"//
				+ "    value.put(10, \"oof\");\r\n"//
				+ "\r\n"//
				+ "}";

		collectionGeneration.createCollection(newBlock, bluePrint, signature, false);
		assertEquals(expectedLocal, printer.print(newBlock));

	}

	@Test
	public void testCreateComplexElementsNestedSignature() {
		SignatureType nestedKeyType = new SignatureType(Integer.class);

		SignatureType key = new SignatureType(Set.class);
		key.addSubType(nestedKeyType);

		SignatureType nestedValueType = new SignatureType(String.class);

		SignatureType mapValue = new SignatureType(List.class);
		mapValue.addSubType(nestedValueType);

		SignatureType signature = new SignatureType(Map.class);
		signature.addSubType(key);
		signature.addSubType(mapValue);

		Map<Set<Integer>, List<String>> map = new LinkedHashMap<>();
		map.put(new HashSet<>(Arrays.asList(1, 2, 3)), Arrays.asList("aged", "like", "milk"));
		map.put(new HashSet<>(Arrays.asList(9, 8, 7)), Arrays.asList("why", "i'm", "so", "stupid"));

		StringBluePrintFactory strFactory = new StringBluePrintFactory();
		NumberBluePrintFactory numFactory = new NumberBluePrintFactory();
		CollectionBluePrintFactory collectionFactory = new CollectionBluePrintFactory();

		BiFunction<String, Object, BluePrint> valueMapper = (name, value) -> strFactory.createBluePrintForType(value)
				? strFactory.createBluePrint(name, (String) value)
				: numFactory.createBluePrint(name, (Number) value);

		BiFunction<String, Object, BluePrint> collectionMapper = (name, value) -> collectionFactory
				.createBluePrint(name, value, currentlyBuildedBluePrints, valueMapper);

		BasicCollectionBluePrint<?> bluePrint = mapFactory
				.createBluePrint("map", map, currentlyBuildedBluePrints, collectionMapper).castToCollectionBluePrint();

		BlockStmt block = new BlockStmt();

		String expected = "{\r\n"//
				+ "    Set<Integer> mapKey = new HashSet<>();\r\n"//
				+ "    mapKey.add(1);\r\n"//
				+ "    mapKey.add(2);\r\n"//
				+ "    mapKey.add(3);\r\n"//
				+ "\r\n"//
				+ "    Set<Integer> mapKey1 = new HashSet<>();\r\n"//
				+ "    mapKey1.add(7);\r\n"//
				+ "    mapKey1.add(8);\r\n"//
				+ "    mapKey1.add(9);\r\n"//
				+ "\r\n"//
				+ "    List<String> mapValue = new ArrayList<>();\r\n" //
				+"    mapValue.add(\"aged\");\r\n"//
				+ "    mapValue.add(\"like\");\r\n"//
				+ "    mapValue.add(\"milk\");\r\n"//
				+ "\r\n"//
				+ "    List<String> mapValue1 = new ArrayList<>();\r\n"//
				+ "    mapValue1.add(\"why\");\r\n"//
				+ "    mapValue1.add(\"i'm\");\r\n"//
				+ "    mapValue1.add(\"so\");\r\n"//
				+ "    mapValue1.add(\"stupid\");\r\n"//
				+ "\r\n"//
				+ "    Map<Set<Integer>, List<String>> map = new LinkedHashMap<>();\r\n"//
				+ "    map.put(mapKey, mapValue);\r\n"//
				+ "    map.put(mapKey1, mapValue1);\r\n"//
				+ "\r\n"//
				+ "}";

		collectionGeneration.createCollection(block, bluePrint, signature, false);

		assertEquals(expected, printer.print(block));

		bluePrint.resetBuildState();

		expected = "{\r\n"//
				+ "    Set<Integer> mapKey = new HashSet<>();\r\n"//
				+ "    mapKey.add(1);\r\n"//
				+ "    mapKey.add(2);\r\n"//
				+ "    mapKey.add(3);\r\n"//
				+ "\r\n"//
				+ "    Set<Integer> mapKey1 = new HashSet<>();\r\n"//
				+ "    mapKey1.add(7);\r\n"//
				+ "    mapKey1.add(8);\r\n"//
				+ "    mapKey1.add(9);\r\n"//
				+ "\r\n"//
				+ "    List<String> mapValue = new ArrayList<>();\r\n" //
				+ "    mapValue.add(\"aged\");\r\n"//
				+ "    mapValue.add(\"like\");\r\n"//
				+ "    mapValue.add(\"milk\");\r\n"//
				+ "\r\n" //
				+ "    List<String> mapValue1 = new ArrayList<>();\r\n"//
				+ "    mapValue1.add(\"why\");\r\n"//
				+ "    mapValue1.add(\"i'm\");\r\n"//
				+ "    mapValue1.add(\"so\");\r\n"//
				+ "    mapValue1.add(\"stupid\");\r\n" //
				+"\r\n" //
				+"    this.map.put(mapKey, mapValue);\r\n"//
				+ "    this.map.put(mapKey1, mapValue1);\r\n"//
				+ "\r\n"//
				+ "}";

		BlockStmt newBlock = new BlockStmt();

		collectionGeneration.createCollection(newBlock, bluePrint, signature, true);
		assertEquals(expected, printer.print(newBlock));

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

		BasicCollectionBluePrint<?> bluePrint = mapFactory
				.createBluePrint("value", map, currentlyBuildedBluePrints, mapper).castToCollectionBluePrint();

		NameExpr accessExpr = new NameExpr("object");

		BlockStmt block = new BlockStmt();

		SetterMethodData setter = new SetterMethodData("setMap", "(Ljava/util/Map;)V", false, SetterType.VALUE_SETTER);

		collectionGeneration.addCollectionToObject(block, bluePrint, true, setter, accessExpr);
		assertEquals("object.setMap(this.value);", block.getStatement(0).toString());

		collectionGeneration.addCollectionToObject(block, bluePrint, false, setter, accessExpr);
		assertEquals("object.setMap(value);", block.getStatement(1).toString());

		SetterMethodData getter = new SetterMethodData("getMap", "()Ljava/util/Map;", false, SetterType.VALUE_GETTER);

		collectionGeneration.addCollectionToObject(block, bluePrint, true, getter, accessExpr);
		assertEquals("object.getMap().putAll(this.value);", block.getStatement(2).toString());

		collectionGeneration.addCollectionToObject(block, bluePrint, false, getter, accessExpr);
		assertEquals("object.getMap().putAll(value);", block.getStatement(3).toString());

		SetterMethodData collectionSetter = new SetterMethodData("addEntry", "(Ljava/lang/Integer;Ljava/lang/String;)V",
				false, SetterType.COLLECTION_SETTER);

		collectionGeneration.addCollectionToObject(block, bluePrint, false, collectionSetter, accessExpr);
		assertEquals("object.addEntry(10, \"exel\");", block.getStatement(4).toString());
	}

	@Test
	public void testAddCollectionToField() {
		Map<Integer, String> map = new HashMap<>();

		BasicCollectionBluePrint<?> bluePrint = mapFactory
				.createBluePrint("value", map, currentlyBuildedBluePrints, null).castToCollectionBluePrint();

		Expression accessExpr = new FieldAccessExpr(new ThisExpr(), "object");

		BlockStmt block = new BlockStmt();

		collectionGeneration.addCollectionToField(block, bluePrint, true, accessExpr);
		assertEquals("this.object = this.value;", block.getStatement(0).toString());

		collectionGeneration.addCollectionToField(block, bluePrint, false, accessExpr);
		assertEquals("this.object = value;", block.getStatement(1).toString());

	}

}

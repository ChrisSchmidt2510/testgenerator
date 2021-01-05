package org.testgen.runtime.generation.javaparser.impl.collection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;
import org.testgen.runtime.generation.api.GenerationFactory;
import org.testgen.runtime.generation.api.collections.CollectionGeneration;
import org.testgen.runtime.generation.api.naming.NamingServiceProvider;
import org.testgen.runtime.generation.javaparser.impl.simple.JavaParserSimpleObjectGenerationFactory;
import org.testgen.runtime.valuetracker.blueprint.AbstractBasicCollectionBluePrint;
import org.testgen.runtime.valuetracker.blueprint.collections.CollectionBluePrint.CollectionBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.StringBluePrint.StringBluePrintFactory;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;

public class CollectionsGenerationTest {

	private Set<Class<?>> imports = new HashSet<>();
	private CollectionGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> collectionGeneration;

	@Before
	public void init() {
		GenerationFactory.<ClassOrInterfaceDeclaration, BlockStmt, Expression>getInstance()
				.setImportCallBackHandler(imports::add);

		GenerationFactory.<ClassOrInterfaceDeclaration, BlockStmt, Expression>getInstance()
				.setSimpleObjectGenerationFactory(new JavaParserSimpleObjectGenerationFactory());

		collectionGeneration = new CollectionsGeneration();
	}

	@After
	public void cleanUp() {
		imports.clear();

		NamingServiceProvider.getNamingService().clearFieldNames();
	}

	@Test
	public void testCreateCollection() {

		List<String> list = new ArrayList<>();
		list.add("foo");
		list.add("oof");
		list.add("why");

		StringBluePrintFactory factory = new StringBluePrintFactory();

		CollectionBluePrintFactory collectionFactory = new CollectionBluePrintFactory();
		AbstractBasicCollectionBluePrint<?> bluePrint = collectionFactory
				.createBluePrint("list", list, (name, value) -> factory.createBluePrint(name, value))
				.castToCollectionBluePrint();

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
				+ "}";

		System.out.println(block);

		Assert.assertEquals(expected, block.toString());

		Assert.assertTrue(imports.contains(List.class) && imports.contains(ArrayList.class));
	}
}

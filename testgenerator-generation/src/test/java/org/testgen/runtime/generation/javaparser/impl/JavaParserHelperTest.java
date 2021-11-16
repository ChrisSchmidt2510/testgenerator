package org.testgen.runtime.generation.javaparser.impl;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;

public class JavaParserHelperTest {

	private Set<Class<?>> imports = new HashSet<>();
	private Consumer<Class<?>> callBackHandler = imports::add;

	@Before
	public void init() {
		imports.clear();
	}

	@Test
	public void testGenerateSignature() {

		SignatureType nestedSig = new SignatureType(String.class);
		SignatureType listSig = new SignatureType(List[].class);
		listSig.addSubType(nestedSig);

		Assert.assertEquals("List<String>[]", JavaParserHelper.generateSignature(listSig, callBackHandler).toString());
		Assert.assertTrue(imports.contains(List.class) && imports.contains(String.class));

		imports.clear();

		SignatureType array = new SignatureType(int[].class);
		Assert.assertEquals("int[]", JavaParserHelper.generateSignature(array, callBackHandler).toString());
		Assert.assertTrue(imports.isEmpty());
	}

	@Test
	public void testGenerateGenericSignature() {

		SignatureType list = new SignatureType(List.class);
		list.addSubType(new SignatureType(String.class));

		Assert.assertEquals("List<String>", JavaParserHelper.generateSignature(list, callBackHandler).toString());
		Assert.assertTrue(imports.contains(List.class) && imports.contains(String.class));
	}

	@Test
	public void testGenerateNestedGenericSignature() {

		SignatureType nestedList = new SignatureType(List.class);
		nestedList.addSubType(new SignatureType(Integer.class));

		SignatureType list = new SignatureType(List.class);
		list.addSubType(nestedList);

		Assert.assertEquals("List<List<Integer>>",
				JavaParserHelper.generateSignature(list, callBackHandler).toString());
		Assert.assertTrue(imports.contains(List.class) && imports.contains(Integer.class));
	}

	@Test
	public void testGenerateMultipleGenericSignatures() {
		SignatureType map = new SignatureType(Map.class);
		map.addSubType(new SignatureType(Integer.class));
		map.addSubType(new SignatureType(LocalDate.class));

		Assert.assertEquals("Map<Integer, LocalDate>",
				JavaParserHelper.generateSignature(map, callBackHandler).toString());
		Assert.assertTrue(
				imports.contains(Map.class) && imports.contains(Integer.class) && imports.contains(LocalDate.class));
	}

	@Test
	public void testGenerateMultipleAndNestedSignatures() {
		SignatureType nestedList = new SignatureType(List.class);
		nestedList.addSubType(new SignatureType(LocalDate.class));

		SignatureType map = new SignatureType(Map.class);
		map.addSubType(new SignatureType(Integer.class));
		map.addSubType(nestedList);

		Assert.assertEquals("Map<Integer, List<LocalDate>>",
				JavaParserHelper.generateSignature(map, callBackHandler).toString());
		Assert.assertTrue(imports.contains(Map.class) && imports.contains(Integer.class) && imports.contains(List.class)
				&& imports.contains(LocalDate.class));

	}

}

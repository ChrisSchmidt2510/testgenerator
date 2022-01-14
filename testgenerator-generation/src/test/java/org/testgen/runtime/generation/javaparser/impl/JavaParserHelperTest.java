package org.testgen.runtime.generation.javaparser.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;

public class JavaParserHelperTest {

	private Set<Class<?>> imports = new HashSet<>();
	private Consumer<Class<?>> callBackHandler = imports::add;

	@BeforeEach
	public void init() {
		imports.clear();
	}

	@Test
	public void testGenerateSignature() {

		SignatureType nestedSig = new SignatureType(String.class);
		SignatureType listSig = new SignatureType(List[].class);
		listSig.addSubType(nestedSig);

		assertEquals("List<String>[]", JavaParserHelper.generateSignature(listSig, callBackHandler).toString());
		assertTrue(imports.contains(List.class) && imports.contains(String.class));

		imports.clear();

		SignatureType array = new SignatureType(int[].class);
		assertEquals("int[]", JavaParserHelper.generateSignature(array, callBackHandler).toString());
		assertTrue(imports.isEmpty());
	}

	@Test
	public void testGenerateGenericSignature() {

		SignatureType list = new SignatureType(List.class);
		list.addSubType(new SignatureType(String.class));

		assertEquals("List<String>", JavaParserHelper.generateSignature(list, callBackHandler).toString());
		assertTrue(imports.contains(List.class) && imports.contains(String.class));
	}

	@Test
	public void testGenerateNestedGenericSignature() {

		SignatureType nestedList = new SignatureType(List.class);
		nestedList.addSubType(new SignatureType(Integer.class));

		SignatureType list = new SignatureType(List.class);
		list.addSubType(nestedList);

		assertEquals("List<List<Integer>>",
				JavaParserHelper.generateSignature(list, callBackHandler).toString());
		assertTrue(imports.contains(List.class) && imports.contains(Integer.class));
	}

	@Test
	public void testGenerateMultipleGenericSignatures() {
		SignatureType map = new SignatureType(Map.class);
		map.addSubType(new SignatureType(Integer.class));
		map.addSubType(new SignatureType(LocalDate.class));

		assertEquals("Map<Integer, LocalDate>",
				JavaParserHelper.generateSignature(map, callBackHandler).toString());
		assertTrue(
				imports.contains(Map.class) && imports.contains(Integer.class) && imports.contains(LocalDate.class));
	}

	@Test
	public void testGenerateMultipleAndNestedSignatures() {
		SignatureType nestedList = new SignatureType(List.class);
		nestedList.addSubType(new SignatureType(LocalDate.class));

		SignatureType map = new SignatureType(Map.class);
		map.addSubType(new SignatureType(Integer.class));
		map.addSubType(nestedList);

		assertEquals("Map<Integer, List<LocalDate>>",
				JavaParserHelper.generateSignature(map, callBackHandler).toString());
		assertTrue(imports.contains(Map.class) && imports.contains(Integer.class) && imports.contains(List.class)
				&& imports.contains(LocalDate.class));

	}

}

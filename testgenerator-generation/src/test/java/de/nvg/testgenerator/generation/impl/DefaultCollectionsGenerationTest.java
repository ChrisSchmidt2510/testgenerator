package de.nvg.testgenerator.generation.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;

import de.nvg.runtime.classdatamodel.SignatureData;

public class DefaultCollectionsGenerationTest {
	private DefaultCollectionsGeneration collectionGenerator = new DefaultCollectionsGeneration();

	@Before
	public void init() {
		DefaultComplexObjectGeneration complexObjectGenerator = new DefaultComplexObjectGeneration();
		complexObjectGenerator.setCollectionsGeneration(collectionGenerator);

		collectionGenerator.setComplexObjectGeneration(complexObjectGenerator);
	}

	@Test
	public void testGetParameterizedTypeName() {
		SignatureData list = new SignatureData(List.class);
		list.addSubType(new SignatureData(String.class));

		TypeName genericType = collectionGenerator.getParameterizedTypeName(list);

		CodeBlock code = CodeBlock.of("$T list = null", genericType);
		Assert.assertEquals("java.util.List<java.lang.String> list = null", //
				code.toString());
		System.out.println(code);
	}

	@Test
	public void testGetParameterizedTypeNameWithNestedSignature() {
		SignatureData nestedList = new SignatureData(List.class);
		nestedList.addSubType(new SignatureData(Integer.class));

		SignatureData list = new SignatureData(List.class);
		list.addSubType(nestedList);

		TypeName genericType = collectionGenerator.getParameterizedTypeName(list);

		CodeBlock code = CodeBlock.of("$T list = null", genericType);
		Assert.assertEquals("java.util.List<java.util.List<java.lang.Integer>> list = null", //
				code.toString());
	}

	@Test
	public void testGetParameterizdTypeNameWithMultipleSignatures() {
		SignatureData map = new SignatureData(Map.class);
		map.addSubType(new SignatureData(Integer.class));
		map.addSubType(new SignatureData(LocalDate.class));

		TypeName genericType = collectionGenerator.getParameterizedTypeName(map);

		CodeBlock code = CodeBlock.of("$T map = null", genericType);

		Assert.assertEquals("java.util.Map<java.lang.Integer, java.time.LocalDate> map = null", //
				code.toString());
	}

	@Test
	public void testGetParameterizedTypeNameWithMultipleSignaturesAndNestedSignatures() {
		SignatureData nestedList = new SignatureData(List.class);
		nestedList.addSubType(new SignatureData(LocalDate.class));

		SignatureData map = new SignatureData(Map.class);
		map.addSubType(new SignatureData(Integer.class));
		map.addSubType(nestedList);

		TypeName genericType = collectionGenerator.getParameterizedTypeName(map);

		CodeBlock code = CodeBlock.of("$T map = null", genericType);

		Assert.assertEquals("java.util.Map<java.lang.Integer, java.util.List<java.time.LocalDate>> map = null", //
				code.toString());
	}
}

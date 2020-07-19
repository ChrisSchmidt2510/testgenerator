package de.nvg.testgenerator.generation.impl;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.CodeBlock.Builder;
import com.squareup.javapoet.TypeName;

import de.nvg.testgenerator.generation.naming.NamingService;
import de.nvg.valuetracker.blueprint.ArrayBluePrint;
import de.nvg.valuetracker.blueprint.simpletypes.SimpleBluePrintFactory;

public class DefaultContainerGenerationTest {
	private DefaultContainerGeneration containerGenerator = new DefaultContainerGeneration();

	@Before
	public void init() {
		NamingService namingService = new NamingService();
		DefaultComplexObjectGeneration complexObjectGenerator = new DefaultComplexObjectGeneration();
		complexObjectGenerator.setContainerGeneration(containerGenerator);
		complexObjectGenerator.setNamingService(namingService);

		containerGenerator.setComplexObjectGeneration(complexObjectGenerator);
		containerGenerator.setNamingService(namingService);
	}

	@Test
	public void testGetParameterizedTypeName() {
		SignatureType list = new SignatureType(List.class);
		list.addSubType(new SignatureType(String.class));

		TypeName genericType = containerGenerator.getParameterizedTypeName(list);

		CodeBlock code = CodeBlock.of("$T list = null", genericType);
		Assert.assertEquals("java.util.List<java.lang.String> list = null", //
				code.toString());
		System.out.println(code);
	}

	@Test
	public void testGetParameterizedTypeNameWithNestedSignature() {
		SignatureType nestedList = new SignatureType(List.class);
		nestedList.addSubType(new SignatureType(Integer.class));

		SignatureType list = new SignatureType(List.class);
		list.addSubType(nestedList);

		TypeName genericType = containerGenerator.getParameterizedTypeName(list);

		CodeBlock code = CodeBlock.of("$T list = null", genericType);
		Assert.assertEquals("java.util.List<java.util.List<java.lang.Integer>> list = null", //
				code.toString());
	}

	@Test
	public void testGetParameterizdTypeNameWithMultipleSignatures() {
		SignatureType map = new SignatureType(Map.class);
		map.addSubType(new SignatureType(Integer.class));
		map.addSubType(new SignatureType(LocalDate.class));

		TypeName genericType = containerGenerator.getParameterizedTypeName(map);

		CodeBlock code = CodeBlock.of("$T map = null", genericType);

		Assert.assertEquals("java.util.Map<java.lang.Integer, java.time.LocalDate> map = null", //
				code.toString());
	}

	@Test
	public void testGetParameterizedTypeNameWithMultipleSignaturesAndNestedSignatures() {
		SignatureType nestedList = new SignatureType(List.class);
		nestedList.addSubType(new SignatureType(LocalDate.class));

		SignatureType map = new SignatureType(Map.class);
		map.addSubType(new SignatureType(Integer.class));
		map.addSubType(nestedList);

		TypeName genericType = containerGenerator.getParameterizedTypeName(map);

		CodeBlock code = CodeBlock.of("$T map = null", genericType);

		Assert.assertEquals("java.util.Map<java.lang.Integer, java.util.List<java.time.LocalDate>> map = null", //
				code.toString());
	}

	@Test
	public void testCreateArray() {
		int[] intArray = new int[] { 4, 5, 6 };
		ArrayBluePrint array = new ArrayBluePrint("array", intArray, intArray.length);
		array.add(0, SimpleBluePrintFactory.of("intArray1", 4));
		array.add(1, SimpleBluePrintFactory.of("intArray2", 5));
		array.add(2, SimpleBluePrintFactory.of("intArray3", 6));

		Builder builder = CodeBlock.builder();
		containerGenerator.createArray(builder, array, false, true);
		System.out.println(builder.build().toString());

		Builder code = CodeBlock.builder();
		containerGenerator.createArray(code, array, false, false);
		System.out.println(code.build());

		LocalDate[] dates = new LocalDate[] { LocalDate.now(), LocalDate.of(2020, Month.DECEMBER, 24) };
		ArrayBluePrint datesBp = new ArrayBluePrint("dates", dates, dates.length);
		datesBp.add(0, SimpleBluePrintFactory.of("dates1", LocalDate.now()));
		datesBp.add(1, SimpleBluePrintFactory.of("dates1", LocalDate.of(2020, Month.DECEMBER, 24)));

		Builder codeDates = CodeBlock.builder();
		containerGenerator.createArray(codeDates, datesBp, false, false);
		System.out.println(codeDates.build());
	}

	public void testCreateMultipDimArray() {
		String[][][] stringArray = new String[2][][];

		ArrayBluePrint strArray = new ArrayBluePrint("stringArray", stringArray, stringArray.length);
		strArray.add(0, SimpleBluePrintFactory.of("stringArray1", null));
		strArray.add(1, SimpleBluePrintFactory.of("stringArray2", null));
		Builder strCode = CodeBlock.builder();
		containerGenerator.createArray(strCode, strArray, false, false);
		System.out.println(strCode.build());
	}
}

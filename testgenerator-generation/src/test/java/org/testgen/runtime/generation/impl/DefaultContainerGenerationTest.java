package org.testgen.runtime.generation.impl;

import java.time.LocalDate;
import java.time.Month;

import org.junit.Before;
import org.junit.Test;
import org.testgen.runtime.generation.naming.NamingService;
import org.testgen.runtime.valuetracker.blueprint.ArrayBluePrint;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.SimpleBluePrintFactory;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.CodeBlock.Builder;

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

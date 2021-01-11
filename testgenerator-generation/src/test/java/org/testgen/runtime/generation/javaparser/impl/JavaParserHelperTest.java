package org.testgen.runtime.generation.javaparser.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.Test;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;

public class JavaParserHelperTest {

	@Test
	public void testGenerateSignature() {
		Set<Class<?>> imports = new HashSet<>();

		SignatureType nestedSig = new SignatureType(String.class);
		SignatureType listSig = new SignatureType(List[].class);
		listSig.addSubType(nestedSig);

		Consumer<Class<?>> callBackHandler = imports::add;

		Assert.assertEquals("List<String>[]", JavaParserHelper.generateSignature(listSig, callBackHandler).toString());
		Assert.assertTrue(imports.contains(List.class) && imports.contains(String.class));

		imports.clear();

		SignatureType array = new SignatureType(int[].class);
		Assert.assertEquals("int[]", JavaParserHelper.generateSignature(array, callBackHandler).toString());
		Assert.assertTrue(imports.isEmpty());
	}

}

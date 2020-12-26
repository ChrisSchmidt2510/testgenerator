package org.testgen.runtime.generation.impl;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;
import org.testgen.runtime.generation.javapoet.impl.TestGenerationHelper;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;

public class TestGenerationHelperTest {

	public List<String> method() {
		return null;
	}

	public Map<Integer, List<LocalDate>> method2() {
		return null;
	}

	public <T> List<T> method3() {
		return null;
	}

	@Test
	public void testMapGenericTypeToSignatureType() {
		try {
			Method method = this.getClass().getMethod("method");

			SignatureType signature = TestGenerationHelper.mapGenericTypeToSignature(method.getGenericReturnType());

			Assert.assertEquals(List.class, signature.getType());
			Assert.assertEquals(Arrays.asList(new SignatureType(String.class)), signature.getSubTypes());
		} catch (NoSuchMethodException | SecurityException e) {
			fail();
		}
	}

	@Test
	public void testMapGenericTypeToSignatureWithNestedTypes() {
		try {
			Method method = this.getClass().getMethod("method2");

			SignatureType signature = TestGenerationHelper.mapGenericTypeToSignature(method.getGenericReturnType());

			SignatureType list = new SignatureType(List.class);
			list.addSubType(new SignatureType(LocalDate.class));

			SignatureType expected = new SignatureType(Map.class);
			expected.addSubType(new SignatureType(Integer.class));
			expected.addSubType(list);

			Assert.assertEquals(expected, signature);
		} catch (NoSuchMethodException | SecurityException e) {
			fail();
		}
	}

	@Test
	public void testMapGenericTypeToSignatureUnparsable() {
		try {
			Method method = this.getClass().getMethod("method3");

			SignatureType signature = TestGenerationHelper.mapGenericTypeToSignature(method.getGenericReturnType());

			Assert.assertEquals(null, signature);
		} catch (NoSuchMethodException | SecurityException e) {
			fail();
		}
	}

	@Test
	public void testGetParameterizedTypeName() {
		SignatureType list = new SignatureType(List.class);
		list.addSubType(new SignatureType(String.class));

		TypeName genericType = TestGenerationHelper.getParameterizedTypeName(list);

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

		TypeName genericType = TestGenerationHelper.getParameterizedTypeName(list);

		CodeBlock code = CodeBlock.of("$T list = null", genericType);
		Assert.assertEquals("java.util.List<java.util.List<java.lang.Integer>> list = null", //
				code.toString());
	}

	@Test
	public void testGetParameterizdTypeNameWithMultipleSignatures() {
		SignatureType map = new SignatureType(Map.class);
		map.addSubType(new SignatureType(Integer.class));
		map.addSubType(new SignatureType(LocalDate.class));

		TypeName genericType = TestGenerationHelper.getParameterizedTypeName(map);

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

		TypeName genericType = TestGenerationHelper.getParameterizedTypeName(map);

		CodeBlock code = CodeBlock.of("$T map = null", genericType);

		Assert.assertEquals("java.util.Map<java.lang.Integer, java.util.List<java.time.LocalDate>> map = null", //
				code.toString());
	}

}

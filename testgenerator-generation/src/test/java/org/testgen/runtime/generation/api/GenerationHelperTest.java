package org.testgen.runtime.generation.api;

import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;

public class GenerationHelperTest {

	public List<String> method() {
		return null;
	}

	public Map<Integer, List<LocalDate>> method2() {
		return null;
	}

	public <T> List<T> method3() {
		return null;
	}
	
	public int method4() {
		return 0;
	}

	@Test
	public void testMapGenericTypeToSignatureType() {
		try {
			Method method = this.getClass().getMethod("method");

			SignatureType signature = GenerationHelper.mapGenericTypeToSignature(method.getGenericReturnType());

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

			SignatureType signature = GenerationHelper.mapGenericTypeToSignature(method.getGenericReturnType());

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

			SignatureType signature = GenerationHelper.mapGenericTypeToSignature(method.getGenericReturnType());

			Assert.assertEquals(null, signature);
		} catch (NoSuchMethodException | SecurityException e) {
			fail();
		}
	}
	
	@Test
	public void testNonGenericTypeToSignature() {
			try {
				Method method = this.getClass().getMethod("method4");
				
				SignatureType signature = GenerationHelper.mapGenericTypeToSignature(method.getGenericReturnType());
			
				Assert.assertEquals(new SignatureType(int.class), signature);
			} catch (NoSuchMethodException | SecurityException e) {
				fail();
			}
		
	}
}

package org.testgen.runtime.generation.javaparser.impl;

import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Test;

import com.github.javaparser.ast.type.ClassOrInterfaceType;

public class JavaParserHelperTest {

	@Test
	public void testGetClassOrInterfaceType() {
		Assert.assertEquals(new ClassOrInterfaceType(new ClassOrInterfaceType(null, Map.class.getSimpleName()),
				Entry.class.getSimpleName()), JavaParserHelper.getClassOrInterfaceType(Entry.class));
	}

}

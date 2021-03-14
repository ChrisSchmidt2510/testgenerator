package org.testgen.extension;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testgen.runtime.proxy.Proxified;
import org.testgen.runtime.proxy.impl.ReferenceProxy;

public class ClassExtensionTest {

	private class A implements Proxified {
	}

	private class B {
	}

	@Test
	public void testIsProxifiedClass() {
		Assertions.assertTrue(ClassExtension.isProxifiedClass(A.class));
		Assertions.assertFalse(ClassExtension.isProxifiedClass(B.class));
	}

	@Test
	public void testIsProxy() {
		Assertions.assertTrue(ClassExtension.isProxy(ReferenceProxy.class));
		Assertions.assertFalse(ClassExtension.isProxy(String.class));
	}

}

package org.testgen.extension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
		assertTrue(ClassExtension.isProxifiedClass(A.class));
		assertFalse(ClassExtension.isProxifiedClass(B.class));
	}

	@Test
	public void testIsProxy() {
		assertTrue(ClassExtension.isProxy(ReferenceProxy.class));
		assertFalse(ClassExtension.isProxy(String.class));
	}

}

package org.testgen.extension;

import org.junit.Assert;
import org.junit.Test;
import org.testgen.runtime.proxy.Proxified;
import org.testgen.runtime.proxy.impl.ReferenceProxy;

public class ClassExtensionTest {

	private class A implements Proxified {
	}

	private class B {
	}

	@Test
	public void testIsProxifiedClass() {
		Assert.assertTrue(ClassExtension.isProxifiedClass(A.class));
		Assert.assertFalse(ClassExtension.isProxifiedClass(B.class));
	}

	@Test
	public void testIsProxy() {
		Assert.assertTrue(ClassExtension.isProxy(ReferenceProxy.class));
		Assert.assertFalse(ClassExtension.isProxy(String.class));
	}

}

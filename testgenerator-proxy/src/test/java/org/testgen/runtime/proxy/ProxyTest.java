package org.testgen.runtime.proxy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ProxyTest
{

  @Test
  public void testProxyEquals()
  {
    assertTrue(new ProxyTestBean().equals(new ProxyTestBean()));
  }

  @Test
  public void testProxyHashcode()
  {
    assertEquals(new ProxyTestBean().hashCode(), new ProxyTestBean().hashCode());
  }

  @Test
  public void testProxyToString()
  {
    assertEquals(new ProxyTestBean().toString(), new ProxyTestBean().toString());
  }
}

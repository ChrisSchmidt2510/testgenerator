package de.nvg.proxy;

import static org.junit.Assert.*;
import org.junit.Test;

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

package de.nvg.proxy.impl;

import de.nvg.proxy.Proxy;

public class LongProxy extends Proxy
{
  private long value;

  public LongProxy(long value, Object parent, String fieldName)
  {
    super(parent, fieldName, "long");
    this.value = value;
  }

  public LongProxy(Object parent, String fieldName)
  {
    super(parent, fieldName, "long");
  }

  public long getValue()
  {
    trackReadFieldCalls();
    return value;
  }

  public void setValue(long value)
  {
    this.value = value;
  }
}

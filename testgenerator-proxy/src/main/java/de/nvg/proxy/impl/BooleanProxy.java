package de.nvg.proxy.impl;

import de.nvg.proxy.Proxy;

public class BooleanProxy extends Proxy
{
  private boolean value;

  public BooleanProxy(boolean value, Object parent, String fieldName)
  {
    super(parent, fieldName, "Z");
    this.value = value;
  }

  public BooleanProxy(Object parent, String fieldName)
  {
    super(parent, fieldName, "Z");
  }

  public void setValue(boolean value)
  {
    this.value = value;
  }

  public boolean getValue()
  {
    trackReadFieldCalls();

    return value;
  }

}

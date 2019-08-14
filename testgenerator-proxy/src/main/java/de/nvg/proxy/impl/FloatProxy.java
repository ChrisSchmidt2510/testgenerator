package de.nvg.proxy.impl;

import de.nvg.proxy.Proxy;

public class FloatProxy extends Proxy
{
  private float value;

  public FloatProxy(float value, Object parent, String fieldName)
  {
    super(parent, fieldName, "float");
    this.value = value;
  }

  public FloatProxy(Object parent, String fieldName)
  {
    super(parent, fieldName, "float");
  }

  public float getValue()
  {
    trackReadFieldCalls();
    return value;
  }

  public void setValue(float value)
  {
    this.value = value;
  }

}

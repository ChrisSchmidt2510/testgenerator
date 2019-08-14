package de.nvg.proxy.impl;

import de.nvg.proxy.Proxy;

public class IntegerProxy extends Proxy
{
  private int value;

  public IntegerProxy(int value, Object parent, String fieldName, String dataType)
  {
    super(parent, fieldName, dataType);
    this.value = value;
  }

  public IntegerProxy(Object parent, String fieldName, String dataType)
  {
    super(parent, fieldName, dataType);
  }

  public int getValue()
  {
    trackReadFieldCalls();
    return value;
  }

  public byte getByteValue()
  {
    trackReadFieldCalls();
    return (byte) value;
  }

  public short getShortValue()
  {
    trackReadFieldCalls();
    return (short) value;
  }

  public char getCharValue()
  {
    trackReadFieldCalls();
    return (char) value;
  }

  public void setValue(int value)
  {
    this.value = value;
  }

}

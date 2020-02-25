package de.nvg.agent.classdata.modification.indy;

import javassist.bytecode.ConstPool;

public enum InvocationType {
	GET_FIELD(ConstPool.REF_getField), GET_STATIC(ConstPool.REF_getStatic), PUT_FIELD(ConstPool.REF_putField),
	PUT_STATIC(ConstPool.REF_putStatic), INVOKE_VIRTUAL(ConstPool.REF_invokeVirtual),
	INVOKE_STATIC(ConstPool.REF_invokeStatic), INVOKE_SPECIAL(ConstPool.REF_invokeSpecial),
	INVOKE_SPECIAL_NEW(ConstPool.REF_newInvokeSpecial), INVOKE_INTERFACE(ConstPool.REF_invokeInterface);

	private int index;

	private InvocationType(int index) {
		this.index = index;
	}

	public int getValue() {
		return index;
	}
}

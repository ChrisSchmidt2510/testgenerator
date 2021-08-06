package org.testgen.agent.classdata.constants;

import javassist.Modifier;
import javassist.bytecode.AccessFlag;

public final class Modifiers {

	
	private Modifiers() {
	}
	
	public static boolean isConstant(int modifier) {
		return Modifier.isFinal(modifier) && Modifier.isStatic(modifier);
	}
	
	public static boolean isSynthetic(int modifier) {
		return (modifier & AccessFlag.SYNTHETIC) != 0;
	}
}

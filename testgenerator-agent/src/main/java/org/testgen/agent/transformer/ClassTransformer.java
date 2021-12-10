package org.testgen.agent.transformer;

import javassist.CtClass;

public interface ClassTransformer {
	
	public boolean modifyClassFile(String className);
	
	public void transformClassFile(String className, CtClass ctClass);

}

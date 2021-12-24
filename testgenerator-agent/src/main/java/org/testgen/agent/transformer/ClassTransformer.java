package org.testgen.agent.transformer;

import javassist.CtClass;
/**
 * Interface for implement classfile manipulations in the testgenerator framework.
 *
 */
public interface ClassTransformer {
	/**
	 * checks if the classname matches the condition of this {@link ClassTransformer}
	 * @param className
	 * @return
	 */
	public boolean modifyClassFile(String className);
	/**
	 * The implementation of this method may transform the supplied class file and
     * return a new replacement class file.
	 * @param className
	 * @param ctClass
	 */
	public void transformClassFile(String className, CtClass ctClass);

}

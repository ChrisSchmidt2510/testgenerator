package org.testgen.agent.classdata.modification.indy;

import org.testgen.agent.classdata.modification.BytecodeUtils;

import javassist.bytecode.BootstrapMethodsAttribute.BootstrapMethod;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;

public abstract class AbstractLambdaBootstrapMethodCreator {
	private static final String BOOTSTRAP_METHOD_CLASS = "java/lang/invoke/LambdaMetafactory";
	private static final String BOOTSTRAP_METHOD_NAME = "metafactory";
	private static final String BOOTSTRAP_METHOD_DESC = "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;";

	private final ClassFile classFile;
	private final ConstPool constantPool;

	/**
	 * 
	 */
	public AbstractLambdaBootstrapMethodCreator(ClassFile classFile, ConstPool constantPool) {
		this.classFile = classFile;
		this.constantPool = constantPool;
	}

	/**
	 * 
	 * @param invocationType only one of the {@link ConstPool#REF_invokeStatic}
	 *                       constants are allowed
	 * @param className      of the indy callsite target class
	 * @param methodName     of indy callsite target method
	 */
	public int create(InvocationType invocationType, String className, String methodName) {

		int bootstrapMethodIndex = constantPool.addMethodrefInfo(constantPool.addClassInfo(BOOTSTRAP_METHOD_CLASS),
				BOOTSTRAP_METHOD_NAME, BOOTSTRAP_METHOD_DESC);

		int bootstrapMethodHandleIndex = constantPool.addMethodHandleInfo(ConstPool.REF_invokeStatic,
				bootstrapMethodIndex);

		int indyGenericMethodTypeIndex = constantPool
				.addMethodTypeInfo(constantPool.addUtf8Info(getGenericMethodType()));

		int indyBodyMethodIndex = constantPool.addMethodrefInfo(constantPool.addClassInfo(className), methodName,
				getTypedMethodType());

		int indyTargetMethodHandleIndex = constantPool.addMethodHandleInfo(invocationType.getValue(),
				indyBodyMethodIndex);

		int indyTypedMethodTypeIndex = constantPool.addMethodTypeInfo(constantPool.addUtf8Info(getTypedMethodType()));

		BootstrapMethod bootstrapMethod = new BootstrapMethod(bootstrapMethodHandleIndex,
				new int[] { indyGenericMethodTypeIndex, indyTargetMethodHandleIndex, indyTypedMethodTypeIndex });

		return BytecodeUtils.addBootstrapMethod(classFile, bootstrapMethod);
	}

	protected abstract String getGenericMethodType();

	protected abstract String getTypedMethodType();
}

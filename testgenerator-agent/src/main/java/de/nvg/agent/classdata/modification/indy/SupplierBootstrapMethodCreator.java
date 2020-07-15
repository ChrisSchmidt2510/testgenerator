package de.nvg.agent.classdata.modification.indy;

import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;

public class SupplierBootstrapMethodCreator extends AbstractBootstrapMethodCreator {
	private static final String INDY_SUPPLIER_GENERIC_RETURN_TYPE = "()Ljava/lang/Object;";

	private final String typedReturntype;

	public SupplierBootstrapMethodCreator(ClassFile classFile, ConstPool constantPool, String typedReturntype) {
		super(classFile, constantPool);
		this.typedReturntype = typedReturntype;
	}

	@Override
	protected String getGenericMethodType() {
		return INDY_SUPPLIER_GENERIC_RETURN_TYPE;
	}

	@Override
	protected String getTypedMethodType() {
		return typedReturntype;
	}

}

package de.nvg.javaagent.classdata.modification.indy;

import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;

public class SupplierBootstrapMethodCreator extends BootstrapMethodCreator {
	private static final String INDY_SUPPLIER_GENERIC_RETURN_TYPE = "()Ljava/lang/Object;";
	private static final String INDY_SUPPLIER_TYPED_RETURN_TYPE = "()Lde/nvg/runtime/classdatamodel/ClassData;";

	public SupplierBootstrapMethodCreator(ClassFile classFile, ConstPool constantPool) {
		super(classFile, constantPool);
	}

	@Override
	protected String getGenericMethodType() {
		return INDY_SUPPLIER_GENERIC_RETURN_TYPE;
	}

	@Override
	protected String getTypedMethodType() {
		return INDY_SUPPLIER_TYPED_RETURN_TYPE;
	}

}

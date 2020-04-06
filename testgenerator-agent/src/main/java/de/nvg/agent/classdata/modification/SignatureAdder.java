package de.nvg.agent.classdata.modification;

import java.util.ArrayList;
import java.util.List;

import de.nvg.agent.classdata.model.SignatureData;
import de.nvg.testgenerator.Wrapper;
import javassist.bytecode.Bytecode;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;

public class SignatureAdder {
	private static final String SIGNATURE_DATA_CLASSNAME = "de/nvg/runtime/classdatamodel/SignatureData";
	private static final String SIGNATURE_DATA_CONSTRUCTOR = "(Ljava/lang/Class;)V";
	private static final String SIGNATURE_DATA_METHOD_ADD_SUBTYPE = "addSubType";
	private static final String SIGNATURE_DATA_METHOD_ADD_SUBTYPE_DESC = "(Lde/nvg/runtime/classdatamodel/SignatureData;)V";

	private final ConstPool constantPool;

	public SignatureAdder(ConstPool constantPool) {
		this.constantPool = constantPool;
	}

	public int add(Bytecode code, SignatureData signature, Wrapper<Integer> localVariableCounter) {
		List<Integer> localVariableCounters = new ArrayList<>();

		for (SignatureData subSignature : signature.getSubTypes()) {
			localVariableCounters.add(add(code, subSignature, localVariableCounter));
		}

		code.addNew(SIGNATURE_DATA_CLASSNAME);
		code.add(Opcode.DUP);
		code.addLdc(constantPool.addClassInfo(BytecodeUtils.cnvDescriptorToJvmName(signature.getType())));
		code.addInvokespecial(SIGNATURE_DATA_CLASSNAME, MethodInfo.nameInit, SIGNATURE_DATA_CONSTRUCTOR);

		int currentSignature = localVariableCounter.getValue();
		localVariableCounter.setValue(currentSignature + 1);
		code.addAstore(currentSignature);

		for (Integer localVariable : localVariableCounters) {
			code.addAload(currentSignature);
			code.addAload(localVariable);
			code.addInvokevirtual(SIGNATURE_DATA_CLASSNAME, SIGNATURE_DATA_METHOD_ADD_SUBTYPE,
					SIGNATURE_DATA_METHOD_ADD_SUBTYPE_DESC);
		}

		return currentSignature;
	}
}

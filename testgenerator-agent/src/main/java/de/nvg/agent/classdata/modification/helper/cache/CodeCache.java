package de.nvg.agent.classdata.modification.helper.cache;

import static de.nvg.agent.classdata.modification.fields.ProxyConstants.*;

import java.util.HashMap;
import java.util.Map;

import de.nvg.agent.classdata.instructions.Instructions;
import de.nvg.agent.classdata.modification.BytecodeUtils;
import javassist.CtClass;
import javassist.bytecode.Bytecode;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import javassist.bytecode.Opcode;

public class CodeCache {
	private final CtClass clazz;
	private final ConstPool constantPool;

	private final Map<CodeType, byte[]> cache = new HashMap<>();

	public CodeCache(CtClass clazz, ConstPool constantPool) {
		this.clazz = clazz;
		this.constantPool = constantPool;
	}

	public byte[] getCodeOrGenerate(CodeType type) {
		if (cache.containsKey(type)) {
			return cache.get(type);
		}

		byte[] code = null;
		switch (type.operationType) {
		case CodeType.GET_PROXY_VALUE:
			code = generateGetProxyValue(type);
			break;
		case CodeType.SET_PROXY_VALUE:
			code = generateSetProxyValue(type);
			break;
		case CodeType.SET_PROXY_VALUE_METHOD:
			code = generateSetProxyValueMethod(type);
			break;
		case CodeType.NEW_PROXY:
			code = generateNewProxy(type);
			break;
		case CodeType.PROXY_INIT:
			code = generateProxyInit(type);
			break;
		default:
			throw new IllegalArgumentException(type.operationType + "isn't a valid operation-Type");
		}

		cache.put(type, code);

//		CodeAttribute ca = new CodeAttribute(constantPool, 0, 0, code, null);
//		Instructions.showCodeArray(System.out, ca.iterator(), constantPool);

		return code;
	}

	private byte[] generateGetProxyValue(CodeType type) {
		Bytecode code = new Bytecode(constantPool);
		code.addGetfield(clazz, type.fieldName, toDescriptor(type.proxy));
		code.addInvokevirtual(type.proxy, type.methodName, type.methodDescriptor);

		if (REFERENCE_PROXY_CLASSNAME.equals(type.proxy))
			code.addCheckcast(Instructions.isArrayType(type.fieldType) ? type.fieldType
					: type.fieldType.substring(1, type.fieldType.length() - 1));

		return code.get();
	}

	private byte[] generateSetProxyValue(CodeType type) {
		Bytecode code = new Bytecode(constantPool);
		code.addGetfield(clazz, type.fieldName, toDescriptor(type.proxy));
		return code.get();
	}

	private byte[] generateSetProxyValueMethod(CodeType type) {
		Bytecode code = new Bytecode(constantPool);
		code.addInvokevirtual(type.proxy, type.methodName, type.methodDescriptor);

		return code.get();
	}

	private byte[] generateNewProxy(CodeType type) {
		Bytecode code = new Bytecode(constantPool);
		code.addNew(type.proxy);
		code.addOpcode(Opcode.DUP);

		return code.get();
	}

	private byte[] generateProxyInit(CodeType type) {
		Bytecode code = new Bytecode(constantPool);
		code.addAload(0);
		code.addLdc(type.fieldName);

		if (INTEGER_PROXY_CLASSNAME.equals(type.proxy) || REFERENCE_PROXY_CLASSNAME.equals(type.proxy))
			BytecodeUtils.addClassInfoToBytecode(code, constantPool, Descriptor.toClassName(type.fieldType));
		code.addInvokespecial(type.proxy, type.methodName, type.methodDescriptor);
		code.addPutfield(clazz, type.fieldName, toDescriptor(type.proxy));

		return code.get();
	}

}

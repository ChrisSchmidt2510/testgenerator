package org.testgen.agent.classdata.instructions;

import java.util.Objects;

import javassist.bytecode.Mnemonic;
import javassist.bytecode.Opcode;

public final class Instruction {
	private final int codeArrayIndex;
	private final int opcode;
	private final int localVariableIndex;
	private final int offset;
	private final int arrayDimensions;

	private final int bootstrapMethodIndex;
	private final String type;
	private final String name;
	private final String classRef;
	private final String constantValue;

	private Instruction(int codeArrayIndex, int opcode, int localVariableIndex, int offset, int arrayDimensions,
			int bootstrapMethodIndex, String type, String name, String classRef, String constantValue) {
		this.codeArrayIndex = codeArrayIndex;
		this.opcode = opcode;
		this.localVariableIndex = localVariableIndex;
		this.offset = offset;
		this.arrayDimensions = arrayDimensions;
		this.bootstrapMethodIndex = bootstrapMethodIndex;
		this.type = type;
		this.name = name;
		this.classRef = classRef;
		this.constantValue = constantValue;
	}

	public int getCodeArrayIndex() {
		return codeArrayIndex;
	}

	public int getOpcode() {
		return opcode;
	}

	public String getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public String getClassRef() {
		return classRef;
	}

	public int getLocalVariableIndex() {
		return localVariableIndex;
	}

	public int getOffset() {
		return offset;
	}

	public int getArrayDimensions() {
		return arrayDimensions;
	}

	public int getBootstrapMethodIndex() {
		return bootstrapMethodIndex;
	}

	public String getConstantValue() {
		return constantValue;
	}

	@Override
	public int hashCode() {
		return Objects.hash(classRef, codeArrayIndex, localVariableIndex, name, offset, opcode, type, constantValue);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Instruction))
			return false;
		Instruction other = (Instruction) obj;
		return Objects.equals(classRef, other.classRef) && codeArrayIndex == other.codeArrayIndex
				&& localVariableIndex == other.localVariableIndex && Objects.equals(name, other.name)
				&& offset == other.offset && opcode == other.opcode && Objects.equals(type, other.type)
				&& Objects.equals(constantValue, other.constantValue);
	}

	@Override
	public String toString() {
		if (Opcode.PUTFIELD == opcode || Opcode.GETFIELD == opcode || Instructions.isInvokeInstruction(this)
				|| Opcode.GETSTATIC == opcode) {
			return codeArrayIndex + ": " + Mnemonic.OPCODE[opcode] + " " + classRef + "." + name + "(" + type + ")";

		} else if (Opcode.ALOAD == opcode || Opcode.ILOAD == opcode || Opcode.DLOAD == opcode || Opcode.FLOAD == opcode
				|| Opcode.LLOAD == opcode) {
			return codeArrayIndex + ": " + Mnemonic.OPCODE[opcode] + " " + localVariableIndex;

		} else if (Opcode.NEW == opcode) {
			return codeArrayIndex + ": " + Mnemonic.OPCODE[opcode] + " Class: " + classRef;

		} else if (Opcode.IFNULL == opcode || Opcode.GOTO == opcode) {
			return codeArrayIndex + ": " + Mnemonic.OPCODE[opcode] + " " + offset;

		} else if (Opcode.LDC == opcode)
			return codeArrayIndex + ": " + Mnemonic.OPCODE[opcode] + " " + type + " " + constantValue;

		return codeArrayIndex + ": " + Mnemonic.OPCODE[opcode];
	}

	public static class Builder {
		private int codeArrayIndex;
		private int opcode;
		private int localVariableIndex = -1;
		private int offset = -1;
		private int bootstrapMethodIndex = -1;
		private int arrayDimensions = -1;
		private String type;
		private String name;
		private String classRef;
		private String constantValue;

		public Builder withCodeArrayIndex(int codeArrayIndex) {
			this.codeArrayIndex = codeArrayIndex;
			return this;
		}

		public Builder withOpcode(int opcode) {
			this.opcode = opcode;
			return this;
		}

		public Builder withLocalVariableIndex(int localVariableIndex) {
			this.localVariableIndex = localVariableIndex;
			return this;
		}

		public Builder withOffset(int offset) {
			this.offset = offset;
			return this;
		}

		public Builder withArrayDimensions(int arrayDimensions) {
			this.arrayDimensions = arrayDimensions;
			return this;
		}

		public Builder withType(String type) {
			this.type = type;
			return this;
		}

		public Builder withName(String name) {
			this.name = name;
			return this;
		}

		public Builder withClassRef(String classRef) {
			this.classRef = classRef;
			return this;
		}

		public Builder withConstantValue(String constantValue) {
			this.constantValue = constantValue;
			return this;
		}

		public Builder withBootstrapMethodIndex(int bootstrapMehodIndex) {
			this.bootstrapMethodIndex = bootstrapMehodIndex;
			return this;
		}

		public Instruction build() {
			return new Instruction(codeArrayIndex, opcode, localVariableIndex, offset, arrayDimensions,
					bootstrapMethodIndex, type, name, classRef, constantValue);

		}
	}

}

package de.nvg.javaagent.classdata;

import java.util.Objects;

public class Instruction {
	private final int codeArrayIndex;
	private final int opcode;
	private final int localVariableIndex;
	private final int offset;
	private final String type;
	private final String name;
	private final String classRef;

	private Instruction(int codeArrayIndex, int opcode, int localVariableIndex, int offset, String type, String name,
			String classRef) {
		this.codeArrayIndex = codeArrayIndex;
		this.opcode = opcode;
		this.localVariableIndex = localVariableIndex;
		this.offset = offset;
		this.type = type;
		this.name = name;
		this.classRef = classRef;
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

	@Override
	public int hashCode() {
		return Objects.hash(name, opcode, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Instruction)) {
			return false;
		}
		Instruction other = (Instruction) obj;
		return codeArrayIndex == other.codeArrayIndex && Objects.equals(name, other.name) && opcode == other.opcode
				&& Objects.equals(type, other.type);
	}

	public static class Builder {
		private int codeArrayIndex;
		private int opcode;
		private int localVariableIndex = -1;
		private int offset = -1;
		private String type;
		private String name;
		private String classRef;

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

		public Instruction build() {
			Instruction bytecode = new Instruction(codeArrayIndex, opcode, localVariableIndex, offset, type, name,
					classRef);

			return bytecode;
		}
	}

}

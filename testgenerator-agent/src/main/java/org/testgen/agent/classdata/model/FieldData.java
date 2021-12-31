package org.testgen.agent.classdata.model;

import java.util.Objects;

import org.testgen.agent.classdata.constants.Modifiers;

import javassist.Modifier;

public final class FieldData {
	private final String dataType;
	private final String name;
	private final int modifier;
	/** Is only used for genericTypes */
	private final SignatureData signature;

	private FieldData(String dataType, String name, int modifier, SignatureData signature) {
		this.dataType = dataType;
		this.name = name;
		this.modifier = modifier;
		this.signature = signature;
	}

	public String getDataType() {
		return dataType;
	}

	public String getName() {
		return name;
	}

	public boolean isMutable() {
		return !Modifier.isFinal(modifier);
	}

	public boolean isStatic() {
		return Modifier.isStatic(modifier);
	}

	public boolean isPublic() {
		return Modifier.isPublic(modifier);
	}

	public boolean isModifiable() {
		return !isPublic() && !isPackage() && !isSynthetic();
	}

	public boolean isSynthetic() {
		return Modifiers.isSynthetic(modifier);
	}

	public SignatureData getSignature() {
		return signature;
	}

	public boolean isPackage() {
		return Modifier.isPackage(modifier);
	}

	@Override
	public int hashCode() {
		return Objects.hash(dataType, name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof FieldData)) {
			return false;
		}
		FieldData other = (FieldData) obj;
		// implemented like the definition of the @see jvms 4.5
		return Objects.equals(dataType, other.dataType) && Objects.equals(name, other.name);
	}

	@Override
	public String toString() {
		return "FieldData [dataType=" + dataType + ", name=" + name + ", modifier=" + modifier + ", signature="
				+ signature + "]";
	}

	public static class Builder {
		private String name;
		private String dataType;
		private SignatureData signature;
		private int modifier;

		public Builder withName(String name) {
			this.name = name;
			return this;
		}

		public Builder withDataType(String dataType) {
			this.dataType = dataType;
			return this;
		}

		public Builder withSignature(SignatureData signature) {
			this.signature = signature;
			return this;
		}

		public Builder withModifier(int modifier) {
			this.modifier = modifier;
			return this;
		}

		public FieldData build() {
			return new FieldData(dataType, name, modifier, signature);
		}
	}

}

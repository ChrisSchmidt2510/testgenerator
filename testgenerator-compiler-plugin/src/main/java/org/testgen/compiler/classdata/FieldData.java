package org.testgen.compiler.classdata;

import java.util.Objects;

import javax.lang.model.type.TypeKind;

import com.sun.tools.javac.util.Name;

public final class FieldData {
	private final String dataType;
	private final TypeKind primitiveDataType;
	private final Name name;
	private final boolean mutable;
	private final boolean isStatic;
	private final boolean isPublic;
	/** Is only used for genericTypes */
	private final SignatureData signature;

	private String comment;

	private FieldData(String dataType, Name name, boolean mutable, boolean isStatic, boolean isPublic,
			SignatureData signature) {
		this.dataType = dataType;
		this.primitiveDataType = null;
		this.name = name;
		this.mutable = mutable;
		this.isStatic = isStatic;
		this.isPublic = isPublic;
		this.signature = signature;
	}

	private FieldData(TypeKind primitiveDataType, Name name, boolean mutable, boolean isStatic, boolean isPublic,
			SignatureData signature) {
		this.dataType = null;
		this.primitiveDataType = primitiveDataType;
		this.name = name;
		this.mutable = mutable;
		this.isStatic = isStatic;
		this.isPublic = isPublic;
		this.signature = signature;
	}

	public String getDataType() {
		return dataType;
	}

	public TypeKind getPrimitiveDataType() {
		return primitiveDataType;
	}

	public boolean isPrimitive() {
		return primitiveDataType != null;
	}

	public Name getName() {
		return name;
	}

	public boolean isMutable() {
		return mutable;
	}

	public boolean isStatic() {
		return isStatic;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public SignatureData getSignature() {
		return signature;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
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
		return "FieldData [dataType=" + dataType + ", name=" + name + ", mutable=" + mutable + ", isStatic=" + isStatic
				+ ", signature=" + signature + ", comment=" + comment + "]";
	}

	public static class Builder {
		private Name name;
		private String dataType;
		private TypeKind primitiveDataType;
		private SignatureData signature;
		private boolean mutable;
		private boolean isStatic;
		private boolean isPublic;

		public Builder withName(Name name) {
			this.name = name;
			return this;
		}

		public Builder withDataType(String dataType) {
			this.dataType = dataType;
			return this;
		}

		public Builder withPrimitiveDataType(TypeKind type) {
			this.primitiveDataType = type;
			return this;
		}

		public Builder withSignature(SignatureData signature) {
			this.signature = signature;
			return this;
		}

		public Builder isMutable(boolean mutable) {
			this.mutable = mutable;
			return this;
		}

		public Builder isStatic(boolean isStatic) {
			this.isStatic = isStatic;
			return this;
		}

		public Builder isPublic(boolean isPublic) {
			this.isPublic = isPublic;
			return this;
		}

		public FieldData build() {
			if (primitiveDataType != null) {
				return new FieldData(primitiveDataType, name, mutable, isStatic, isPublic, signature);
			}

			return new FieldData(dataType, name, mutable, isStatic, isPublic, signature);
		}
	}

}

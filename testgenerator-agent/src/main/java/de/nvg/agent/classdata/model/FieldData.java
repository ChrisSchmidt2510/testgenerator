package de.nvg.agent.classdata.model;

import java.util.Objects;

public class FieldData {
	private final String dataType;
	private final String name;
	private final boolean mutable;
	private final boolean isStatic;
	private final boolean isPublic;
	/** Is only used for genericTypes */
	private final SignatureData signature;

	private String comment;

	private FieldData(String dataType, String name, boolean mutable, boolean isStatic, boolean isPublic,
			SignatureData signature) {
		this.dataType = dataType;
		this.name = name;
		this.mutable = mutable;
		this.isStatic = isStatic;
		this.isPublic = isPublic;
		this.signature = signature;
	}

	public String getDataType() {
		return dataType;
	}

	public String getName() {
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
		private String name;
		private String dataType;
		private SignatureData signature;
		private boolean mutable;
		private boolean isStatic;
		private boolean isPublic;

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
			return new FieldData(dataType, name, mutable, isStatic, isPublic, signature);
		}
	}

}

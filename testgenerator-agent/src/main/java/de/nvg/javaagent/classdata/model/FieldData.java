package de.nvg.javaagent.classdata.model;

import java.util.Objects;

public class FieldData {
	private final String dataType;
	private final String name;
	private final boolean mutable;
	private final boolean isStatic;
	/** Is only used for genericTypes */
	private final String signature;

	private String comment;

	private FieldData(String dataType, String name, boolean mutable, boolean isStatic, String signature) {
		this.dataType = dataType;
		this.name = name;
		this.mutable = mutable;
		this.isStatic = isStatic;
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

	public String getSignature() {
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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof FieldData))
			return false;
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
		private String signature;
		private boolean mutable;
		private boolean isStatic;

		public Builder withName(String name) {
			this.name = name;
			return this;
		}

		public Builder withDataType(String dataType) {
			this.dataType = dataType;
			return this;
		}

		public Builder withSignature(String signature) {
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

		public FieldData build() {
			return new FieldData(dataType, name, mutable, isStatic, signature);
		}
	}

}

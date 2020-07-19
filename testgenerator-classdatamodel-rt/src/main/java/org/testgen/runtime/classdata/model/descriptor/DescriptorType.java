package org.testgen.runtime.classdata.model.descriptor;

public interface DescriptorType {

	public default boolean isBasicType() {
		return this instanceof BasicType;
	}

	public default boolean isSignatureType() {
		return this instanceof SignatureType;
	}

	public default BasicType castToBasicType() {
		if (isBasicType())
			return (BasicType) this;

		return null;
	}

	public default SignatureType castToSignatureType() {
		if (isSignatureType())
			return (SignatureType) this;

		return null;
	}

}

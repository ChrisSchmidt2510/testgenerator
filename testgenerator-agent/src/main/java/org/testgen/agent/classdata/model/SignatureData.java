package org.testgen.agent.classdata.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SignatureData {
	private final String type;
	private final List<SignatureData> subTypes = new ArrayList<>();

	public SignatureData(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void addSubType(SignatureData subType) {
		this.subTypes.add(subType);
	}

	public void addSubTypes(List<SignatureData> subTypes) {
		this.subTypes.addAll(subTypes);
	}

	public List<SignatureData> getSubTypes() {
		return Collections.unmodifiableList(subTypes);
	}

	@Override
	public int hashCode() {
		return Objects.hash(subTypes, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof SignatureData))
			return false;
		SignatureData other = (SignatureData) obj;
		return Objects.equals(subTypes, other.subTypes) && Objects.equals(type, other.type);
	}

	@Override
	public String toString() {
		return "Signature: \n" + signaturePrinter(this, 0);
	}

	private String signaturePrinter(SignatureData signature, int tabs) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < tabs; i++) {
			builder.append("\t");
		}
		builder.append(signature.getType() + "\n");

		for (SignatureData subSignature : signature.subTypes) {
			builder.append(signaturePrinter(subSignature, tabs + 1));
		}

		return builder.toString();
	}

}

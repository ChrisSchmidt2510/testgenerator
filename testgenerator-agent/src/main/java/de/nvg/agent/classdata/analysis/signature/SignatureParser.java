package de.nvg.agent.classdata.analysis.signature;

import java.util.ArrayList;
import java.util.List;

import de.nvg.agent.classdata.model.SignatureData;

public final class SignatureParser {
	private static final Character GENERIC_PARAM_START = '<';
	private static final Character GENERIC_PARAM_END = '>';
	private static final Character TYPE_DELIMETER = ';';

	private SignatureParser() {
	}

	public static SignatureData parse(String signature) throws SignatureParserException {

		if (signature.contains(GENERIC_PARAM_START.toString())) {
			int genericParamStart = signature.indexOf(GENERIC_PARAM_START);
			int typeDelimeter = signature.substring(0, genericParamStart).indexOf(TYPE_DELIMETER);

			String type = signature.substring(typeDelimeter == -1 ? 0 : (typeDelimeter + 1), genericParamStart)
					+ TYPE_DELIMETER;

			validate(type);

			SignatureData model = new SignatureData(type);

			String subTypeSignature = signature.substring(signature.indexOf(GENERIC_PARAM_START) + 1,
					signature.lastIndexOf(GENERIC_PARAM_END));

			List<SignatureData> subTypes = parseSubSignatureElements(subTypeSignature);

			model.addSubTypes(subTypes);

			return model;
		}

		return null;

	}

	private static List<SignatureData> parseSubSignatureElements(String signature) throws SignatureParserException {
		List<SignatureData> signatures = new ArrayList<>();

		while (signature.contains(TYPE_DELIMETER.toString())) {
			String signatureElement = signature.substring(0, signature.indexOf(TYPE_DELIMETER) + 1);

			if (!signatureElement.contains(GENERIC_PARAM_START.toString())) {

				validate(signatureElement);

				signatures.add(new SignatureData(signatureElement));
				signature = signature.substring(signature.indexOf(TYPE_DELIMETER) + 1);

			} else {
				int genericParamEnd = findCorrectGenericParamEnd(signature);
				String subSignature = signature.substring(0, genericParamEnd);
				signatures.add(parse(subSignature));
				signature = signature.substring(genericParamEnd + 1);
			}
		}

		return signatures;
	}

	private static void validate(String signatureElement) throws SignatureParserException {
		if (!signatureElement.startsWith("L") || !signatureElement.endsWith(TYPE_DELIMETER.toString())) {
			throw new SignatureParserException(signatureElement + " cant't be parsed into a valid Signature");
		}
	}

	private static int findCorrectGenericParamEnd(String param) {
		int neededGenericParamEnds = 0;

		char[] paramArray = param.toCharArray();

		for (int i = 0; i < paramArray.length; i++) {
			char c = paramArray[i];

			if (GENERIC_PARAM_START == c) {
				neededGenericParamEnds++;
			} else if (TYPE_DELIMETER == c) {
				if (neededGenericParamEnds == 0) {
					return i;
				}
			} else if (GENERIC_PARAM_END == c) {
				neededGenericParamEnds--;
			}
		}

		throw new IllegalArgumentException("invalid generic parameter");
	}
}

package de.nvg.agent.classdata.analysis.signature;

public class SignatureParserException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1251731493128714717L;

	public SignatureParserException(String message) {
		super(message);
	}

	SignatureParserException(SignatureParserException e) {
		super(e.getMessage());
	}

}

package org.testgen.agent;

public class AgentException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -157848420930793836L;

	public AgentException(String message) {
		super(message);
	}

	public AgentException(String message, Throwable cause) {
		super(message, cause);
	}

}

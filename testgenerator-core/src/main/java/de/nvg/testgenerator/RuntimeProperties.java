package de.nvg.testgenerator;

public class RuntimeProperties {
	private static final RuntimeProperties INSTANCE = new RuntimeProperties();

	private String className;
	private String method;
	private String methodDescriptor;
	private String blPackage;
	private boolean traceGetterCalls;
	private boolean activateTracking = false;

	public boolean isTrackingActive() {
		return activateTracking;
	}

	public void setActivateTracking(boolean activateTracking) {
		this.activateTracking = activateTracking;
	}

	private RuntimeProperties() {
	}

	public static RuntimeProperties getInstance() {
		return INSTANCE;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getMethodDescriptor() {
		return methodDescriptor;
	}

	public void setMethodDescriptor(String methodDescriptor) {
		this.methodDescriptor = methodDescriptor;
	}

	public String getBlPackage() {
		return blPackage;
	}

	public void setBlPackage(String blPackage) {
		this.blPackage = blPackage;
	}

	public boolean isTraceGetterCalls() {
		return traceGetterCalls;
	}

	public void setTraceGetterCalls(boolean traceGetterCalls) {
		this.traceGetterCalls = traceGetterCalls;
	}

}

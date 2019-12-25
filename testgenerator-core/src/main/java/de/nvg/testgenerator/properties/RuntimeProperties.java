package de.nvg.testgenerator.properties;

public class RuntimeProperties {
	private static final RuntimeProperties INSTANCE = new RuntimeProperties();

	private boolean activateTracking = false;

	private RuntimeProperties() {
	}

	public static RuntimeProperties getInstance() {
		return INSTANCE;
	}

	public boolean isTrackingActive() {
		return activateTracking;
	}

	public void setActivateTracking(boolean activateTracking) {
		this.activateTracking = activateTracking;
	}

}

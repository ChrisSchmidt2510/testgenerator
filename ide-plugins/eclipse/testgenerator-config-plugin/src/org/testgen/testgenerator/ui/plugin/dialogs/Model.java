package org.testgen.testgenerator.ui.plugin.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.ILaunchConfiguration;

public class Model {

	public static final String AGENT_TYPE_TESTGENERATOR = "Testgenerator";
	public static final String AGENT_TYPE_TESTGENERATOR_FULL = "TestgeneratorFull";

	private String agentType;

	private String className;

	private List<String> methods = new ArrayList<>();
	private int selectedMethodIndex;

	private List<String> blPackages = new ArrayList<>();

	private ILaunchConfiguration launchConfiguration;

	// optional Parameters
	private List<String> blPackageJarDest = new ArrayList<>();

	private boolean traceReadFieldAccess;

	private boolean useTestgeneratorBootstrap;

	private String printClassDirectory;

	private String costumTestgeneratorClassName;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void setMethods(List<String> methods) {
		this.methods = methods;
	}

	public List<String> getMethods() {
		return methods;
	}

	public void setSelectedMethodIndex(int index) {
		this.selectedMethodIndex = index;
	}

	public int getSelectedMethodIndex() {
		return selectedMethodIndex;
	}

	public void setBlPackages(List<String> blPackages) {
		this.blPackages = blPackages;
	}

	public List<String> getBlPackages() {
		return blPackages;
	}

	public List<String> getBlPackageJarDest() {
		return blPackageJarDest;
	}

	public void setBlPackageJarDest(List<String> blPackageJarDest) {
		this.blPackageJarDest = blPackageJarDest;
	}

	public boolean isTraceReadFieldAccess() {
		return traceReadFieldAccess;
	}

	public void setTraceReadFieldAccess(boolean traceReadFieldAccess) {
		this.traceReadFieldAccess = traceReadFieldAccess;
	}

	public String getPrintClassDirectory() {
		return printClassDirectory;
	}

	public void setPrintClassDirectory(String printClassDirectory) {
		this.printClassDirectory = printClassDirectory;
	}

	public String getCostumTestgeneratorClassName() {
		return costumTestgeneratorClassName;
	}

	public void setCostumTestgeneratorClassName(String costumTestgeneratorClassName) {
		this.costumTestgeneratorClassName = costumTestgeneratorClassName;
	}

	public boolean useTestgeneratorBootstrap() {
		return useTestgeneratorBootstrap;
	}

	public void setUsetestgeneratorBootstrap(boolean usetestgeneratorBootrap) {
		this.useTestgeneratorBootstrap = usetestgeneratorBootrap;
	}

	public ILaunchConfiguration getLaunchConfiguration() {
		return launchConfiguration;
	}

	public String getAgentType() {
		return agentType;
	}

	public void setAgentType(String agentType) {
		this.agentType = agentType;
	}

	public void setLaunchConfiguration(ILaunchConfiguration launchConfiguration) {
		this.launchConfiguration = launchConfiguration;
	}

}

package org.testgen.testgenerator.ui.plugin.dialogs.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.debug.core.ILaunchConfiguration;

public class Model {

	public static final String AGENT_TYPE_TESTGENERATOR = "Testgenerator";
	public static final String AGENT_TYPE_TESTGENERATOR_FULL = "TestgeneratorFull";

	private String agentType;

	private String className;

	private List<String> methods = new ArrayList<>();
	private String selectedMethod;

	private List<BlProject> projects = new ArrayList<>();

	private ILaunchConfiguration launchConfiguration;

	private boolean traceReadFieldAccess;

	private boolean useTestgeneratorBootstrap;

	private String costumTestgeneratorClassName;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void setMethods(Collection<String> methods) {
		this.methods.clear();
		this.methods.addAll(methods);
	}

	public List<String> getMethods() {
		return methods;
	}

	public void setSelectedMethod(String index) {
		this.selectedMethod = index;
	}

	public String getSelectedMethod() {
		return selectedMethod;
	}

	public void addProject(BlProject project) {
		this.projects.add(project);
	}

	public List<BlProject> getProjects() {
		return projects;
	}

	public boolean isTraceReadFieldAccess() {
		return traceReadFieldAccess;
	}

	public void setTraceReadFieldAccess(boolean traceReadFieldAccess) {
		this.traceReadFieldAccess = traceReadFieldAccess;
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

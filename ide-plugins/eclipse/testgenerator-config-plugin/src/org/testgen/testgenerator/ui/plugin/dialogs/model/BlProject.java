package org.testgen.testgenerator.ui.plugin.dialogs.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;

public class BlProject {
	private IProject project;

	private IPackageFragmentRoot fragmentRoot;

	private IPath outputLocation;

	private List<IPackageFragment> selectedPackages = new ArrayList<>();
	private List<Dependency> dependencies = new ArrayList<>();

	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

	public void setFragmentRoot(IPackageFragmentRoot fragmentRoot) {
		this.fragmentRoot = fragmentRoot;
	}

	public IPackageFragmentRoot getFragmentRoot() {
		return this.fragmentRoot;
	}

	public IPath getOutputLocation() {
		return outputLocation;
	}

	public void setOutputLocation(IPath outputLocation) {
		this.outputLocation = outputLocation;
	}

	public List<Dependency> getDependencies() {
		return dependencies;
	}

	public void addDependency(Dependency dependency) {
		dependencies.add(dependency);
	}

	public List<IPackageFragment> getSelectedPackages() {
		return selectedPackages;
	}

	public void addSelectedPackage(IPackageFragment packageFragment) {
		selectedPackages.add(packageFragment);
	}

}

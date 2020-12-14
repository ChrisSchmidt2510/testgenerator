package org.testgen.testgenerator.ui.plugin.dialogs.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;

public class Dependency {

	private final boolean project;

	private String name;
	private IPackageFragmentRoot packageFragmentRoot;
	private List<IPackageFragment> selectedPackages = new ArrayList<>();

	public Dependency(boolean project) {
		this.project = project;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public IPackageFragmentRoot getPackageFragmentRoot() {
		return packageFragmentRoot;
	}

	public void setPackageFragmentRoot(IPackageFragmentRoot packageFragmentRoot) {
		this.packageFragmentRoot = packageFragmentRoot;
	}

	public List<IPackageFragment> getSelectedPackages() {
		return selectedPackages;
	}

	public void addSelectedPackage(IPackageFragment packageFragment) {
		this.selectedPackages.add(packageFragment);
	}

	public boolean isProject() {
		return project;
	}

}

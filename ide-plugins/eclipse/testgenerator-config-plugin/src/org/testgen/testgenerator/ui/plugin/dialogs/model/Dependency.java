package org.testgen.testgenerator.ui.plugin.dialogs.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;
import org.testgen.testgenerator.ui.plugin.TestgeneratorActivator;

@SuppressWarnings("restriction")
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

	public String getPackageFragmentPath() {
		if (project) {
			IJavaProject javaProject = packageFragmentRoot.getJavaProject();

			try {
				IProject iProject = javaProject.getProject();

				if (iProject.hasNature(IMavenConstants.NATURE_ID)) {
					IMavenProjectRegistry projectRegistry = MavenPlugin.getMavenProjectRegistry();
					IMavenProjectFacade mavenProject = projectRegistry.getProject(iProject);

					return iProject.getFolder(mavenProject.getOutputLocation()).getLocation().toPortableString();
				}
			} catch (CoreException e) {
				TestgeneratorActivator.log(e);
			}

			throw new IllegalArgumentException(String.format("Project Dependency %s has to be a Maven Project", name));
		} else {
			return packageFragmentRoot.getPath().removeLastSegments(1).toPortableString();
		}
	}

}

package org.testgen.testgenerator.ui.plugin.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.packageview.PackageCache;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerContentProvider;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerLabelProvider;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;
import org.eclipse.m2e.jdt.IClasspathManager;
import org.eclipse.m2e.jdt.MavenJdtPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.ide.IDE.SharedImages;
import org.testgen.testgenerator.ui.plugin.TestgeneratorActivator;
import org.testgen.testgenerator.ui.plugin.dialogs.model.BlProject;
import org.testgen.testgenerator.ui.plugin.dialogs.model.Dependency;

@SuppressWarnings("restriction")
public class PackageSelectionDialog extends SelectionDialog {

	enum DialogType {
		PROJECT_SELECTION, PROJECT, DEPENDENCY;
	}

	private Combo project;

	private TreeViewer packageViewer;

	private TableViewer dependencyViewer;

	private Map<String, IProject> projects = new HashMap<>();

	private BlProject selectedProject = new BlProject();

	private Dependency dependency;

	private final DialogType dialogType;

	/**
	 * @wbp.parser.constructor
	 */
	public PackageSelectionDialog(Shell parentShell) {
		super(parentShell);

		initModel();

		this.dialogType = DialogType.PROJECT_SELECTION;
	}

	public PackageSelectionDialog(Shell parentShell, BlProject selectedProject) {
		super(parentShell);

		this.selectedProject = selectedProject;
		this.dialogType = DialogType.PROJECT;
	}

	public PackageSelectionDialog(Shell parentShell, Dependency dependency) {
		super(parentShell);

		this.dependency = dependency;
		this.dialogType = DialogType.DEPENDENCY;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		if (DialogType.PROJECT_SELECTION == dialogType) {
			newShell.setText("Select Project");
		} else if (DialogType.PROJECT == dialogType) {
			newShell.setText("update Project");
		} else if (DialogType.DEPENDENCY == dialogType) {
			newShell.setText("update Dependency");
		}
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);

		initDialog(container);
		container.setLayout(null);

		return container;
	}

	private void initDialog(Composite container) {
		project = new Combo(container, SWT.NONE);
		project.setBounds(80, 10, 400, 28);
		projects.keySet().forEach(project::add);
		project.addListener(SWT.Selection, e -> updatePackageViewer());
		project.setEnabled(DialogType.PROJECT_SELECTION == dialogType);

		PackageContentProvider contentProvider = new PackageContentProvider();
		PackageExplorerLabelProvider labelProvider = new PackageExplorerLabelProvider(
				new PackageExplorerContentProvider(false));

		packageViewer = new TreeViewer(container, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL);
		packageViewer.setContentProvider(contentProvider);
		packageViewer.setLabelProvider(labelProvider);

		Tree tree = packageViewer.getTree();
		tree.setBounds(10, 61, 624, 378);

		Label lblProject = new Label(container, SWT.NONE);
		lblProject.setBounds(10, 10, 57, 20);
		lblProject.setText(DialogType.DEPENDENCY == dialogType ? "Dependency" : "Project");

		Label lblSpacer = new Label(container, SWT.NONE);
		lblSpacer.setBounds(634, 23, 10, 20);

		if (DialogType.DEPENDENCY != dialogType) {
			Group grpDependencies = new Group(container, SWT.NONE);
			grpDependencies.setText("Dependencies");
			grpDependencies.setBounds(10, 462, 624, 165);

			dependencyViewer = new TableViewer(grpDependencies,
					SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.FULL_SELECTION);
			dependencyViewer.setContentProvider(ArrayContentProvider.getInstance());
			dependencyViewer.addDoubleClickListener(e -> dependencyViewerDoubleClick());

			Table table = dependencyViewer.getTable();
			table.setBounds(10, 27, 604, 128);
			table.setHeaderVisible(true);

			TableViewerColumn colName = new TableViewerColumn(dependencyViewer, SWT.NONE);
			colName.setLabelProvider(new ColumnLabelProvider() {

				@Override
				public String getText(Object element) {
					return ((Dependency) element).getName();
				}

				@Override
				public Image getImage(Object element) {

					return ((Dependency) element).isProject()
							? PlatformUI.getWorkbench().getSharedImages().getImage(SharedImages.IMG_OBJ_PROJECT)
							: JavaPluginImages.DESC_OBJS_EXTJAR.createImage();
				}
			});
			TableColumn tblColName = colName.getColumn();
			tblColName.setWidth(250);
			tblColName.setText("Name");

			TableViewerColumn colSelectedPackages = new TableViewerColumn(dependencyViewer, SWT.NONE);
			colSelectedPackages.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					Dependency dependency = (Dependency) element;

					List<String> packageNames = dependency.getSelectedPackages().stream()
							.map(IPackageFragment::getElementName).collect(Collectors.toList());

					return String.join(",", packageNames);
				}

				@Override
				public Image getImage(Object element) {
					return JavaPluginImages.DESC_OBJS_PACKAGE.createImage();
				}
			});
			TableColumn tblColSelectedProjects = colSelectedPackages.getColumn();
			tblColSelectedProjects.setWidth(350);
			tblColSelectedProjects.setText("Selected Packages");
		}

		if (DialogType.DEPENDENCY == dialogType) {
			setDependencyValuesInDialog();
		} else if (DialogType.PROJECT == dialogType) {
			setProjectValuesInDialog();
		}

	}

	private void initModel() {
		List<IProject> mavenProjects = Arrays.stream(ResourcesPlugin.getWorkspace().getRoot().getProjects())
				.filter(project -> {
					try {
						return project.isOpen() && project.hasNature(IMavenConstants.NATURE_ID);
					} catch (CoreException e) {
						TestgeneratorActivator.log(e);
						return false;
					}
				}).collect(Collectors.toList());

		mavenProjects.forEach(javaProject -> projects.put(javaProject.getName(), javaProject));
	}

	class PackageContentProvider implements ITreeContentProvider {

		@Override
		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof IPackageFragmentRoot) {
				IPackageFragmentRoot packageRoot = (IPackageFragmentRoot) parentElement;

				try {
					IJavaElement[] packages = packageRoot.getChildren();
					List<IPackageFragment> modifiedPackages = new ArrayList<>();

					for (IJavaElement element : packages) {
						IPackageFragment singlePackage = (IPackageFragment) element;

						if (!singlePackage.isDefaultPackage() && !singlePackage.getElementName().contains(".")) {
							PackageCache cache = new PackageCache(packageRoot);

							modifiedPackages.add(getFolded(cache, singlePackage));
						}
					}

					return modifiedPackages.toArray();

				} catch (CoreException e) {
					TestgeneratorActivator.log(e);

					return null;
				}

			} else if (parentElement instanceof IPackageFragment) {
				IPackageFragment packageFragment = (IPackageFragment) parentElement;

				List<Object> childs = new ArrayList<>();

				PackageCache cache = new PackageCache((IPackageFragmentRoot) packageFragment.getParent());
				try {
					for (IPackageFragment singlePackage : cache.getDirectChildren(packageFragment)) {
						IPackageFragment modifiedPackage = getFolded(cache, singlePackage);

						childs.add(modifiedPackage);
					}

				} catch (JavaModelException e) {
					TestgeneratorActivator.log(e);
				}

				return childs.toArray();
			}

			return null;
		}

		@Override
		public Object getParent(Object element) {
			if (element instanceof IJavaElement) {
				return ((IJavaElement) element).getParent();
			}

			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			Object[] children = getChildren(element);

			return children != null && children.length > 0;
		}

		private IPackageFragment getFolded(PackageCache cache, IPackageFragment singlePackage)
				throws JavaModelException {
			while (isEmpty(singlePackage) && cache.hasSingleChild(singlePackage)) {
				singlePackage = cache.getSingleChild(singlePackage);
			}

			return singlePackage;
		}

		private boolean isEmpty(IPackageFragment fragment) throws JavaModelException {
			return !fragment.containsJavaResources() && fragment.getNonJavaResources().length == 0;
		}

	}

	private void updatePackageViewer() {
		String projectName = this.project.getText();

		IProject project = projects.get(projectName);

		selectedProject.setProject(project);

		try {
			if (project.hasNature(IMavenConstants.NATURE_ID)) {
				updatePackageViewerForMavenProject(project);
			}
		} catch (CoreException e) {
			TestgeneratorActivator.log(e);
		}
	}

	private void updatePackageViewerForMavenProject(IProject project) throws CoreException {
		IPackageFragmentRoot packageFragment = getFragmentRootForMavenProject(project);

		IMavenProjectRegistry projectRegistry = MavenPlugin.getMavenProjectRegistry();
		IMavenProjectFacade mavenProject = projectRegistry.getProject(project);

		selectedProject.setFragmentRoot(packageFragment);
		selectedProject.setOutputLocation(project.getFolder(mavenProject.getOutputLocation().removeFirstSegments(1))
				.getLocation().removeLastSegments(1));

		packageViewer.setInput(packageFragment);
		packageViewer.expandAll();

		IClasspathManager classpathManager = MavenJdtPlugin.getDefault().getBuildpathManager();

		IClasspathEntry[] dependencies = classpathManager.getClasspath(project, IClasspathManager.CLASSPATH_RUNTIME,
				false, new NullProgressMonitor());

		selectedProject.getSelectedPackages().clear();

		getDependenciesOfProject(project, dependencies);

		dependencyViewer.setInput(selectedProject.getDependencies());
	}

	private void getDependenciesOfProject(IProject project, IClasspathEntry[] dependencies) throws CoreException {
		IJavaProject javaProject = JavaCore.create(project);

		for (IClasspathEntry cp : dependencies) {

			boolean isDependencyProject = IClasspathEntry.CPE_PROJECT == cp.getEntryKind();

			Dependency dependency = new Dependency(isDependencyProject);

			IPackageFragmentRoot dependencyPackageRoot = null;

			if (isDependencyProject) {
				IProject childProject = (IProject) ResourcesPlugin.getWorkspace().getRoot().findMember(cp.getPath());
				dependencyPackageRoot = getFragmentRootForMavenProject(childProject);

			} else {
				dependencyPackageRoot = javaProject.findPackageFragmentRoot(cp.getPath());
			}

			if (!dependencyPackageRoot.isOpen()) {
				dependencyPackageRoot.open(new NullProgressMonitor());
			}

			dependency.setPackageFragmentRoot(dependencyPackageRoot);

			Optional<IPackageFragment> basePackageOptional = Arrays.stream(dependencyPackageRoot.getChildren())
					.map(element -> (IPackageFragment) element).filter(pkg -> {
						try {
							return !pkg.isDefaultPackage() && pkg.hasSubpackages();
						} catch (JavaModelException e) {
							TestgeneratorActivator.log(e);
							return false;
						}
					})
					.collect(Collectors.minBy((pkg1, pkg2) -> pkg1.getElementName().compareTo(pkg2.getElementName())));

			if (basePackageOptional.isPresent()) {
				IPackageFragment basePackage = basePackageOptional.get();
				PackageCache packageCache = new PackageCache(dependencyPackageRoot);

				while (packageCache.hasSingleChild(basePackage) && basePackage.getOrdinaryClassFiles().length == 0) {
					basePackage = packageCache.getSingleChild(basePackage);
				}

				dependency.addSelectedPackage(basePackage);
			}

			String artifactID = Arrays.stream(cp.getExtraAttributes())
					.filter(ca -> IClasspathManager.ARTIFACT_ID_ATTRIBUTE.equals(ca.getName()))
					.map(IClasspathAttribute::getValue).findAny()
					.orElseThrow(() -> new IllegalArgumentException("artifactId must exist"));

			dependency.setName(artifactID);

			selectedProject.addDependency(dependency);
		}
	}

	private IPackageFragmentRoot getFragmentRootForMavenProject(IProject project) {
		IJavaProject javaProject = JavaCore.create(project);

		IMavenProjectRegistry projectRegistry = MavenPlugin.getMavenProjectRegistry();
		IMavenProjectFacade mavenProject = projectRegistry.getProject(project);

		IPath[] compileSourceLocations = mavenProject.getCompileSourceLocations();

		IPackageFragmentRoot packageFragment = null;

		if (compileSourceLocations.length == 1) {
			packageFragment = javaProject.getPackageFragmentRoot(project.getFolder(compileSourceLocations[0]));

		} else {
			List<IPackageFragmentRoot> fragments = new ArrayList<>();

			for (IPath compileSourceLocation : compileSourceLocations) {
				IPackageFragmentRoot fragment = javaProject
						.getPackageFragmentRoot(project.getFolder(compileSourceLocation));
				fragments.add(fragment);
			}

			packageFragment = selectSourceFragmentRoot(fragments);

			if (packageFragment == null) {
				MessageDialog.openError(getShell(), "Error Selecting source-root",
						"pls select a source-root for this project");
			}

		}

		return packageFragment;
	}

	private IPackageFragmentRoot selectSourceFragmentRoot(List<IPackageFragmentRoot> fragmentRoots) {
		ListDialog list = new ListDialog(getShell());
		list.setLabelProvider(new JavaElementLabelProvider());
		list.setContentProvider(ArrayContentProvider.getInstance());
		list.setTitle("Select Source-Root");
		list.setInput(fragmentRoots);

		if (Window.OK == list.open()) {
			return (IPackageFragmentRoot) list.getResult()[0];
		}

		return null;
	}

	private void dependencyViewerDoubleClick() {
		Dependency firstElement = (Dependency) dependencyViewer.getStructuredSelection().getFirstElement();

		PackageSelectionDialog dialog = new PackageSelectionDialog(getShell(), firstElement);

		if (Window.OK == dialog.open()) {
			dependencyViewer.refresh();
		}
	}

	private void setDependencyValuesInDialog() {
		String name = this.dependency.getName();

		project.add(name);
		project.setText(name);

		packageViewer.setInput(dependency.getPackageFragmentRoot());
		packageViewer.expandAll();

		IStructuredSelection selection = new StructuredSelection(dependency.getSelectedPackages());

		packageViewer.setSelection(selection, true);
	}

	private void setProjectValuesInDialog() {
		String name = this.selectedProject.getProject().getName();

		project.add(name);
		project.setText(name);

		packageViewer.setInput(this.selectedProject.getFragmentRoot());
		packageViewer.expandAll();

		IStructuredSelection selection = new StructuredSelection(this.selectedProject.getSelectedPackages());

		packageViewer.setSelection(selection);

		dependencyViewer.setInput(this.selectedProject.getDependencies());
	}

	@Override
	protected void okPressed() {
		ITreeSelection structuredSelection = packageViewer.getStructuredSelection();

		@SuppressWarnings("unchecked")
		List<IPackageFragment> list = (List<IPackageFragment>) structuredSelection.toList().stream()
				.filter(el -> el instanceof IPackageFragment).collect(Collectors.toList());

		List<IPackageFragment> selectedPackages = DialogType.DEPENDENCY == dialogType ? dependency.getSelectedPackages()
				: selectedProject.getSelectedPackages();

		selectedPackages.clear();
		selectedPackages.addAll(list);

		setResult(Arrays.asList(selectedProject));

		super.okPressed();
	}
}

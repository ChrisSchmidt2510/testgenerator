package org.testgen.testgenerator.ui.plugin.dialogs;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.core.IConfigurationElementConstants;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.internal.ui.JavaUIMessages;
import org.eclipse.jdt.internal.ui.dialogs.OpenTypeSelectionDialog;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;
import org.testgen.testgenerator.ui.plugin.TestgeneratorActivator;
import org.testgen.testgenerator.ui.plugin.dialogs.model.BlProject;
import org.testgen.testgenerator.ui.plugin.dialogs.model.Dependency;
import org.testgen.testgenerator.ui.plugin.dialogs.model.Model;
import org.testgen.testgenerator.ui.plugin.helper.Descriptor;
import org.testgen.testgenerator.ui.plugin.helper.JDTUtil;
import org.testgen.testgenerator.ui.plugin.helper.Utils;
import org.testgen.testgenerator.ui.plugin.preference.TestgeneratorPreferencePage;

@SuppressWarnings("restriction")
public class TestgeneratorConfigurationController {

	private static final String JAVAAGENT = "-javaagent:";

	private static final String BOOT_CLASSPATH = "-Xbootclasspath/p:";
	private static final String ARG_CLASS_NAME = "ClassName";
	private static final String ARG_METHOD_NAME = "MethodName";
	private static final String ARG_METHOD_DESC = "MethodDescriptor";
	private static final String ARG_BL_PACKAGE = "BlPackage";
	private static final String ARG_BL_PACKGE_JAR_DEST = "BlPackageJarDestination";
	private static final String ARG_TRACE_READ_FIELD_ACCESS = "TraceReadFieldAccess";
	private static final String ARG_COSTUM_TESTGENERATOR_CLASS = "CostumTestgeneratorClass";

	private static final String ARG_PATH_TO_TESTCLASS = "PathToTestclass";

	private static final String GENERALL_ARG_SEPARATUR = "-";
	private static final String LIST_ARG_SEPARATUR = ",";
	private static final String EQUAL = "=";

	private static final String TEST_CLASS_GENERATION = "de.nvg.testgenerator.generation.TestClassGeneration";

	private final Shell activeShell;

	private final Model model = new Model();

	private TestgeneratorConfigurationDialog dialog;

	private IType selectedType;
	private IType customTestgeneratorType;
	private Map<String, IMethod> methods = new HashMap<>();

	private Set<ILaunchConfiguration> javaLaunchConfigs = new HashSet<>();
	private Set<IServer> servers = new HashSet<>();

	public TestgeneratorConfigurationController(Shell activeShell) {
		this.activeShell = activeShell;
	}

	public Shell getActiveShell() {
		return activeShell;
	}

	public void createDialog(IMethod selectedMethod) {
		selectedType = selectedMethod.getDeclaringType();

		if (getTestFragmentRoot(selectedType) != null) {
			try {
				updateModel(selectedType.getMethods(), selectedMethod);
			} catch (JavaModelException e) {
				TestgeneratorActivator.log(e);
			}

			dialog = new TestgeneratorConfigurationDialog(activeShell, //
					this, model);
			dialog.create();
			dialog.updateComponents();
			dialog.open();
		} else {
			MessageDialog.openError(activeShell, "Error initalizing Testgenerator-Plugin",
					"Pls add a Test sourcefolder to project " + selectedType.getJavaProject().getElementName());
		}
	}

	public void updateTestclassType() {
		IType testClassType = openTypeSelection();

		if (JDTUtil.validateType(testClassType)) {

			selectedType = testClassType;
			methods.clear();

			try {
				updateModel(testClassType.getMethods(), null);
			} catch (JavaModelException e) {
				TestgeneratorActivator.log(e);
			}

			dialog.updateComponents();
		} else {
			MessageDialog.openError(activeShell, "Cant use selected type",
					"The type you selected no Test can be generated");
		}
	}

	public void updateCustomTestgeneratorClass() {
		IType costumTestgeneratorType = openTypeSelection();

		boolean correctType = false;

		try {
			for (String interfaceType : costumTestgeneratorType.getSuperInterfaceTypeSignatures()) {
				String qualifiedInterfaceType = JavaModelUtil.getResolvedTypeName(interfaceType,
						costumTestgeneratorType);

				if (TEST_CLASS_GENERATION.equals(qualifiedInterfaceType)) {
					correctType = true;
				}
			}

			if (correctType) {
				this.customTestgeneratorType = costumTestgeneratorType;
				model.setCostumTestgeneratorClassName(costumTestgeneratorType.getTypeQualifiedName());
				dialog.updateComponents();

			} else {
				MessageDialog.openError(activeShell, "error while selecting CostumTestgeneratorClass",
						"selected class has to implement " + TEST_CLASS_GENERATION);
			}
		} catch (JavaModelException e) {
			TestgeneratorActivator.log(e);
			MessageDialog.openError(activeShell, "error while selecting CostumTestgeneratorClass",
					"Message: " + e.getMessage());
		}
	}

	private IType openTypeSelection() {
		SelectionDialog dialog = new OpenTypeSelectionDialog(activeShell, false,
				PlatformUI.getWorkbench().getProgressService(), null, IJavaSearchConstants.TYPE);
		dialog.setTitle(JavaUIMessages.OpenTypeAction_dialogTitle);
		dialog.setMessage(JavaUIMessages.OpenTypeAction_dialogMessage);

		if (IDialogConstants.OK_ID == dialog.open()) {

			Object[] types = dialog.getResult();

			if (types != null && types.length == 1) {
				return (IType) types[0];
			}
		}

		return null;
	}

	public void selectLaunchConfiguration() {

		BusyIndicator.showWhile(Display.getCurrent(), () -> {
			ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
			try {
				ILaunchConfiguration[] launchConfigs = launchManager.getLaunchConfigurations();

				for (ILaunchConfiguration launchConfig : launchConfigs) {
					if (IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION
							.equals(launchConfig.getType().getAttribute(IConfigurationElementConstants.ID))) {
						this.javaLaunchConfigs.add(launchConfig);
					}
				}
			} catch (CoreException e) {
				TestgeneratorActivator.log(e);
			}

			this.servers.addAll(Arrays.asList(ServerCore.getServers()));

		});

		LaunchConfigurationDialog listDialog = new LaunchConfigurationDialog(activeShell);
		listDialog.setInputApplications(javaLaunchConfigs);
		listDialog.setInputServers(servers);

		if (IDialogConstants.OK_ID == listDialog.open()) {
			Object[] result = listDialog.getResult();

			if (result != null && result.length > 0) {
				ILaunchConfiguration launchConfig = (ILaunchConfiguration) result[0];

				model.setLaunchConfiguration(launchConfig);
				dialog.updateComponents();
			}
		}

	}

	public void addProject() {
		PackageSelectionDialog selection = new PackageSelectionDialog(activeShell);
		if (Window.OK == selection.open()) {
			BlProject project = (BlProject) selection.getResult()[0];

			model.addProject(project);

			dialog.updateComponents();
		}

	}

	public void updateProject(BlProject project) {
		PackageSelectionDialog selection = new PackageSelectionDialog(activeShell, project);

		if (Window.OK == selection.open()) {
			dialog.updateComponents();
		}
	}

	public boolean addToLaunchConfiguraton() {
		try {
			String agentArgument = generateAgentArgumentList();
			String bootstrapArgument = generateTestgeneratorBootstrap();

			ILaunchConfiguration launchConfig = model.getLaunchConfiguration();

			String arguments = launchConfig.getAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS,
					(String) null);

			ILaunchConfigurationWorkingCopy copy = launchConfig.getWorkingCopy();

			if (arguments.contains(JAVAAGENT) || arguments.contains(BOOT_CLASSPATH)) {

				String message = null;

				String[] data = null;

				if (arguments.contains(JAVAAGENT) && arguments.contains(BOOT_CLASSPATH)) {
					message = "The selected LaunchConfiguration already contains a " + JAVAAGENT + " and "
							+ BOOT_CLASSPATH
							+ " argument. Because it cant be garanteed that the arguments are overriden correctly your Testgeneratorarguments are copied to clipboard";

					data = new String[] { agentArgument + System.lineSeparator() + bootstrapArgument };
				} else if (arguments.contains(JAVAAGENT)) {
					message = "The selected LaunchConfiguration already contains a " + JAVAAGENT
							+ " argument. Because it cant be garanteed that the argument are overriden correctly your Testgeneratorargument is copied to clipboard";

					data = new String[] { agentArgument };
				} else {
					message = "The selected LaunchConfiguration already contains a " + BOOT_CLASSPATH
							+ " argument. Because it cant be garanteed that the argument are overriden correctly your Testgeneratorargument is copied to clipboard";

					data = new String[] { bootstrapArgument };
				}

				MessageDialog.openInformation(activeShell, "Adding Argument to LaunchConfiguration failed", message);

				Clipboard clipboard = new Clipboard(activeShell.getDisplay());
				clipboard.setContents(data, new Transfer[] { TextTransfer.getInstance() });

			} else {
				addNewArgumentsToLaunchConfigCopy(arguments, agentArgument, bootstrapArgument, copy);
			}

		} catch (IllegalArgumentException e) {
			MessageDialog.openError(activeShell, "error while adding Testgeneratur arguments to Launch Config",
					e.getMessage());
			return false;
		} catch (CoreException e) {
			MessageDialog.openError(activeShell, "error while adding Testgeneratur arguments to Launch Config",
					"can`t open the VM-Arguments from Launch Configuration");
			TestgeneratorActivator.log(e);
			return false;
		}

		return true;
	}

	private void addNewArgumentsToLaunchConfigCopy(String oldArgument, String agentArgument, String bootstrapArgument,
			ILaunchConfigurationWorkingCopy copy) throws CoreException {
		StringBuilder builder = new StringBuilder();

		if (oldArgument != null) {
			builder.append(oldArgument);
		}
		builder.append(System.lineSeparator());
		builder.append(agentArgument);

		if (model.useTestgeneratorBootstrap()) {
			builder.append(System.lineSeparator());
			builder.append(bootstrapArgument);
		}
		copy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, builder.toString());
		copy.doSave();
	}

	private void updateModel(IMethod[] methods, IMethod selectedMethod) {
		this.methods.clear();
		for (IMethod method : methods) {
			if (JDTUtil.validateMethod(method)) {
				this.methods.put(createMethodString(method), method);
			}
		}

		model.setMethods(this.methods.keySet());

		if (selectedMethod != null) {
			model.setSelectedMethod(createMethodString(selectedMethod));
		}

		model.setClassName(selectedType.getTypeQualifiedName());

	}

	private String generateAgentArgumentList() {
		StringBuilder argument = new StringBuilder();

		try {
			argument.append(JAVAAGENT);

			IPreferenceStore store = TestgeneratorActivator.getDefault().getPreferenceStore();

			String agentJar = null;

			if (Model.AGENT_TYPE_TESTGENERATOR.equals(model.getAgentType())) {
				agentJar = store.getString(TestgeneratorPreferencePage.STORE_ARG_TESTGENERATOR_JAR);
			} else if (Model.AGENT_TYPE_TESTGENERATOR_FULL.equals(model.getAgentType())) {
				agentJar = store.getString(TestgeneratorPreferencePage.STORE_ARG_TESTGENERATOR_FULL_JAR);
			}

			if (Utils.checkStringFilled(agentJar)) {
				argument.append(agentJar + EQUAL);
			} else {
				throw new IllegalArgumentException(
						"no JAR found for " + model.getAgentType() + ". Pls add the jar at the Preferences");
			}

			argument.append(GENERALL_ARG_SEPARATUR + ARG_CLASS_NAME + EQUAL
					+ selectedType.getFullyQualifiedName().replace('.', '/'));

			String selectedMethodStr = model.getSelectedMethod();

			IMethod method = methods.get(selectedMethodStr);

			argument.append(GENERALL_ARG_SEPARATUR + ARG_METHOD_NAME + EQUAL + method.getElementName());

			Descriptor descriptor = Descriptor.getInstance(selectedType.getJavaProject());

			argument.append(GENERALL_ARG_SEPARATUR + ARG_METHOD_DESC + EQUAL + "(");

			for (int i = 0; i < method.getParameterTypes().length; i++) {
				String param = method.getParameterTypes()[i];
				argument.append(descriptor.getJvmFullQualifiedName(param, method.getDeclaringType()));
			}

			argument.append(")");
			argument.append(descriptor.getJvmFullQualifiedName(method.getReturnType(), method.getDeclaringType()));

			if (model.isTraceReadFieldAccess()) {
				argument.append(GENERALL_ARG_SEPARATUR + ARG_TRACE_READ_FIELD_ACCESS);
			}

			if (!model.getProjects().isEmpty()) {
				String packageArguments = generateBlPackageArgument();

				argument.append(packageArguments);
			}

			if (model.getCostumTestgeneratorClassName() != null) {
				argument.append(
						GENERALL_ARG_SEPARATUR + ARG_COSTUM_TESTGENERATOR_CLASS + EQUAL + customTestgeneratorType);
			}

			argument.append(generatePathToTestclassArgument());

		} catch (JavaModelException e) {
			TestgeneratorActivator.log(e);
		}

		return argument.toString();
	}

	private String generateBlPackageArgument() {
		Set<String> packages = new HashSet<>();
		Set<String> jarDest = new HashSet<>();

		StringBuilder argument = new StringBuilder();

		for (BlProject blProject : model.getProjects()) {
			if (!blProject.getSelectedPackages().isEmpty()) {
				jarDest.add(blProject.getOutputLocation().toPortableString());

				for (Dependency dependency : blProject.getDependencies()) {
					jarDest.add(dependency.getPackageFragmentPath());

					for (IPackageFragment pkg : dependency.getSelectedPackages()) {
						packages.add(pkg.getElementName().replace('.', '/'));
					}
				}

				for (IPackageFragment pkg : blProject.getSelectedPackages()) {
					packages.add(pkg.getElementName().replace('.', '/'));
				}
			}
		}

		argument.append(GENERALL_ARG_SEPARATUR + ARG_BL_PACKAGE + EQUAL + String.join(LIST_ARG_SEPARATUR, packages));
		argument.append(
				GENERALL_ARG_SEPARATUR + ARG_BL_PACKGE_JAR_DEST + EQUAL + String.join(LIST_ARG_SEPARATUR, jarDest));

		return argument.toString();

	}

	private String generatePathToTestclassArgument() {
		if (JDTUtil.validateType(selectedType)) {
			IPackageFragmentRoot testFragmentRoot = getTestFragmentRoot(selectedType);

			IPath testClassLocation = selectedType.getJavaProject().getProject().getRawLocation();
			testClassLocation = testClassLocation.append(testFragmentRoot.getPath().removeFirstSegments(1));

			boolean testClassExists = false;

			try {
				for (IJavaElement element : testFragmentRoot.getChildren()) {
					IPackageFragment packageFragment = (IPackageFragment) element;

					Optional<ICompilationUnit> testClassOptional = Arrays.stream(packageFragment.getCompilationUnits())
							.filter(cu -> cu.getElementName().contains(selectedType.getElementName())).findAny();

					if (testClassOptional.isPresent()) {
						testClassExists = true;
						testClassLocation = testClassLocation
								.append(packageFragment.getElementName().replace('.', '/'));
						testClassLocation = testClassLocation.append(testClassOptional.get().getElementName());
						break;
					}
				}

			} catch (JavaModelException e) {
				TestgeneratorActivator.log(e);
			}

			if (!testClassExists) {
				testClassLocation.append(selectedType.getPackageFragment().getElementName().replace('.', '/'));
				testClassLocation.append(selectedType.getFullyQualifiedName('/') + "Test.java");
			}

			return GENERALL_ARG_SEPARATUR + ARG_PATH_TO_TESTCLASS + EQUAL + testClassLocation.toPortableString();
		}

		return "";
	}

	private IPackageFragmentRoot getTestFragmentRoot(IType selectedType) {
		IJavaProject javaProject = selectedType.getJavaProject();

		if (javaProject != null && javaProject.exists()) {
			try {
				for (IPackageFragmentRoot root : javaProject.getPackageFragmentRoots()) {

					if (root.getKind() == IPackageFragmentRoot.K_SOURCE && root.getResolvedClasspathEntry().isTest()) {
						return root;
					}

				}
			} catch (JavaModelException e) {
				TestgeneratorActivator.log(e);
			}
		}

		return null;
	}

	private String generateTestgeneratorBootstrap() {
		if (model.useTestgeneratorBootstrap()) {
			IPreferenceStore store = TestgeneratorActivator.getDefault().getPreferenceStore();
			String bootstrapJar = store.getString(TestgeneratorPreferencePage.STORE_ARG_TESTGENERATOR_BOOTSTRAP_JAR);

			if (Utils.checkStringFilled(bootstrapJar)) {
				return BOOT_CLASSPATH + bootstrapJar;
			} else {
				throw new IllegalArgumentException(
						"No testgenerator-bootstrap.jar found. Pls add the jar at the Preferences");
			}
		}

		return null;
	}

	private String createMethodString(IMethod method) {
		String methodName = method.getElementName();

		String[] parameters = new String[method.getParameterTypes().length];

		for (int i = 0; i < method.getParameterTypes().length; i++) {
			String param = method.getParameterTypes()[i];
			parameters[i] = Signature.getSignatureSimpleName(param);
		}

		methodName += "(" + String.join(", ", parameters) + ")";

		return methodName;
	}

}

package org.testgen.testgenerator.ui.plugin.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.internal.ui.JavaUIMessages;
import org.eclipse.jdt.internal.ui.dialogs.OpenTypeSelectionDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.testgen.testgenerator.ui.plugin.helper.Descriptor;

@SuppressWarnings("restriction")
public class TestgeneratorConfigurationController {

	private static final String ARG_CLASS_NAME = "ClassName";
	private static final String ARG_METHOD_NAME = "MethodName";
	private static final String ARG_METHOD_DESC = "MethodDescriptor";
	private static final String ARG_BL_PACKAGE = "BlPackage";
	private static final String ARG_BL_PACKGE_JAR_DEST = "BlPackageJarDestination";
	private static final String ARG_TRACE_READ_FIELD_ACCESS = "TraceReadFieldAccess";
	private static final String ARG_PRINT_CLASSFILES_DIR = "PrintClassFilesDir";
	private static final String ARG_COSTUM_TESTGENERATOR_CLASS = "CostumTestgeneratorClass";

	private static final String GENERALL_ARG_SEPARATUR = "-";
	private static final String LIST_ARG_SEPARATUR = ",";
	private static final String EQUAL = "=";

	private static final String TEST_CLASS_GENERATION = "de.nvg.testgenerator.generation.TestClassGeneration";

	private final Shell activeShell;

	private final Model model = new Model();

	private TestgeneratorConfigurationDialog dialog;

	private IType selectedSourceType;
	private Map<IMethod, String> methods = new HashMap<>();

	public TestgeneratorConfigurationController(Shell activeShell) {
		this.activeShell = activeShell;
	}

	public void createDialog(IMethod selectedMethod) {
		selectedSourceType = selectedMethod.getDeclaringType();

		try {
			updateModel(selectedSourceType.getMethods(), selectedMethod);
		} catch (JavaModelException e) {
		}

		dialog = new TestgeneratorConfigurationDialog(activeShell, //
				this, model);
		dialog.create();
		dialog.updateComponents();
		dialog.open();
	}

	public void updateTestclassType() {
		IType testClassType = openTypeSelection();

		selectedSourceType = testClassType;
		methods.clear();

		try {
			updateModel(testClassType.getMethods(), null);
		} catch (JavaModelException e) {
		}

		dialog.updateComponents();
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
				model.setCostumTestgeneratorClassName(costumTestgeneratorType.getFullyQualifiedName());
				model.setArgumentList(generateArgumentList());
				dialog.updateComponents();

			} else {
				MessageDialog.openError(activeShell, "error while selecting CostumTestgeneratorClass",
						"selected class has to implement " + TEST_CLASS_GENERATION);
			}
		} catch (JavaModelException e) {
		}
	}

	private IType openTypeSelection() {
		SelectionDialog dialog = new OpenTypeSelectionDialog(activeShell, true,
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

	private void updateModel(IMethod[] methods, IMethod selectedMethod) {
		this.methods.clear();
		for (IMethod method : methods) {
			this.methods.put(method, createMethodString(method));
		}

		List<String> methodStrings = new ArrayList<>(this.methods.values());
		model.setMethods(methodStrings);

		model.setSelectedMethodIndex(
				selectedMethod != null ? methodStrings.indexOf(createMethodString(selectedMethod)) : 0);
		model.setClassName(selectedSourceType.getFullyQualifiedName());
		model.setArgumentList(generateArgumentList());

	}

	public String generateArgumentList() {
		StringBuilder argument = new StringBuilder();
		try {
			argument.append(GENERALL_ARG_SEPARATUR + ARG_CLASS_NAME + EQUAL + model.getClassName().replace(".", "/")
					+ System.lineSeparator());

			String selectedMethodStr = model.getMethods().get(model.getSelectedMethodIndex());

			Entry<IMethod, String> selectedMethod = methods.entrySet().stream()
					.filter(method -> method.getValue().equals(selectedMethodStr)).findAny()
					.orElseThrow(() -> new RuntimeException("No matching Method found"));

			IMethod method = selectedMethod.getKey();

			argument.append(GENERALL_ARG_SEPARATUR + ARG_METHOD_NAME + EQUAL + method.getElementName()
					+ System.lineSeparator());

			Descriptor descriptor = Descriptor.getInstance(selectedSourceType.getJavaProject());

			argument.append(GENERALL_ARG_SEPARATUR + ARG_METHOD_DESC + EQUAL + "(");

			for (int i = 0; i < method.getParameterTypes().length; i++) {
				String param = method.getParameterTypes()[i];
				argument.append(descriptor.getJvmFullQualifiedName(param, method.getDeclaringType()));
			}

			argument.append(")");
			argument.append(descriptor.getJvmFullQualifiedName(method.getReturnType(), method.getDeclaringType()));
			argument.append(System.lineSeparator());
			if (model.isTraceReadFieldAccess()) {
				argument.append(GENERALL_ARG_SEPARATUR + ARG_TRACE_READ_FIELD_ACCESS);
				argument.append(System.lineSeparator());
			}

			if (!model.getBlPackages().isEmpty()) {
				List<String> modifiedBlPackages = model.getBlPackages().stream().map(pack -> pack.replace(".", "/"))
						.collect(Collectors.toList());
				argument.append(GENERALL_ARG_SEPARATUR + ARG_BL_PACKAGE + EQUAL
						+ String.join(LIST_ARG_SEPARATUR, modifiedBlPackages));
				argument.append(System.lineSeparator());
			}

			if (!model.getBlPackageJarDest().isEmpty()) {
				argument.append(GENERALL_ARG_SEPARATUR + ARG_BL_PACKGE_JAR_DEST + EQUAL
						+ String.join(LIST_ARG_SEPARATUR, model.getBlPackageJarDest()));
				argument.append(System.lineSeparator());
			}

			if (model.getCostumTestgeneratorClassName() != null) {
				argument.append(GENERALL_ARG_SEPARATUR + ARG_COSTUM_TESTGENERATOR_CLASS + EQUAL
						+ model.getCostumTestgeneratorClassName());
				argument.append(System.lineSeparator());
			}

			if (model.getPrintClassDirectory() != null) {
				argument.append(
						GENERALL_ARG_SEPARATUR + ARG_PRINT_CLASSFILES_DIR + EQUAL + model.getPrintClassDirectory());
				argument.append(System.lineSeparator());
			}

		} catch (JavaModelException e) {
			e.printStackTrace();
		}

		return argument.toString();
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

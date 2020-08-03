package org.testgen.testgenerator.ui.plugin.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.internal.core.SourceType;
import org.eclipse.jdt.internal.ui.JavaUIMessages;
import org.eclipse.jdt.internal.ui.dialogs.OpenTypeSelectionDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;

@SuppressWarnings("restriction")
public class TestgeneratorConfigurationController {

	private final Shell activeShell;

	private final Model model = new Model();

	private TestgeneratorConfigurationDialog testgeneratorDialog;

	private ICompilationUnit compilationUnit;
	private Map<IMethod, String> methods = new HashMap<>();

	public TestgeneratorConfigurationController(Shell activeShell) {
		this.activeShell = activeShell;
	}

	public void createDialog(IMethod selectedMethod) {
		compilationUnit = selectedMethod.getCompilationUnit();

		try {
			for (IType type : compilationUnit.getTypes()) {
				if (type instanceof SourceType) {
					SourceType sourceType = (SourceType) type;
					updateModel(sourceType.getMethods());
				}
			}
		} catch (JavaModelException e) {
		}

		String selectedMethodStr = createMethodString(selectedMethod);
		model.setSelectedMethodIndex(model.getMethods().indexOf(selectedMethodStr));

		testgeneratorDialog = new TestgeneratorConfigurationDialog(activeShell, //
				this, model);
		testgeneratorDialog.create();
		testgeneratorDialog.updateComponents();
		testgeneratorDialog.open();
	}

	public void openTypeSelectionDialog() {
		SelectionDialog dialog = new OpenTypeSelectionDialog(activeShell, true,
				PlatformUI.getWorkbench().getProgressService(), null, IJavaSearchConstants.TYPE);
		dialog.setTitle(JavaUIMessages.OpenTypeAction_dialogTitle);
		dialog.setMessage(JavaUIMessages.OpenTypeAction_dialogMessage);

		if (IDialogConstants.OK_ID == dialog.open()) {

			Object[] types = dialog.getResult();
			if (types == null || types.length == 0)
				return;

			if (types.length == 1) {
				SourceType sourceType = (SourceType) types[0];
				compilationUnit = sourceType.getCompilationUnit();
				methods.clear();
				try {
					updateModel(sourceType.getMethods());
					model.setSelectedMethodIndex(0);
				} catch (JavaModelException e) {
				}

				testgeneratorDialog.updateComponents();
			}
		}
	}

	private void updateModel(IMethod[] methods) {
		this.methods.clear();
		for (IMethod method : methods) {
			this.methods.put(method, createMethodString(method));
		}

		List<String> methodStrings = new ArrayList<>(this.methods.values());

		model.setMethods(methodStrings);

		try {
			model.setClassName((compilationUnit.getPackageDeclarations().length == 1
					? compilationUnit.getPackageDeclarations()[0].getElementName() + "."
					: "") + compilationUnit.getElementName().replace(".java", ""));
		} catch (JavaModelException e) {
		}
	}

	private String createMethodString(IMethod method) {
		String methodName = method.getElementName();

		String[] parameters = new String[method.getParameterTypes().length];

		for (int i = 0; i < method.getParameterTypes().length; i++) {
			String param = method.getParameterTypes()[i];
			parameters[i] = Signature.getSignatureSimpleName(param);
		}

		methodName += "(" + String.join(", ", parameters) + ")";
		try {
			methodName += " " + Signature.getSignatureSimpleName(method.getReturnType());
		} catch (JavaModelException e) {
		}
		return methodName;
	}
}

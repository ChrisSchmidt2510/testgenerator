package org.testgen.testgenerator.ui.plugin.action;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.testgen.testgenerator.ui.plugin.dialogs.TestgeneratorConfigurationController;

@SuppressWarnings("restriction")
public class SelectedMethodActionDelegate implements IObjectActionDelegate {

	private Shell shell = JavaPlugin.getActiveWorkbenchShell();

	@Override
	public void run(IAction action) {
		if (action instanceof SelectedMethodAction) {
			SelectedMethodAction methodAction = (SelectedMethodAction) action;

			IMethod selectedMethod = methodAction.getSelectedMethod();

			TestgeneratorConfigurationController controller = new TestgeneratorConfigurationController(shell);
			controller.createDialog(selectedMethod);

		} else {
			MessageDialog.openInformation(shell, "Fehlerhafte Selection",
					"Es können nur Methoden für die Testgenerator-Configuration verwendet werden");
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

}

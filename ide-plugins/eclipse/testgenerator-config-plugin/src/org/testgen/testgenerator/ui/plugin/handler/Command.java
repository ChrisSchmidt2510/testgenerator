package org.testgen.testgenerator.ui.plugin.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.testgen.testgenerator.ui.plugin.action.SelectedMethodAction;
import org.testgen.testgenerator.ui.plugin.action.SelectedMethodActionDelegate;
import org.testgen.testgenerator.ui.plugin.helper.JDTUtil;

@SuppressWarnings("restriction")
public class Command extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEditorPart editor = HandlerUtil.getActiveEditor(event);

		IMethod method = JDTUtil.getSelectedMethod((CompilationUnitEditor) editor);

		SelectedMethodActionDelegate actionDelegate = new SelectedMethodActionDelegate();
		actionDelegate.run(new SelectedMethodAction(method));

		return null;
	}

}

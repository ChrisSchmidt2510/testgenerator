package org.testgen.testgenerator.ui.plugin.propertytester;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.testgen.testgenerator.ui.plugin.jdt.JDTUtil;

@SuppressWarnings("restriction")
public class MethodPropertyTester extends PropertyTester {

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {

		if ("hasResourceSelection".equals(property) && receiver instanceof CompilationUnitEditor) {
			return JDTUtil.getSelectedMethod((CompilationUnitEditor) receiver) != null;
		}

		return false;
	}

}

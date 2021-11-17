package org.testgen.testgenerator.ui.plugin.propertytester;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.testgen.testgenerator.ui.plugin.helper.JDTUtil;

@SuppressWarnings("restriction")
public class MethodPropertyTester extends PropertyTester {

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {

		if ("hasResourceSelection".equals(property) && receiver instanceof CompilationUnitEditor) {
			IMethod selectedMethod = JDTUtil.getSelectedMethod((CompilationUnitEditor) receiver);

			return JDTUtil.validateType(selectedMethod.getDeclaringType()) && JDTUtil.validateMethod(selectedMethod);
		}

		return false;
	}

}

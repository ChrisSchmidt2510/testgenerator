package org.testgen.testgenerator.ui.plugin.helper;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;
import org.testgen.testgenerator.ui.plugin.TestgeneratorActivator;

@SuppressWarnings("restriction")
public final class JDTUtil {

	private JDTUtil() {
	}

	public static IMethod getSelectedMethod(CompilationUnitEditor editor) {
		Point selectedRange = editor.getViewer().getSelectedRange();

		ITypeRoot typeRoot = EditorUtility.getEditorInputJavaElement((IEditorPart) editor, true);

		try {
			IJavaElement[] elements = typeRoot.codeSelect(selectedRange.x, selectedRange.y);

			return elements.length == 1 && elements[0] instanceof IMethod
					&& ((IMethod) elements[0]).getCompilationUnit().equals(typeRoot)//
							? (IMethod) elements[0]
							: null;
		} catch (JavaModelException e) {
			TestgeneratorActivator.log(e);
			return null;
		}

	}

	public static boolean validateType(IType type) {
		if (!type.isBinary())
			try {

				return (type.isClass() || type.isEnum()) && !Flags.isPrivate(type.getFlags());

			} catch (JavaModelException e) {
				TestgeneratorActivator.log(e);
				return false;
			}

		return false;
	}

	public static boolean validateMethod(IMethod method) {
		if (method != null) {

			boolean isNotTest = false;

			int flags = 0;
			try {
				flags = method.getFlags();

				isNotTest = !JavaModelUtil.getPackageFragmentRoot(method).getResolvedClasspathEntry().isTest();
			} catch (JavaModelException e) {
				TestgeneratorActivator.log(e);
			}

			return isNotTest && !Flags.isPrivate(flags) && !Flags.isProtected(flags);
		}

		return false;
	}

}

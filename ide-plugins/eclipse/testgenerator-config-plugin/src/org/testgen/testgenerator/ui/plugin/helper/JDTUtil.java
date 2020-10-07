package org.testgen.testgenerator.ui.plugin.helper;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;

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
			return null;
		}

	}

}

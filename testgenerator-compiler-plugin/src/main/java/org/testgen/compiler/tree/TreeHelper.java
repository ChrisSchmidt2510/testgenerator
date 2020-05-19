package org.testgen.compiler.tree;

import java.util.Arrays;
import java.util.List;

import org.testgen.compiler.util.CompilerObjectsHolder;
import org.testgen.compiler.util.Utils;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Names;

public final class TreeHelper {

	private TreeHelper() {
	}

	public static JCFieldAccess createType(String type) {
		TreeMaker treeMaker = CompilerObjectsHolder.getInstance().treeMaker;
		Names symbolTable = CompilerObjectsHolder.getInstance().symbolTable;

		List<String> list = Arrays.asList(type.split("\\."));

		JCIdent firstDot = treeMaker.Ident(symbolTable.fromString(list.get(0)));

		return createType(treeMaker, symbolTable, firstDot, list.subList(1, list.size()));
	}

	private static JCFieldAccess createType(TreeMaker treeMaker, Names symbolTable, JCExpression baseType,
			List<String> restOfType) {
		if (restOfType.size() > 1) {
			JCFieldAccess packagePart = treeMaker.Select(baseType, symbolTable.fromString(restOfType.get(0)));
			return createType(treeMaker, symbolTable, packagePart, restOfType.subList(1, restOfType.size()));
		}

		return treeMaker.Select(baseType, symbolTable.fromString(restOfType.get(0)));
	}

	public static <T extends JCTree> T getParentTree(Tree tree) {
		CompilationUnitTree compilationUnit = CompilerObjectsHolder.getInstance().getCompilationUnit();
		return Utils.cast(TreePath.getPath(compilationUnit, tree).getParentPath().getLeaf());
	}

}

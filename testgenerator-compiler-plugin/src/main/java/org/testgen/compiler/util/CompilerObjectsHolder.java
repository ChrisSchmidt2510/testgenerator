package org.testgen.compiler.util;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.Trees;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;

public final class CompilerObjectsHolder {
	private static CompilerObjectsHolder instance;
	public final TreeMaker treeMaker;
	public final Names symbolTable;
	public final Trees trees;
	private CompilationUnitTree cu;

	private CompilerObjectsHolder(Context context, Trees trees) {
		treeMaker = TreeMaker.instance(context);
		symbolTable = Names.instance(context);
		this.trees = trees;
	}

	public static void init(Context context, Trees trees) {
		instance = new CompilerObjectsHolder(context, trees);
	}

	public static CompilerObjectsHolder getInstance() {
		return instance;
	}

	public void setCompilationUnit(CompilationUnitTree cu) {
		this.cu = cu;
	}

	public CompilationUnitTree getCompilationUnit() {
		return cu;
	}

}

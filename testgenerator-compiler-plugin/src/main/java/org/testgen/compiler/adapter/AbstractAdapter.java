package org.testgen.compiler.adapter;

import org.testgen.compiler.classdata.BasicClassData;
import org.testgen.compiler.util.CompilerObjectsHolder;

import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Names;

public abstract class AbstractAdapter<T> implements Adapter<T> {
	protected final TreeMaker treeMaker = CompilerObjectsHolder.getInstance().treeMaker;
	protected final Names symbolTable = CompilerObjectsHolder.getInstance().symbolTable;

	protected final BasicClassData classData;

	public AbstractAdapter(BasicClassData classData) {
		this.classData = classData;
	}

}

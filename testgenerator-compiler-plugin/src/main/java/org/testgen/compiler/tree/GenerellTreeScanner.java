package org.testgen.compiler.tree;

import java.util.ArrayList;
import java.util.List;

import org.testgen.compiler.adapter.impl.ClassAdapter;
import org.testgen.compiler.adapter.impl.EnumAdapter;
import org.testgen.compiler.util.CompilerObjectsHolder;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.TreeScanner;

public class GenerellTreeScanner extends TreeScanner<Void, Void> {
	private String packageName;
	private List<String> imports = new ArrayList<>();

	@Override
	public Void visitCompilationUnit(CompilationUnitTree node, Void p) {
		packageName = node.getPackageName().toString();

		for (ImportTree importTree : node.getImports()) {
			imports.add(importTree.getQualifiedIdentifier().toString());
		}

		CompilerObjectsHolder.getInstance().setCompilationUnit(node);
		return super.visitCompilationUnit(node, p);
	}

	@Override
	public Void visitClass(ClassTree node, Void p) {
		// TODO check if class is innerclass
		if (Kind.CLASS == node.getKind()) {
			ClassAdapter classAdapter = new ClassAdapter(packageName, imports);
			classAdapter.visit(node);
		} else if (Kind.ENUM == node.getKind()) {
			EnumAdapter enumAdapter = new EnumAdapter(packageName, imports);
			enumAdapter.visit(node);
		}

		return super.visitClass(node, p);
	}

}

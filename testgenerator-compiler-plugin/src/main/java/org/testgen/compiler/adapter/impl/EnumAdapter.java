package org.testgen.compiler.adapter.impl;

import java.util.List;

import org.testgen.compiler.adapter.Adapter;
import org.testgen.compiler.classdata.BasicClassData;
import org.testgen.compiler.classdata.ClassData;
import org.testgen.compiler.classdata.InnerClassData;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.Tree.Kind;

public class EnumAdapter implements Adapter<ClassTree> {
	private final List<String> imports;

	private final String packageName;

	private final BasicClassData parentClass;

	public EnumAdapter(String packageName, List<String> imports) {
		this.packageName = packageName;
		this.parentClass = null;
		this.imports = imports;
	}

	public EnumAdapter(BasicClassData parentClass, List<String> imports) {
		this.parentClass = parentClass;
		this.packageName = null;
		this.imports = imports;
	}

	@Override
	public void visit(ClassTree node) {
		System.out.println("EnumTree " + node.getSimpleName());
		BasicClassData classData;

		if (parentClass != null) {
			classData = new InnerClassData(parentClass, node.getSimpleName().toString(), true);
			parentClass.addInnerClass((InnerClassData) classData);
		} else {
			classData = new ClassData(packageName, node.getSimpleName().toString(), true);
		}

		ClassAdapter classAdapter = new ClassAdapter(classData, imports);
		initiateVisit(node.getMembers(), Kind.CLASS, classAdapter);

		EnumAdapter enumAdapter = new EnumAdapter(classData, imports);
		initiateVisit(node.getMembers(), Kind.ENUM, enumAdapter);

	}

}

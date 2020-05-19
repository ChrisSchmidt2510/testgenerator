package org.testgen.compiler.adapter.impl;

import java.util.List;

import org.testgen.compiler.adapter.Adapter;
import org.testgen.compiler.classdata.BasicClassData;
import org.testgen.compiler.classdata.ClassData;
import org.testgen.compiler.classdata.InnerClassData;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;

public class ClassAdapter implements Adapter<ClassTree> {
	private final String packageName;
	private final List<String> imports;

	private final BasicClassData parentClass;

	public ClassAdapter(String packageName, List<String> imports) {
		this.packageName = packageName;
		this.imports = imports;
		parentClass = null;
	}

	public ClassAdapter(BasicClassData outerClass, List<String> imports) {
		this.parentClass = outerClass;
		this.imports = imports;
		packageName = null;
	}

	@Override
	public void visit(ClassTree node) {
		System.out.println("ClassTree " + node.getSimpleName());
		if (packageName != null) {
			ClassData classData = new ClassData(packageName, node.getSimpleName().toString());
			visitMembers(node.getMembers(), classData);
		} else {
			InnerClassData classData = new InnerClassData(parentClass, node.getSimpleName().toString());
			visitMembers(node.getMembers(), classData);
		}

		System.out.println("afterModification");
		System.out.println(node + "\n");
	}

	private void visitMembers(List<? extends Tree> members, BasicClassData classData) {
		ClassAdapter classAdapter = new ClassAdapter(classData, imports);
		initiateVisit(members, Kind.CLASS, classAdapter);

		EnumAdapter enumAdapter = new EnumAdapter(classData, imports);
		initiateVisit(members, Kind.ENUM, enumAdapter);

		FieldAdapter fieldAdapter = new FieldAdapter(classData, imports);
		initiateVisit(members, Kind.VARIABLE, fieldAdapter);

		MethodAdapter methodAdapter = new MethodAdapter(classData);
		initiateVisit(members, Kind.METHOD, methodAdapter);
	}

}

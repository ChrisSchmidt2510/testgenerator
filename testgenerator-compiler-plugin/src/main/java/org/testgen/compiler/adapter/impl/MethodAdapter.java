package org.testgen.compiler.adapter.impl;

import java.util.Set;
import java.util.stream.Collectors;

import javax.lang.model.element.Name;

import org.testgen.compiler.adapter.AbstractAdapter;
import org.testgen.compiler.classdata.BasicClassData;
import org.testgen.compiler.tree.MethodModifier;

import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;

public class MethodAdapter extends AbstractAdapter<MethodTree> {

	public MethodAdapter(BasicClassData classData) {
		super(classData);
	}

	@Override
	public void visit(MethodTree node) {
		System.out.println("MethodTree " + node.getName());
		System.out.println(node);

		Set<Name> methodParameters = node.getParameters().stream().map(VariableTree::getName)
				.collect(Collectors.toSet());

		MethodModifier methodModifier = new MethodModifier(classData, methodParameters);

		node.getBody().accept(methodModifier, null);
	}

}

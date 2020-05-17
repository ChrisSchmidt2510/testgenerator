package org.testgen.compiler.tree;

import java.util.Set;

import javax.lang.model.element.Name;

import org.testgen.compiler.classdata.BasicClassData;
import org.testgen.compiler.constants.ProxyConstants;
import org.testgen.compiler.util.CompilerObjectsHolder;
import org.testgen.compiler.util.Utils;

import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.MemberReferenceTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.tree.JCTree.JCAssign;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCExpressionStatement;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.tree.JCTree.JCReturn;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Names;

public class MethodModifier extends TreeScanner<Void, Void> {
	private final BasicClassData classData;
	private final Set<Name> localVariableNames;
	private final TreeMaker treeMaker = CompilerObjectsHolder.getInstance().treeMaker;
	private final Names symbolTable = CompilerObjectsHolder.getInstance().symbolTable;

	public MethodModifier(BasicClassData classData, Set<Name> localVariableNames) {
		this.classData = classData;
		this.localVariableNames = localVariableNames;
	}

	@Override
	public Void visitVariable(VariableTree node, Void p) {
		localVariableNames.add(node.getName());

		return super.visitVariable(node, p);
	}

	@Override
	public Void visitAssignment(AssignmentTree node, Void p) {

		if (isField(Utils.cast(node.getVariable()))) {
			JCExpressionStatement parent = TreeHelper.getParentTree(node);
			parent.expr = modifyAssignmentTree(Utils.cast(node));
		}

		if (isField(Utils.cast(node.getExpression()))) {

		}

		return null;
	}

	@Override
	public Void visitMemberReference(MemberReferenceTree node, Void p) {
		System.out.println("Member Reference");
		System.out.println(node);
		return super.visitMemberReference(node, p);
	}

	@Override
	public Void visitMethodInvocation(MethodInvocationTree node, Void p) {
		JCMethodInvocation methodInvoke = Utils.cast(node);

		ListBuffer<JCExpression> modifiedArgs = new ListBuffer<>();

		for (JCExpression arg : methodInvoke.args) {
			modifiedArgs.add(isField(arg) ? modifyFieldToProxyGetValue(arg) : arg);
		}
		methodInvoke.args = modifiedArgs.toList();

		if (methodInvoke.meth instanceof JCFieldAccess) {
			JCFieldAccess methodIdent = Utils.cast(methodInvoke.meth);

			if (isField(methodIdent)) {

			} else {

			}
		}

		System.out.println("Method Invocation");
		System.out.println(node);
		return super.visitMethodInvocation(node, p);

		// JCFieldAccess.name check for isField
		// -> //JCFieldAccess.selected is JCFieldAccess -> loopen
	}

	@Override
	public Void visitReturn(ReturnTree node, Void p) {
		JCReturn returnInst = Utils.cast(node);

		if (isField(returnInst.expr)) {

			returnInst.expr = modifyFieldToProxyGetValue(returnInst.expr);
		}

		return super.visitReturn(node, p);
	}

	private boolean isField(JCExpression expression) {
		if (expression instanceof JCFieldAccess) {
			JCFieldAccess variable = Utils.cast(expression);

			if (variable.selected instanceof JCIdent) {
				JCIdent ident = Utils.cast(variable.selected);

				return ident.name.equals(symbolTable._this) && classData.getFields()//
						.stream().anyMatch(field -> //
						field.getName().equals(variable.name));
			}
		} else if (expression instanceof JCIdent) {
			JCIdent ident = Utils.cast(expression);

			return localVariableNames.stream()//
					.noneMatch(var -> ident.name.equals(var)) && classData.getFields().stream().anyMatch(field -> //
			field.getName().equals(ident.name));
		}

		return false;
	}

	/** TODO find a better name */
	private JCExpression modifyAssignmentTree(JCAssign assignment) {
		return treeMaker.Apply(null, treeMaker.Select(assignment.lhs, //
				symbolTable.fromString(ProxyConstants.METHOD_SETVALUE)),
				com.sun.tools.javac.util.List.of(assignment.rhs));
	}

	private JCExpression modifyFieldToProxyGetValue(JCExpression baseType) {
		return treeMaker.Apply(null, treeMaker.Select(baseType, symbolTable.fromString(ProxyConstants.METHOD_GETVALUE)),
				com.sun.tools.javac.util.List.nil());
	}

}

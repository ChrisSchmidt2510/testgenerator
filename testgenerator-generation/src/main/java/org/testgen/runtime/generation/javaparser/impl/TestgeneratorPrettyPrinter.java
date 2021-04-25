package org.testgen.runtime.generation.javaparser.impl;

import static com.github.javaparser.utils.Utils.normalizeEolInTextBlock;

import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.stmt.EmptyStmt;
import com.github.javaparser.printer.PrettyPrintVisitor;
import com.github.javaparser.printer.PrettyPrinterConfiguration;

public class TestgeneratorPrettyPrinter extends PrettyPrintVisitor {

	public TestgeneratorPrettyPrinter(PrettyPrinterConfiguration prettyPrinterConfiguration) {
		super(prettyPrinterConfiguration);
	}

	@Override
	public void visit(EmptyStmt n, Void arg) {

		if (n.getComment().filter(com -> com.isLineComment()).isPresent()) {
			LineComment lineComment = n.getComment().get().asLineComment();

			if (configuration.isIgnoreComments()) {
				return;
			}
			printer.print("// ").print(normalizeEolInTextBlock(lineComment.getContent(), "").trim());
		} else {
			printComment(n.getComment(), arg);
		}
	}

}

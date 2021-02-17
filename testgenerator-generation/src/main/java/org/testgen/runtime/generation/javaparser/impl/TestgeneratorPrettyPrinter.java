package org.testgen.runtime.generation.javaparser.impl;

import com.github.javaparser.ast.stmt.EmptyStmt;
import com.github.javaparser.printer.PrettyPrintVisitor;
import com.github.javaparser.printer.PrettyPrinterConfiguration;

public class TestgeneratorPrettyPrinter extends PrettyPrintVisitor {

	public TestgeneratorPrettyPrinter(PrettyPrinterConfiguration prettyPrinterConfiguration) {
		super(prettyPrinterConfiguration);
	}

	@Override
	public void visit(EmptyStmt n, Void arg) {
//		printComment(n.getComment(), arg);
//		printer.println();
	}

}

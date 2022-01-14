package org.testgen.runtime.generation.javaparser.impl;

import static com.github.javaparser.utils.Utils.normalizeEolInTextBlock;

import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.stmt.EmptyStmt;
import com.github.javaparser.printer.DefaultPrettyPrinterVisitor;
import com.github.javaparser.printer.configuration.DefaultConfigurationOption;
import com.github.javaparser.printer.configuration.DefaultPrinterConfiguration.ConfigOption;
import com.github.javaparser.printer.configuration.PrinterConfiguration;

public class TestgeneratorPrettyPrinter extends DefaultPrettyPrinterVisitor {

	public TestgeneratorPrettyPrinter(PrinterConfiguration configuration) {
		super(configuration);
	}

	@Override
	public void visit(EmptyStmt n, Void arg) {

		if (n.getComment().filter(com -> com.isLineComment()).isPresent()) {
			LineComment lineComment = n.getComment().get().asLineComment();

			if (!configuration.isActivated(new DefaultConfigurationOption(ConfigOption.PRINT_COMMENTS))) {
				return;
			}
			printer.print("// ").print(normalizeEolInTextBlock(lineComment.getContent(), "").trim());
		} else {
			printComment(n.getComment(), arg);
		}
	}

}

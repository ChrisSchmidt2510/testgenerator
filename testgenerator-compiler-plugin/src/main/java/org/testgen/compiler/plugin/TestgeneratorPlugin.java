package org.testgen.compiler.plugin;

import org.testgen.compiler.tree.GenerellTreeScanner;
import org.testgen.compiler.util.CompilerObjectsHolder;

import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskEvent.Kind;
import com.sun.source.util.TaskListener;
import com.sun.source.util.Trees;
import com.sun.tools.javac.api.BasicJavacTask;
import com.sun.tools.javac.util.Context;

public class TestgeneratorPlugin implements Plugin {

	@Override
	public String getName() {
		return "testgenerator-plugin";
	}

	@Override
	public void init(JavacTask task, String... args) {
		Context context = ((BasicJavacTask) task).getContext();

		CompilerObjectsHolder.init(context, Trees.instance(task));

		final GenerellTreeScanner treeScanner = new GenerellTreeScanner();

		task.addTaskListener(new TaskListener() {

			@Override
			public void started(TaskEvent e) {
			}

			@Override
			public void finished(TaskEvent e) {
				if (Kind.PARSE == e.getKind()) {
					e.getCompilationUnit().accept(treeScanner, null);
				}
			}
		});
	}

}

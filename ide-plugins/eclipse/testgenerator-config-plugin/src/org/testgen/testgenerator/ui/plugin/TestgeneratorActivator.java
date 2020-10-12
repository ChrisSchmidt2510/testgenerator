package org.testgen.testgenerator.ui.plugin;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class TestgeneratorActivator extends AbstractUIPlugin {
	// The shared instance.
	private static TestgeneratorActivator plugin;

	/**
	 * The constructor.
	 */
	public TestgeneratorActivator() {
		plugin = this;
	}

	/**
	 * Returns the shared instance.
	 */
	public static TestgeneratorActivator getDefault() {
		return plugin;
	}

	public static void log(Object msg) {
		ILog log = TestgeneratorActivator.getDefault().getLog();
		Status status = new Status(IStatus.ERROR, "TestgeneratorConfigurationUI", IStatus.ERROR, msg + "\n", null);
		log.log(status);
	}

	public static void log(Throwable ex) {
		ILog log = TestgeneratorActivator.getDefault().getLog();
		StringWriter stringWriter = new StringWriter();
		ex.printStackTrace(new PrintWriter(stringWriter));
		String msg = stringWriter.getBuffer().toString();
		Status status = new Status(IStatus.ERROR, "TestgeneratorConfigurationUI", IStatus.ERROR, msg, null);
		log.log(status);
	}

	public void start(BundleContext context) throws Exception {
		log("Testgeneratorplugin started");
	}

}

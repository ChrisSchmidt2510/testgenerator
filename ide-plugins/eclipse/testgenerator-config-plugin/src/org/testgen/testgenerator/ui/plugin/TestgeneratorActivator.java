package org.testgen.testgenerator.ui.plugin;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;

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

	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	static public void log(Object msg) {
		ILog log = TestgeneratorActivator.getDefault().getLog();
		Status status = new Status(IStatus.ERROR, "TestgeneratorConfigurationUI", IStatus.ERROR, msg + "\n", null);
		log.log(status);
	}

	static public void log(Throwable ex) {
		ILog log = TestgeneratorActivator.getDefault().getLog();
		StringWriter stringWriter = new StringWriter();
		ex.printStackTrace(new PrintWriter(stringWriter));
		String msg = stringWriter.getBuffer().toString();
		Status status = new Status(IStatus.ERROR, "TestgeneratorConfigurationUI", IStatus.ERROR, msg, null);
		log.log(status);
	}

	// /**
	// * @see
	// org.eclipse.ui.plugin.AbstractUIPlugin#initializeDefaultPreferences(org.eclipse.jface.preference.IPreferenceStore)
	// */
	// protected void initializeDefaultPreferences(IPreferenceStore store)
	// {
	// super.initializeDefaultPreferences(store);
	// }

	/**
	 * Return the Debug Yes or No
	 * 
	 * @return boolean
	 */
	public boolean isDebug() {
		return false;
	}

	public void sysout(boolean dbg, String str) {
		if (!dbg || (dbg && isDebug())) {
			System.out.println("[TestgeneratorConfigurationUI] " + str);
		}
	}
}

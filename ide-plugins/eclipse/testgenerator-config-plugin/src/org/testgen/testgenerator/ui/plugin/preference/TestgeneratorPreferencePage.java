package org.testgen.testgenerator.ui.plugin.preference;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.testgen.testgenerator.ui.plugin.TestgeneratorActivator;

//Have to implement IWorkbenchPreferencePage
public class TestgeneratorPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	public static final String STORE_ARG_TESTGENERATOR_JAR = "testgenerator-jar";
	public static final String STORE_ARG_TESTGENERATOR_FULL_JAR = "testgenerator-full-jar";

	public static final String STORE_ARG_TESTGENERATOR_BOOTSTRAP_JAR = "testgenerator-bootstrap-jar";

	private Text txtTestgenerator;
	private Text txtTestgeneratorFull;
	private Text txtTestgeneratorBootstrap;

	public TestgeneratorPreferencePage() {
		setPreferenceStore(TestgeneratorActivator.getDefault().getPreferenceStore());

		initDefaults();
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite mainPanel = new Composite(parent, SWT.NONE);

		Shell shell = parent.getShell();

		Label lblTestgeneratorjarPfad = new Label(mainPanel, SWT.NONE);
		lblTestgeneratorjarPfad.setBounds(10, 10, 152, 20);
		lblTestgeneratorjarPfad.setText("testgenerator.jar file");

		txtTestgenerator = new Text(mainPanel, SWT.BORDER);
		txtTestgenerator.setBounds(241, 10, 212, 26);

		Button btnBrowseTestgeneratorJar = new Button(mainPanel, SWT.NONE);
		btnBrowseTestgeneratorJar.setBounds(459, 5, 60, 30);
		btnBrowseTestgeneratorJar.setText("Browse");
		btnBrowseTestgeneratorJar.addListener(SWT.Selection,
				e -> openFileDialog(shell, "testgenerator.jar", txtTestgenerator));

		Label lblTestgeneratorfulljarPfad = new Label(mainPanel, SWT.NONE);
		lblTestgeneratorfulljarPfad.setBounds(10, 50, 175, 20);
		lblTestgeneratorfulljarPfad.setText("testgenerator-full.jar file");

		txtTestgeneratorFull = new Text(mainPanel, SWT.BORDER);
		txtTestgeneratorFull.setBounds(241, 48, 212, 26);

		Button btnBrowseTestgeneratorFullJar = new Button(mainPanel, SWT.NONE);
		btnBrowseTestgeneratorFullJar.setBounds(459, 45, 60, 30);
		btnBrowseTestgeneratorFullJar.setText("Browse");
		btnBrowseTestgeneratorFullJar.addListener(SWT.Selection,
				e -> openFileDialog(shell, "testgenerator-full.jar", txtTestgeneratorFull));

		Label lblTestgeneratorBootstrapJar = new Label(mainPanel, SWT.NONE);
		lblTestgeneratorBootstrapJar.setBounds(10, 90, 216, 20);
		lblTestgeneratorBootstrapJar.setText("testgenerator-bootstrap.jar file");

		txtTestgeneratorBootstrap = new Text(mainPanel, SWT.BORDER);
		txtTestgeneratorBootstrap.setBounds(241, 84, 212, 26);

		Button btnBrowseTestgeneratorBootstrapJar = new Button(mainPanel, SWT.NONE);
		btnBrowseTestgeneratorBootstrapJar.setBounds(459, 85, 60, 30);
		btnBrowseTestgeneratorBootstrapJar.setText("Browse");
		btnBrowseTestgeneratorBootstrapJar.addListener(SWT.Selection,
				e -> openFileDialog(shell, "testgenerator-bootstrap.jar", txtTestgeneratorBootstrap));

		loadStore();

		return mainPanel;
	}

	@Override
	protected void performDefaults() {
		initDefaults();
		setToDefaults();
		loadStore();
	}

	@Override
	public boolean performOk() {
		IPreferenceStore store = getPreferenceStore();

		store.setValue(STORE_ARG_TESTGENERATOR_JAR, txtTestgenerator.getText());
		store.setValue(STORE_ARG_TESTGENERATOR_FULL_JAR, txtTestgeneratorFull.getText());
		store.setValue(STORE_ARG_TESTGENERATOR_BOOTSTRAP_JAR, txtTestgeneratorBootstrap.getText());

		return true;
	}

	private void openFileDialog(Shell shell, String jarName, Text textFieldToShow) {

		FileDialog fileDialog = new FileDialog(shell);
		fileDialog.setText("Select " + jarName);
		fileDialog.setFilterExtensions(new String[] { "*.jar" });
		fileDialog.setFilterNames(new String[] { jarName });

		String selectedJarFile = fileDialog.open();

		if (selectedJarFile != null) {
			textFieldToShow.setText(selectedJarFile);
		}
	}

	private void initDefaults() {
		IPreferenceStore store = getPreferenceStore();

		// set default
		store.setDefault(STORE_ARG_TESTGENERATOR_JAR, "");
		store.setDefault(STORE_ARG_TESTGENERATOR_FULL_JAR, "");
		store.setDefault(STORE_ARG_TESTGENERATOR_BOOTSTRAP_JAR, "");
	}

	private void setToDefaults() {
		IPreferenceStore store = getPreferenceStore();

		store.setToDefault(STORE_ARG_TESTGENERATOR_JAR);
		store.setToDefault(STORE_ARG_TESTGENERATOR_FULL_JAR);
		store.setToDefault(STORE_ARG_TESTGENERATOR_BOOTSTRAP_JAR);
	}

	private void loadStore() {
		IPreferenceStore store = getPreferenceStore();

		txtTestgenerator.setText(store.getString(STORE_ARG_TESTGENERATOR_JAR));
		txtTestgeneratorFull.setText(store.getString(STORE_ARG_TESTGENERATOR_FULL_JAR));
		txtTestgeneratorBootstrap.setText(store.getString(STORE_ARG_TESTGENERATOR_BOOTSTRAP_JAR));
	}

	@Override
	public void init(IWorkbench workbench) {
	}

}

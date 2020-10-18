package org.testgen.testgenerator.ui.plugin.dialogs;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.testgen.testgenerator.ui.plugin.helper.Utils;

public class TestgeneratorConfigurationDialog extends Dialog {
	private Label lblTestclass;
	private Label lblMethods;
	private Label lblBlPackage;
	private Label lblPrintClassFileDir;
	private Label lblCostumTestgeneratorClass;
	private Label lblTraceReadFieldAccess;
	private Label lblBlpackageDestination;

	private Combo methods;

	private List blPackage;
	private List blPackageDest;

	private Text txtClassName;
	private Text txtBlPackage;
	private Text txtBlPackageDest;
	private Text txtCostumTestgenerator;
	private Text txtPrintClassFileDir;

	private Button btnBrowse;
	private Button btnAdd;
	private Button btnRemove;
	private Button btnBrowseDir;
	private Button btnAddPackageDest;
	private Button btnRemovePackageDest;
	private Button btnBrowseCostumTestgenerator;

	private Button checkBoxReadFieldAccess;

	private final TestgeneratorConfigurationController controller;
	private final Model model;
	private Label lblUseTestgeneratorbootstrap;
	private Button checkBoxUseTestgeneratorBootstrap;
	private Label lblLaunchConfiguration;
	private Text txtLaunchConfiguration;
	private Button btnSelect;
	private Label lblNewLabel;

	protected TestgeneratorConfigurationDialog(Shell parentShell, //
			TestgeneratorConfigurationController controller, Model model) {
		super(parentShell);
		this.controller = controller;
		this.model = model;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);

		initDialog(container);

		lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setBounds(637, 156, 70, 20);

		return container;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Testgenerator - Configuration");
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "Copy", true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected void okPressed() {
		updateModel();

		if (checkNeededParameters() && controller.addToLaunchConfiguraton()) {
			super.okPressed();
		}
	}

	private boolean checkNeededParameters() {
		java.util.List<String> neededParameters = new ArrayList<>();

		if (model.getAgentType() == null) {
			neededParameters.add("Agenttype");
		}

		if (model.getClassName() == null) {
			neededParameters.add("Class");
		}

		if (model.getSelectedMethodIndex() < 0) {
			neededParameters.add("Method");
		}

		if (model.getLaunchConfiguration() == null) {
			neededParameters.add("Launch Configuration");
		}

		if (!neededParameters.isEmpty()) {
			MessageDialog.openError(controller.getActiveShell(), "cant create Testgenerator configuration",
					"The following parameters are needed " + String.join(",", neededParameters));
			return false;
		}

		return true;
	}

	private void initDialog(Composite parent) {
		parent.setLayout(null);

		lblMethods = new Label(parent, SWT.NONE);
		lblMethods.setBounds(10, 139, 52, 20);
		lblMethods.setText("Method");

		lblTestclass = new Label(parent, SWT.NONE);
		lblTestclass.setBounds(10, 81, 33, 20);
		lblTestclass.setText("Class");

		txtClassName = new Text(parent, SWT.BORDER);
		txtClassName.setBounds(215, 79, 321, 26);
		txtClassName.setEditable(false);

		btnBrowse = new Button(parent, SWT.PUSH);
		btnBrowse.setBounds(557, 78, 65, 30);
		btnBrowse.setText("Browse");
		btnBrowse.addListener(SWT.Selection, e -> controller.updateTestclassType());

		methods = new Combo(parent, SWT.NONE);
		methods.setBounds(215, 131, 321, 28);

		lblBlPackage = new Label(parent, SWT.NONE);
		lblBlPackage.setBounds(10, 187, 85, 20);
		lblBlPackage.setText("BL-Package");

		txtBlPackage = new Text(parent, SWT.BORDER);
		txtBlPackage.setBounds(215, 181, 321, 26);

		blPackage = new List(parent, SWT.SINGLE | SWT.BORDER);
		blPackage.setBounds(215, 213, 321, 60);
		blPackage.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				btnRemove.setEnabled(true);
			}
		});

		btnAdd = new Button(parent, SWT.NONE);
		btnAdd.setBounds(557, 182, 65, 30);
		btnAdd.setText("Add");
		btnAdd.addListener(SWT.Selection, e -> updateModel());

		btnRemove = new Button(parent, SWT.NONE);
		btnRemove.setBounds(628, 182, 65, 30);
		btnRemove.setText("Remove");
		btnRemove.setEnabled(false);
		btnRemove.addListener(SWT.Selection, e -> removeEntryBlPackage());

		Group grpAgenttype = new Group(parent, SWT.NONE);
		grpAgenttype.setBounds(215, 0, 321, 72);
		grpAgenttype.setText("Agenttype");

		Button radioTestgenerator = new Button(grpAgenttype, SWT.RADIO);
		radioTestgenerator.setSize(124, 20);
		radioTestgenerator.setLocation(10, 22);
		radioTestgenerator.setText("Testgenerator");
		radioTestgenerator.addListener(SWT.Selection, e -> model.setAgentType(Model.AGENT_TYPE_TESTGENERATOR));

		Button radioTestgeneratorFull = new Button(grpAgenttype, SWT.RADIO);
		radioTestgeneratorFull.setSize(144, 20);
		radioTestgeneratorFull.setLocation(10, 42);
		radioTestgeneratorFull.setText("Testgenerator-Full");
		radioTestgeneratorFull.addListener(SWT.Selection, e -> model.setAgentType(Model.AGENT_TYPE_TESTGENERATOR_FULL));

		lblLaunchConfiguration = new Label(parent, SWT.NONE);
		lblLaunchConfiguration.setBounds(10, 285, 155, 20);
		lblLaunchConfiguration.setText("Launch Configuration");

		Group optionalParams = new Group(parent, SWT.NONE);
		optionalParams.setBounds(10, 315, 687, 280);
		optionalParams.setText("optional parameter");

		txtBlPackageDest = new Text(optionalParams, SWT.BORDER);
		txtBlPackageDest.setLocation(204, 17);
		txtBlPackageDest.setSize(322, 26);

		lblBlpackageDestination = new Label(optionalParams, SWT.NONE);
		lblBlpackageDestination.setBounds(10, 19, 156, 20);
		lblBlpackageDestination.setText("BL-Package Destination");

		btnAddPackageDest = new Button(optionalParams, SWT.NONE);
		btnAddPackageDest.setLocation(546, 14);
		btnAddPackageDest.setSize(65, 30);
		btnAddPackageDest.setText("Add");
		btnAddPackageDest.addListener(SWT.Selection, e -> updateModel());

		btnRemovePackageDest = new Button(optionalParams, SWT.NONE);
		btnRemovePackageDest.setLocation(617, 14);
		btnRemovePackageDest.setSize(65, 30);
		btnRemovePackageDest.setText("Remove");
		btnRemovePackageDest.setEnabled(false);
		btnRemovePackageDest.addListener(SWT.Selection, e -> removeEntryBlPackageJarDest());

		blPackageDest = new List(optionalParams, SWT.BORDER);
		blPackageDest.setLocation(204, 49);
		blPackageDest.setSize(321, 60);
		blPackageDest.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				btnRemovePackageDest.setEnabled(true);
			}
		});

		lblTraceReadFieldAccess = new Label(optionalParams, SWT.NONE);
		lblTraceReadFieldAccess.setLocation(10, 115);
		lblTraceReadFieldAccess.setSize(156, 20);
		lblTraceReadFieldAccess.setText("Trace Read Fieldaccess");

		checkBoxReadFieldAccess = new Button(optionalParams, SWT.CHECK);
		checkBoxReadFieldAccess.setLocation(204, 115);
		checkBoxReadFieldAccess.setSize(111, 20);
		checkBoxReadFieldAccess.addListener(SWT.Selection, e -> updateModel());

		lblUseTestgeneratorbootstrap = new Label(optionalParams, SWT.NONE);
		lblUseTestgeneratorbootstrap.setBounds(10, 152, 194, 20);
		lblUseTestgeneratorbootstrap.setText("use Testgenerator-Bootstrap");

		checkBoxUseTestgeneratorBootstrap = new Button(optionalParams, SWT.CHECK);
		checkBoxUseTestgeneratorBootstrap.setGrayed(true);
		checkBoxUseTestgeneratorBootstrap.setBounds(204, 147, 90, 30);
		checkBoxUseTestgeneratorBootstrap.addListener(SWT.Selection, e -> updateModel());

		lblPrintClassFileDir = new Label(optionalParams, SWT.NONE);
		lblPrintClassFileDir.setLocation(10, 191);
		lblPrintClassFileDir.setSize(156, 20);
		lblPrintClassFileDir.setText("Print Classfiles Dir.");

		txtPrintClassFileDir = new Text(optionalParams, SWT.BORDER);
		txtPrintClassFileDir.setBounds(204, 187, 321, 26);
		txtPrintClassFileDir.setEditable(false);

		txtCostumTestgenerator = new Text(optionalParams, SWT.BORDER);
		txtCostumTestgenerator.setBounds(204, 227, 321, 26);
		txtCostumTestgenerator.setEditable(false);

		lblCostumTestgeneratorClass = new Label(optionalParams, SWT.NONE);
		lblCostumTestgeneratorClass.setBounds(10, 229, 178, 20);
		lblCostumTestgeneratorClass.setText("Costum TestgeneratorClass");

		btnBrowseCostumTestgenerator = new Button(optionalParams, SWT.NONE);
		btnBrowseCostumTestgenerator.setBounds(546, 224, 65, 30);
		btnBrowseCostumTestgenerator.setText("Browse");
		btnBrowseCostumTestgenerator.addListener(SWT.Selection, e -> controller.updateCustomTestgeneratorClass());

		btnBrowseDir = new Button(optionalParams, SWT.NONE);
		btnBrowseDir.setBounds(546, 186, 65, 30);
		btnBrowseDir.setText("Browse");
		btnBrowseDir.addListener(SWT.Selection, e -> controller.openDirectoryDialog());

		txtLaunchConfiguration = new Text(parent, SWT.BORDER);
		txtLaunchConfiguration.setBounds(215, 283, 321, 26);
		txtLaunchConfiguration.setEditable(false);

		btnSelect = new Button(parent, SWT.NONE);
		btnSelect.setBounds(557, 280, 65, 30);
		btnSelect.setText("Select");
		btnSelect.addListener(SWT.Selection, e -> controller.selectLaunchConfiguration());
	}

	private void removeEntryBlPackage() {
		int selectionIndex = blPackage.getSelectionIndex();
		String selectedBlPackage = blPackage.getItem(selectionIndex);

		model.getBlPackages().remove(selectedBlPackage);
		blPackage.remove(selectionIndex);
		btnRemove.setEnabled(false);

		updateModel();
	}

	private void removeEntryBlPackageJarDest() {
		int selectionIndex = blPackageDest.getSelectionIndex();
		String selectedBlPackageJar = blPackageDest.getItem(selectionIndex);

		model.getBlPackageJarDest().remove(selectedBlPackageJar);
		blPackageDest.remove(selectionIndex);
		btnRemovePackageDest.setEnabled(false);

		updateModel();
	}

	public void updateComponents() {
		txtClassName.setText(model.getClassName());

		methods.removeAll();
		model.getMethods().forEach(method -> methods.add(method));
		methods.select(model.getSelectedMethodIndex());

		blPackage.removeAll();
		model.getBlPackages().forEach(pack -> blPackage.add(pack));

		blPackageDest.removeAll();
		model.getBlPackageJarDest().forEach(dest -> blPackageDest.add(dest));

		checkBoxReadFieldAccess.setSelection(model.isTraceReadFieldAccess());

//		boot

		if (model.getCostumTestgeneratorClassName() != null) {
			txtCostumTestgenerator.setText(model.getCostumTestgeneratorClassName());
		}

		if (model.getPrintClassDirectory() != null) {
			txtPrintClassFileDir.setText(model.getPrintClassDirectory());
		}

		if (model.getLaunchConfiguration() != null) {
			txtLaunchConfiguration.setText(model.getLaunchConfiguration().getName());
		}

	}

	private void updateModel() {
		model.setTraceReadFieldAccess(checkBoxReadFieldAccess.getSelection());
		model.setUsetestgeneratorBootstrap(checkBoxUseTestgeneratorBootstrap.getSelection());

		String blPackage = txtBlPackage.getText();
		if (Utils.checkStringFilled(blPackage)) {
			if (!model.getBlPackages().contains(blPackage)) {
				model.getBlPackages().add(blPackage);
			}

			txtBlPackage.setText("");
		}

		String blPackageJarDest = txtBlPackageDest.getText();
		if (Utils.checkStringFilled(blPackageJarDest)) {
			if (!model.getBlPackageJarDest().contains(blPackageJarDest)) {
				model.getBlPackageJarDest().add(blPackageJarDest);
			}

			txtBlPackageDest.setText("");
		}

		updateComponents();
	}
}

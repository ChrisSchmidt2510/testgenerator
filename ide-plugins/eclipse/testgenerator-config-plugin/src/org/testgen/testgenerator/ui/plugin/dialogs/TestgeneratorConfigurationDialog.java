package org.testgen.testgenerator.ui.plugin.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class TestgeneratorConfigurationDialog extends Dialog {
	private Label lblTestgenerationConfiguration;
	private Label lblTestclass;
	private Label lblMethods;
	private Label lblBlPackage;
	private Label lblArgumentlist;
	private Label lblPrintClassFileDir;
	private Label lblCostumTestgeneratorClass;
	private Label lblTraceReadFieldAccess;
	private Label lblBlpackageDestination;

	private Combo methods;

	private List blPackage;
	private List blPackageDest;

	private Text txtClassName;
	private Text txtBlPackage;
	private Text txtArgumentList;
	private Text txtBlPackageDest;
	private Text txtCostumTestgenerator;
	private Text txtPrintClassFileDir;

	private Button btnBrowse;
	private Button btnAdd;
	private Button btnRemove;
	private Button btnCopy;
	private Button btnBrowseDir;
	private Button btnAddPackageDest;
	private Button btnRemovePackageDest;
	private Button btnBrowseCostumTestgenerator;

	private Button checkBoxReadFieldAccess;

	private final TestgeneratorConfigurationController controller;
	private final Model model;

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

		return container;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Testgenerator - Configuration");
	}

	private void initDialog(Composite parent) {
		parent.setLayout(null);
		parent.setSize(650, 450);

		lblTestgenerationConfiguration = new Label(parent, SWT.NONE);
		lblTestgenerationConfiguration.setBounds(207, 5, 193, 20);
		lblTestgenerationConfiguration.setText("Testgeneration Configuration");

		lblMethods = new Label(parent, SWT.NONE);
		lblMethods.setBounds(15, 115, 52, 20);
		lblMethods.setText("Method");

		lblTestclass = new Label(parent, SWT.NONE);
		lblTestclass.setBounds(15, 57, 33, 20);
		lblTestclass.setText("Class");

		txtClassName = new Text(parent, SWT.BORDER);
		txtClassName.setBounds(220, 55, 321, 26);
		txtClassName.setEditable(false);

		btnBrowse = new Button(parent, SWT.PUSH);
		btnBrowse.setBounds(562, 54, 60, 30);
		btnBrowse.setText("Browse");
		btnBrowse.addListener(SWT.Selection, e -> controller.updateTestclassType());

		methods = new Combo(parent, SWT.NONE);
		methods.setBounds(220, 107, 321, 28);

		lblBlPackage = new Label(parent, SWT.NONE);
		lblBlPackage.setBounds(15, 163, 85, 20);
		lblBlPackage.setText("BL-Package");

		txtBlPackage = new Text(parent, SWT.BORDER);
		txtBlPackage.setBounds(220, 157, 321, 26);

		blPackage = new List(parent, SWT.SINGLE | SWT.BORDER);
		blPackage.setBounds(220, 189, 321, 60);
		blPackage.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				btnRemove.setEnabled(true);
			}
		});

		btnAdd = new Button(parent, SWT.NONE);
		btnAdd.setBounds(562, 158, 65, 30);
		btnAdd.setText("Add");
		btnAdd.addListener(SWT.Selection, e -> updateModel());

		btnRemove = new Button(parent, SWT.NONE);
		btnRemove.setBounds(633, 158, 65, 30);
		btnRemove.setText("Remove");
		btnRemove.setEnabled(false);
		btnRemove.addListener(SWT.Selection, e -> removeEntryBlPackage());

		lblArgumentlist = new Label(parent, SWT.NONE);
		lblArgumentlist.setBounds(10, 519, 103, 20);
		lblArgumentlist.setText("Argument-List");

		ScrolledComposite sc = new ScrolledComposite(parent, SWT.NONE | SWT.H_SCROLL);
		sc.setBounds(220, 517, 321, 115);

		txtArgumentList = new Text(sc, SWT.WRAP | SWT.BORDER | SWT.MULTI);
		txtArgumentList.setBounds(220, 517, 321, 115);
		txtArgumentList.setEditable(false);

		sc.setContent(txtArgumentList);

		btnCopy = new Button(parent, SWT.NONE);
		btnCopy.setBounds(562, 519, 60, 30);
		btnCopy.setText("Copy");

		txtBlPackageDest = new Text(parent, SWT.BORDER);
		txtBlPackageDest.setBounds(220, 267, 321, 26);

		lblBlpackageDestination = new Label(parent, SWT.NONE);
		lblBlpackageDestination.setBounds(15, 267, 156, 20);
		lblBlpackageDestination.setText("BL-Package Destination");

		blPackageDest = new List(parent, SWT.BORDER);
		blPackageDest.setBounds(220, 299, 321, 60);
		blPackageDest.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				btnRemovePackageDest.setEnabled(true);
			}
		});

		btnAddPackageDest = new Button(parent, SWT.NONE);
		btnAddPackageDest.setText("Add");
		btnAddPackageDest.setBounds(562, 267, 65, 30);
		btnAddPackageDest.addListener(SWT.Selection, e -> updateModel());

		btnRemovePackageDest = new Button(parent, SWT.NONE);
		btnRemovePackageDest.setText("Remove");
		btnRemovePackageDest.setBounds(633, 267, 65, 30);
		btnRemovePackageDest.setEnabled(false);
		btnRemovePackageDest.addListener(SWT.Selection, e -> removeEntryBlPackageJarDest());

		lblTraceReadFieldAccess = new Label(parent, SWT.NONE);
		lblTraceReadFieldAccess.setBounds(15, 378, 156, 20);
		lblTraceReadFieldAccess.setText("Trace Read Fieldaccess");

		checkBoxReadFieldAccess = new Button(parent, SWT.CHECK);
		checkBoxReadFieldAccess.setBounds(220, 378, 111, 20);
		checkBoxReadFieldAccess.addListener(SWT.Selection, e -> updateModel());

		lblPrintClassFileDir = new Label(parent, SWT.NONE);
		lblPrintClassFileDir.setBounds(15, 415, 156, 20);
		lblPrintClassFileDir.setText("Print Classfiles Dir.");

		txtPrintClassFileDir = new Text(parent, SWT.BORDER);
		txtPrintClassFileDir.setBounds(220, 415, 321, 26);

		btnBrowseDir = new Button(parent, SWT.NONE);
		btnBrowseDir.setBounds(562, 411, 60, 30);
		btnBrowseDir.setText("Browse");

		lblCostumTestgeneratorClass = new Label(parent, SWT.NONE);
		lblCostumTestgeneratorClass.setBounds(15, 462, 178, 20);
		lblCostumTestgeneratorClass.setText("Costum TestgeneratorClass");

		txtCostumTestgenerator = new Text(parent, SWT.BORDER);
		txtCostumTestgenerator.setBounds(220, 456, 321, 26);
		txtCostumTestgenerator.setEditable(false);

		btnBrowseCostumTestgenerator = new Button(parent, SWT.NONE);
		btnBrowseCostumTestgenerator.setBounds(562, 452, 60, 30);
		btnBrowseCostumTestgenerator.setText("Browse");
		btnBrowseCostumTestgenerator.addListener(SWT.Selection, e -> controller.updateCustomTestgeneratorClass());
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

		if (model.getCostumTestgeneratorClassName() != null) {
			txtCostumTestgenerator.setText(model.getCostumTestgeneratorClassName());
		}

		txtArgumentList.setText(model.getArgumentList());
	}

	public void updateModel() {
		model.setTraceReadFieldAccess(checkBoxReadFieldAccess.getSelection());

		String blPackage = txtBlPackage.getText();
		if (checkStringFilled(blPackage)) {
			if (!model.getBlPackages().contains(blPackage)) {
				model.getBlPackages().add(blPackage);
			}

			txtBlPackage.setText("");
		}

		String blPackageJarDest = txtBlPackageDest.getText();
		if (checkStringFilled(blPackageJarDest)) {
			if (!model.getBlPackageJarDest().contains(blPackageJarDest)) {
				model.getBlPackageJarDest().add(blPackageJarDest);
			}

			txtBlPackageDest.setText("");
		}

		model.setArgumentList(controller.generateArgumentList());

		updateComponents();
	}

	private static boolean checkStringFilled(String str) {
		return str != null && !str.trim().isEmpty();
	}
}

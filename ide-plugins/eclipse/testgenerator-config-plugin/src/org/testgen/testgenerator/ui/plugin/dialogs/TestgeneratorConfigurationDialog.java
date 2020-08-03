package org.testgen.testgenerator.ui.plugin.dialogs;

import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class TestgeneratorConfigurationDialog extends Dialog {
	private Label lblTestgenerationConfiguration;
	private Label lblTestclass;
	private Label lblMethods;
	private Label lblBlPackage;
	private Label lblBlPackageDest;
	private Label lblArgumentlist;

	private Combo methods;

	private Text txtClassName;
	private Text txtBlPackage;
	private Text txtBlPackageDest;
	private Text txtArgumentList;

	private Table tblBlPackage;
	private TableItem tblItem;

	private Button btnBrowse;
	private Button btnAdd;
	private Button btnCopy;

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

		lblTestclass = new Label(parent, SWT.NONE);
		lblTestclass.setBounds(15, 33, 33, 20);
		lblTestclass.setText("Class");

		txtClassName = new Text(parent, SWT.BORDER);
		txtClassName.setBounds(172, 31, 321, 26);

		btnBrowse = new Button(parent, SWT.PUSH);
		btnBrowse.setBounds(514, 30, 60, 30);
		btnBrowse.setText("Browse");
		btnBrowse.addListener(SWT.Selection, e -> controller.openTypeSelectionDialog());

		lblMethods = new Label(parent, SWT.NONE);
		lblMethods.setBounds(15, 91, 52, 20);
		lblMethods.setText("Method");

		methods = new Combo(parent, SWT.NONE);
		methods.setBounds(172, 83, 321, 28);

		lblBlPackage = new Label(parent, SWT.NONE);
		lblBlPackage.setBounds(15, 139, 85, 20);
		lblBlPackage.setText("BL-Package");

		txtBlPackage = new Text(parent, SWT.BORDER);
		txtBlPackage.setBounds(172, 133, 321, 26);

		btnAdd = new Button(parent, SWT.NONE);
		btnAdd.setBounds(514, 162, 60, 30);
		btnAdd.setText("Add");
		btnAdd.addListener(SWT.Selection, e -> addTableRow());

		tblBlPackage = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION);
		tblBlPackage.setBounds(172, 211, 321, 49);
		tblBlPackage.setHeaderVisible(true);
		tblBlPackage.setLinesVisible(true);

		tblItem = new TableItem(tblBlPackage, SWT.NONE);

		TableColumn blPackage = new TableColumn(tblBlPackage, SWT.CENTER);
		blPackage.setText("BL-Package");

		TableColumn blPackageDest = new TableColumn(tblBlPackage, SWT.CENTER);
		blPackageDest.setText("BL-Package Dest");

		lblArgumentlist = new Label(parent, SWT.NONE);
		lblArgumentlist.setBounds(10, 286, 103, 20);
		lblArgumentlist.setText("Argument-List");

		txtArgumentList = new Text(parent, SWT.BORDER);
		txtArgumentList.setBounds(172, 284, 321, 115);

		btnCopy = new Button(parent, SWT.NONE);
		btnCopy.setBounds(514, 286, 60, 30);
		btnCopy.setText("Copy");

		lblBlPackageDest = new Label(parent, SWT.NONE);
		lblBlPackageDest.setBounds(15, 165, 156, 20);
		lblBlPackageDest.setText("BL-Package Dest.");

		txtBlPackageDest = new Text(parent, SWT.BORDER);
		txtBlPackageDest.setBounds(172, 165, 321, 26);
	}

	private void addTableRow() {
		tblItem.setText(0, txtBlPackage.getText());
		tblItem.setText(1, txtBlPackageDest.getText());

	}

	public void updateComponents() {
		txtClassName.setText(model.getClassName());

		methods.removeAll();
		model.getMethods().forEach(method -> methods.add(method));
		methods.select(model.getSelectedMethodIndex());

		Set<Entry<String, String>> blPackages = model.getBlPackages().entrySet();

		tblBlPackage.removeAll();
		for (Entry<String, String> packagePair : blPackages) {
			// BL-Package
			tblItem.setText(0, packagePair.getKey());
			// BL-Package Dest
			tblItem.setText(1, packagePair.getValue());
		}

	}

}

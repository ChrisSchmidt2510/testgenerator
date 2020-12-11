package org.testgen.testgenerator.ui.plugin.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.viewsupport.JavaElementImageProvider;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.testgen.testgenerator.ui.plugin.dialogs.model.BlProject;
import org.testgen.testgenerator.ui.plugin.dialogs.model.Model;
import org.testgen.testgenerator.ui.plugin.helper.Utils;

@SuppressWarnings("restriction")
public class TestgeneratorConfigurationDialog extends Dialog {
	private Label lblTestclass;
	private Label lblMethods;
	private Label lblCostumTestgeneratorClass;
	private Label lblTraceReadFieldAccess;
	private Label lblUseTestgeneratorbootstrap;
	private Label lblLaunchConfiguration;
	private Label lblSpacer;

	private Combo methods;

	private Text txtClassName;
	private Text txtCostumTestgenerator;
	private Text txtLaunchConfiguration;

	private Button btnBrowse;

	private Button btnAddProject;
	private Button btnRemoveProject;
	private Button btnBrowseCostumTestgenerator;
	private Button btnSelect;

	private Button checkBoxReadFieldAccess;
	private Button checkBoxUseTestgeneratorBootstrap;

	private final TestgeneratorConfigurationController controller;
	private final Model model;
	private Group grpBlprojects;
	private TableViewer tblViewerProjects;

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

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "Add", true);
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

		if (!Utils.checkStringFilled(model.getSelectedMethod())) {
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
		methods.addListener(SWT.Selection, e -> model.setSelectedMethod(methods.getText()));

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

		grpBlprojects = new Group(parent, SWT.NONE);
		grpBlprojects.setText("BL-Projects");
		grpBlprojects.setBounds(10, 178, 687, 132);

		tblViewerProjects = new TableViewer(grpBlprojects, SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.FULL_SELECTION);
		tblViewerProjects.setContentProvider(ArrayContentProvider.getInstance());
		tblViewerProjects.addDoubleClickListener(e -> updateProject());

		Table tblProjects = tblViewerProjects.getTable();
		tblProjects.setBounds(10, 24, 517, 98);
		tblProjects.setHeaderVisible(true);
		tblProjects.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				btnRemoveProject.setEnabled(true);
			}

		});

		TableViewerColumn tblColProject = new TableViewerColumn(tblViewerProjects, SWT.NONE);
		tblColProject.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				BlProject project = (BlProject) element;

				return project.getProject().getName();
			}

			@Override
			public Image getImage(Object element) {
				BlProject project = (BlProject) element;

				IJavaProject javaProject = JavaCore.create(project.getProject());

				return new JavaElementImageProvider()
						.getJavaImageDescriptor(javaProject,
								JavaElementImageProvider.OVERLAY_ICONS | JavaElementImageProvider.SMALL_ICONS)
						.createImage();
			}
		});

		TableColumn colProject = tblColProject.getColumn();
		colProject.setWidth(200);
		colProject.setText("Project");

		TableViewerColumn tblColPackages = new TableViewerColumn(tblViewerProjects, SWT.NONE);
		tblColPackages.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				BlProject project = (BlProject) element;

				List<String> packageNames = project.getSelectedPackages().stream()//
						.map(pkg -> pkg.getElementName()).collect(Collectors.toList());

				return String.join(",", packageNames);
			}

			@Override
			public Image getImage(Object element) {
				return JavaPluginImages.DESC_OBJS_PACKAGE.createImage();
			}
		});

		TableColumn colPackages = tblColPackages.getColumn();
		colPackages.setWidth(310);
		colPackages.setText("Selected Packages");

		tblViewerProjects.setInput(model.getProjects());

		btnAddProject = new Button(grpBlprojects, SWT.NONE);
		btnAddProject.setLocation(546, 24);
		btnAddProject.setSize(65, 30);
		btnAddProject.setText("Add");
		btnAddProject.addListener(SWT.Selection, e -> controller.addProject());

		btnRemoveProject = new Button(grpBlprojects, SWT.NONE);
		btnRemoveProject.setBounds(617, 24, 65, 30);
		btnRemoveProject.setText("Remove");
		btnRemoveProject.setEnabled(false);
		btnRemoveProject.addListener(SWT.Selection, e -> removeProject());

		lblLaunchConfiguration = new Label(parent, SWT.NONE);
		lblLaunchConfiguration.setBounds(10, 332, 155, 20);
		lblLaunchConfiguration.setText("Launch Configuration");

		Group optionalParams = new Group(parent, SWT.NONE);
		optionalParams.setBounds(10, 358, 687, 132);
		optionalParams.setText("optional parameter");

		lblTraceReadFieldAccess = new Label(optionalParams, SWT.NONE);
		lblTraceReadFieldAccess.setLocation(10, 27);
		lblTraceReadFieldAccess.setSize(156, 20);
		lblTraceReadFieldAccess.setText("Trace Read Fieldaccess");

		checkBoxReadFieldAccess = new Button(optionalParams, SWT.CHECK);
		checkBoxReadFieldAccess.setLocation(204, 27);
		checkBoxReadFieldAccess.setSize(16, 20);
		checkBoxReadFieldAccess.addListener(SWT.Selection, e -> updateModel());

		lblUseTestgeneratorbootstrap = new Label(optionalParams, SWT.NONE);
		lblUseTestgeneratorbootstrap.setBounds(10, 64, 194, 20);
		lblUseTestgeneratorbootstrap.setText("use Testgenerator-Bootstrap");

		checkBoxUseTestgeneratorBootstrap = new Button(optionalParams, SWT.CHECK);
		checkBoxUseTestgeneratorBootstrap.setBounds(204, 59, 16, 30);
		checkBoxUseTestgeneratorBootstrap.addListener(SWT.Selection, e -> updateModel());

		txtCostumTestgenerator = new Text(optionalParams, SWT.BORDER);
		txtCostumTestgenerator.setBounds(204, 93, 321, 26);
		txtCostumTestgenerator.setEditable(false);

		lblCostumTestgeneratorClass = new Label(optionalParams, SWT.NONE);
		lblCostumTestgeneratorClass.setBounds(10, 95, 178, 20);
		lblCostumTestgeneratorClass.setText("Costum TestgeneratorClass");

		btnBrowseCostumTestgenerator = new Button(optionalParams, SWT.NONE);
		btnBrowseCostumTestgenerator.setBounds(546, 90, 65, 30);
		btnBrowseCostumTestgenerator.setText("Browse");
		btnBrowseCostumTestgenerator.addListener(SWT.Selection, e -> controller.updateCustomTestgeneratorClass());

		txtLaunchConfiguration = new Text(parent, SWT.BORDER);
		txtLaunchConfiguration.setBounds(215, 330, 321, 26);
		txtLaunchConfiguration.setEditable(false);

		btnSelect = new Button(parent, SWT.NONE);
		btnSelect.setBounds(557, 327, 65, 30);
		btnSelect.setText("Select");
		btnSelect.addListener(SWT.Selection, e -> controller.selectLaunchConfiguration());

		lblSpacer = new Label(parent, SWT.NONE);
		lblSpacer.setBounds(637, 156, 70, 20);
	}

	public void updateComponents() {
		txtClassName.setText(model.getClassName());

		methods.removeAll();
		model.getMethods().forEach(methods::add);
		methods.select(model.getMethods().indexOf(model.getSelectedMethod()));

		checkBoxReadFieldAccess.setSelection(model.isTraceReadFieldAccess());

		if (model.getCostumTestgeneratorClassName() != null) {
			txtCostumTestgenerator.setText(model.getCostumTestgeneratorClassName());
		}

		if (model.getLaunchConfiguration() != null) {
			txtLaunchConfiguration.setText(model.getLaunchConfiguration().getName());
		}

		tblViewerProjects.refresh();

	}

	private void updateModel() {
		model.setTraceReadFieldAccess(checkBoxReadFieldAccess.getSelection());
		model.setUsetestgeneratorBootstrap(checkBoxUseTestgeneratorBootstrap.getSelection());
		model.setSelectedMethod(methods.getText());
	}

	private void removeProject() {
		IStructuredSelection selection = tblViewerProjects.getStructuredSelection();
		model.getProjects().remove(selection.getFirstElement());

		btnRemoveProject.setEnabled(false);

		tblViewerProjects.refresh();
	}

	private void updateProject() {
		IStructuredSelection selection = tblViewerProjects.getStructuredSelection();
		BlProject selectedProject = (BlProject) selection.getFirstElement();

		controller.updateProject(selectedProject);
	}
}

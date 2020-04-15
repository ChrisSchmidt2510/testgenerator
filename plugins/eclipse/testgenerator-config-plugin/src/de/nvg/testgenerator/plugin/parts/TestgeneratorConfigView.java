package de.nvg.testgenerator.plugin.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

public class TestgeneratorConfigView {
	private Label lblTestgeneratoragentConfiguration;
	private Label lblClassname;
	private Label lblMethod;
	private Label lblMethoddescriptor;
	private Label lblBlpackage;
	private Label lblTraceGetterCalls;

	private Text txtClassName;
	private Text txtMethod;
	private Text txtMethodDescriptor;
	private Text txtBlPackage;
	private Text argumentString;

	private Table tblBlPackages;

	private Button btnBrowse;
	private Button btnAdd;
	private Button checkboxTraceGetterCalls;
	private Button btnGenerateArgument;

	private Composite panelArguments;

	@PostConstruct
	public void createPartControl(Composite parent) {
		parent.setLayout(null);

		lblTestgeneratoragentConfiguration = new Label(parent, SWT.NONE);
		lblTestgeneratoragentConfiguration.setAlignment(SWT.CENTER);
		lblTestgeneratoragentConfiguration.setBounds(98, 10, 232, 20);
		lblTestgeneratoragentConfiguration.setText("Testgenerator-Agent Configuration");

		lblClassname = new Label(parent, SWT.NONE);
		lblClassname.setBounds(10, 49, 70, 26);
		lblClassname.setText("Classname");

		txtClassName = new Text(parent, SWT.BORDER);
		txtClassName.setBounds(147, 46, 183, 26);

		btnBrowse = new Button(parent, SWT.NONE);
		btnBrowse.setBounds(348, 44, 90, 30);
		btnBrowse.setText("Browse");

		lblMethod = new Label(parent, SWT.NONE);
		lblMethod.setBounds(10, 81, 70, 26);
		lblMethod.setText("Method");

		txtMethod = new Text(parent, SWT.BORDER);
		txtMethod.setBounds(147, 78, 183, 26);

		lblBlpackage = new Label(parent, SWT.NONE);
		lblBlpackage.setBounds(10, 145, 81, 26);
		lblBlpackage.setText("BL-Package");

		txtBlPackage = new Text(parent, SWT.BORDER);
		txtBlPackage.setBounds(147, 142, 183, 26);

		btnAdd = new Button(parent, SWT.NONE);
		btnAdd.setBounds(348, 140, 90, 30);
		btnAdd.setText("Add");

		tblBlPackages = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION);
		tblBlPackages.setBounds(10, 180, 428, 51);
		tblBlPackages.setHeaderVisible(true);
		tblBlPackages.setLinesVisible(true);

		lblTraceGetterCalls = new Label(parent, SWT.NONE);
		lblTraceGetterCalls.setBounds(10, 237, 107, 20);
		lblTraceGetterCalls.setText("TraceGetterCalls");

		checkboxTraceGetterCalls = new Button(parent, SWT.CHECK);
		checkboxTraceGetterCalls.setBounds(147, 237, 111, 20);

		panelArguments = new Composite(parent, SWT.NONE);
		panelArguments.setBounds(10, 299, 428, 87);
		panelArguments.setLayout(null);

		argumentString = new Text(panelArguments, SWT.BORDER);
		argumentString.setBounds(0, 10, 428, 77);

		lblMethoddescriptor = new Label(parent, SWT.NONE);
		lblMethoddescriptor.setBounds(10, 114, 127, 20);
		lblMethoddescriptor.setText("Method-Descriptor");

		txtMethodDescriptor = new Text(parent, SWT.BORDER);
		txtMethodDescriptor.setBounds(147, 110, 183, 26);

		btnGenerateArgument = new Button(parent, SWT.NONE);
		btnGenerateArgument.setBounds(10, 263, 144, 30);
		btnGenerateArgument.setText("Generate-Argument");
//		btnGenerateArgument.add
	}

	@Focus
	public void setFocus() {

	}

	/**
	 * This method is kept for E3 compatiblity. You can remove it if you do not mix
	 * E3 and E4 code. <br/>
	 * With E4 code you will set directly the selection in ESelectionService and you
	 * do not receive a ISelection
	 * 
	 * @param s the selection received from JFace (E3 mode)
	 */
	@Inject
	@Optional
	public void setSelection(@Named(IServiceConstants.ACTIVE_SELECTION) ISelection s) {
		if (s == null || s.isEmpty())
			return;

		if (s instanceof IStructuredSelection) {
			IStructuredSelection iss = (IStructuredSelection) s;
			if (iss.size() == 1)
				setSelection(iss.getFirstElement());
			else
				setSelection(iss.toArray());
		}
	}

	/**
	 * This method manages the selection of your current object. In this example we
	 * listen to a single Object (even the ISelection already captured in E3 mode).
	 * <br/>
	 * You should change the parameter type of your received Object to manage your
	 * specific selection
	 * 
	 * @param o : the current object received
	 */
	@Inject
	@Optional
	public void setSelection(@Named(IServiceConstants.ACTIVE_SELECTION) Object o) {

		// Remove the 2 following lines in pure E4 mode, keep them in mixed mode
		if (o instanceof ISelection) // Already captured
			return;
	}

	/**
	 * This method manages the multiple selection of your current objects. <br/>
	 * You should change the parameter type of your array of Objects to manage your
	 * specific selection
	 * 
	 * @param o : the current array of objects received in case of multiple
	 *          selection
	 */
	@Inject
	@Optional
	public void setSelection(@Named(IServiceConstants.ACTIVE_SELECTION) Object[] selectedObjects) {

	}
}

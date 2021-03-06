package org.testgen.testgenerator.ui.plugin.dialogs;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.internal.ui.DefaultLabelProvider;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.ide.IDE.SharedImages;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.ServerUICore;
import org.testgen.testgenerator.ui.plugin.TestgeneratorActivator;

@SuppressWarnings("restriction")
public class LaunchConfigurationDialog extends SelectionDialog {
	private TableViewer tblViewerApplication;
	private TableViewer tblViewerServer;

	private Collection<ILaunchConfiguration> inputApplications;
	private Collection<IServer> inputServers;

	private ColumnLabelProvider launchConfigName = new ColumnLabelProvider() {

		DefaultLabelProvider provider = new DefaultLabelProvider();

		@Override
		public String getText(Object element) {
			ILaunchConfiguration launch = ((ILaunchConfiguration) element);

			return provider.getText(launch);
		}

		@Override
		public Image getImage(Object element) {
			ILaunchConfiguration launch = ((ILaunchConfiguration) element);

			return provider.getImage(launch);
		}
	};

	public LaunchConfigurationDialog(Shell parentShell) {
		super(parentShell);
	}

	public void setInputApplications(Collection<ILaunchConfiguration> javaApplications) {
		this.inputApplications = javaApplications;
	}

	public void setInputServers(Collection<IServer> servers) {
		this.inputServers = servers;
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
		newShell.setText("Select Launch Configuration");
	}

	@Override
	protected void okPressed() {
		IStructuredSelection selectionApplication = tblViewerApplication.getStructuredSelection();
		if (!selectionApplication.isEmpty()) {
			setResult(selectionApplication.toList());
		}

		IStructuredSelection selectionServer = tblViewerServer.getStructuredSelection();
		if (!selectionServer.isEmpty()) {
			IServer server = (IServer) selectionServer.getFirstElement();

			try {
				setResult(Arrays.asList(server.getLaunchConfiguration(false, null)));
			} catch (CoreException e) {
				TestgeneratorActivator.log(e);
			}
		}

		super.okPressed();
	}

	private void initDialog(Composite container) {
		container.setLayout(null);

		Group grpJavaApplications = new Group(container, SWT.NONE);
		grpJavaApplications.setText("Java Applications");
		grpJavaApplications.setBounds(10, 10, 462, 142);

		tblViewerApplication = new TableViewer(grpJavaApplications,
				SWT.SINGLE | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		tblViewerApplication.addSelectionChangedListener(e -> tblViewerServer.getTable().setEnabled(false));
		tblViewerApplication.addDoubleClickListener(e -> okPressed());
		tblViewerApplication.setContentProvider(ArrayContentProvider.getInstance());

		tblViewerApplication.getTable().setBounds(10, 21, 442, 111);
		tblViewerApplication.getTable().setHeaderVisible(true);

		TableViewerColumn tblViewerApplicationColumnName = new TableViewerColumn(tblViewerApplication, SWT.NONE);
		TableColumn tblApplicationNameColumn = tblViewerApplicationColumnName.getColumn();
		tblApplicationNameColumn.setWidth(221);
		tblApplicationNameColumn.setText("Name");

		tblViewerApplicationColumnName.setLabelProvider(launchConfigName);

		TableViewerColumn tblViewerApplicationColumnProject = new TableViewerColumn(tblViewerApplication, SWT.NONE, 1);
		TableColumn tblApplicationProjectColumn = tblViewerApplicationColumnProject.getColumn();
		tblApplicationProjectColumn.setWidth(215);
		tblApplicationProjectColumn.setText("Project");

		tblViewerApplicationColumnProject.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ILaunchConfiguration launchConfig = (ILaunchConfiguration) element;

				try {
					return launchConfig.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME,
							(String) null);

				} catch (CoreException e) {
					TestgeneratorActivator.log(e);
					return null;
				}
			}

			@Override
			public Image getImage(Object element) {
				return PlatformUI.getWorkbench().getSharedImages().getImage(SharedImages.IMG_OBJ_PROJECT);
			}
		});

		tblViewerApplication.setInput(inputApplications);

		Group grpServer = new Group(container, SWT.NONE);
		grpServer.setText("Server");
		grpServer.setBounds(10, 166, 462, 131);

		tblViewerServer = new TableViewer(grpServer, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.FULL_SELECTION);
		tblViewerServer.getTable().setBounds(10, 20, 442, 101);
		tblViewerServer.addSelectionChangedListener(e -> tblViewerApplication.getTable().setEnabled(false));
		tblViewerServer.addDoubleClickListener(e -> okPressed());
		tblViewerServer.setContentProvider(ArrayContentProvider.getInstance());
		tblViewerServer.setLabelProvider(ServerUICore.getLabelProvider());
		tblViewerServer.setInput(inputServers);
	}
}

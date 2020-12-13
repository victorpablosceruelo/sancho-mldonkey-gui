/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.server.users;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import sancho.model.mldonkey.Server;
import sancho.view.viewFrame.ViewFrame;
import sancho.view.viewer.table.GTableView;

public class ServerUsersTableView extends GTableView {
	public static final int NAME = 0;
	public static final int TAGS = 1;
	public static final int ADDR = 2;
	public static final int PORT = 3;

	public ServerUsersTableView(ViewFrame viewFrame) {
		super(viewFrame);

		preferenceString = "serverUsers";
		columnLabels = new String[]{"serverUsers.name", "serverUsers.tags", "serverUsers.addr", "serverUsers.port"};
		columnAlignment = new int[]{SWT.LEFT, SWT.LEFT, SWT.RIGHT, SWT.RIGHT};
		columnDefaultWidths = new int[]{100, 100, 75, 75};

		gSorter = new ServerUsersTableSorter(this);
		tableContentProvider = new ServerUsersTableContentProvider(this, viewFrame.getCLabel());
		tableLabelProvider = new ServerUsersTableLabelProvider(this);
		tableMenuListener = new ServerUsersTableMenuListener(this);

		createContents(viewFrame.getChildComposite());
	}

	protected void createContents(Composite parent) {
		super.createContents(parent);
		sViewer.addSelectionChangedListener((ServerUsersTableMenuListener) tableMenuListener);
	}

	public void setInput() {
	}
	
	public void setInput(Server server) {
		sViewer.setInput(server);	
	}

	
}
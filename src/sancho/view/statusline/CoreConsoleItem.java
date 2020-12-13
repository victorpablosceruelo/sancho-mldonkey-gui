/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.statusline;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import sancho.core.Sancho;
import sancho.view.StatusLine;
import sancho.view.utility.SResources;

public class CoreConsoleItem {
	private Composite composite;

	public CoreConsoleItem(StatusLine statusLine) {
		this.composite = statusLine.getStatusline();
		createContents();
	}

	public void createContents() {
		Composite linkComposite = new Composite(composite, SWT.NONE);
		linkComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.FILL_VERTICAL));
		linkComposite.setLayout(new FillLayout());

		ToolBar toolBar = new ToolBar(linkComposite, SWT.FLAT);
		final ToolItem toolItem = new ToolItem(toolBar, SWT.NONE);
		toolItem.setImage(SResources.getImage("ProgramIcon-12"));
		toolItem.setToolTipText(SResources.getString("sl.coreConsole"));
		toolItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Sancho.getCoreConsole().getShell().open();
			}
		});
	}
}

/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.friends.clientDirectories;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import sancho.view.friends.clientFiles.ClientFilesTableView;
import sancho.view.viewer.table.GTableMenuListener;
import sancho.view.viewer.table.GTableView;

public class ClientDirectoriesTableView extends GTableView {
  public static final int DIRECTORY = 0;

  public ClientDirectoriesTableView(ClientDirectoriesViewFrame viewFrame) {
    super(viewFrame);

    preferenceString = "clientDirectories";
    columnLabels = new String[]{"clientDirectories.directory"};
    columnDefaultWidths = new int[]{120};
    columnAlignment = new int[]{SWT.LEFT};

    tableContentProvider = new ClientDirectoriesTableContentProvider(this);
    tableLabelProvider = new ClientDirectoriesTableLabelProvider(this);
    gSorter = new ClientDirectoriesTableSorter(this);
    tableMenuListener = new ClientDirectoriesTableMenuListener(this);
    this.createContents(viewFrame.getChildComposite());
  }

  public void setInput() {
  }

  public void setInput(Object object) {
    sViewer.setInput(object);
  }

  public GTableMenuListener getMenuListener() {
    return this.tableMenuListener;
  }

  public void setFilesView(ClientFilesTableView cFTV) {
    ((ClientDirectoriesTableMenuListener) tableMenuListener).setFilesView(cFTV);
    ((ClientDirectoriesTableContentProvider) tableContentProvider).setFilesView(cFTV);
  }

  protected void createContents(Composite parent) {
    super.createContents(parent);
    sViewer.addSelectionChangedListener((ClientDirectoriesTableMenuListener) tableMenuListener);
  }

}

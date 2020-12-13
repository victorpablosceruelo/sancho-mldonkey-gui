/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.friends.clientFiles;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import sancho.view.viewer.table.GTableMenuListener;
import sancho.view.viewer.table.GTableView;

public class ClientFilesTableView extends GTableView {
  public static final int NAME = 0;
  public static final int SIZE = 1;
  public static final int FORMAT = 2;
  public static final int MEDIA = 3;
  public static final int CODEC = 4;
  public static final int BITRATE = 5;
  public static final int LENGTH = 6;
  public static final int HASH = 7;

  public ClientFilesTableView(ClientFilesViewFrame viewFrame) {
    super(viewFrame);

    preferenceString = "clientFiles";
    columnLabels = new String[]{"clientFiles.name", "clientFiles.size", "clientFiles.format",
        "clientFiles.media", "clientFiles.codec", "clientFiles.bitrate", "clientFiles.length",
        "clientFiles.hash"};

    columnDefaultWidths = new int[]{150, 65, 50, 50, 60, 60, 60, 90};

    columnAlignment = new int[]{SWT.LEFT, SWT.RIGHT, SWT.LEFT, SWT.LEFT, SWT.RIGHT, SWT.RIGHT, SWT.RIGHT,
        SWT.LEFT};

    tableContentProvider = new ClientFilesTableContentProvider(this);
    tableLabelProvider = new ClientFilesTableLabelProvider(this);
    gSorter = new ClientFilesTableSorter(this);
    tableMenuListener = new ClientFilesTableMenuListener(this);
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

  protected void createContents(Composite parent) {
    super.createContents(parent);
    sViewer.addSelectionChangedListener((ClientFilesTableMenuListener) tableMenuListener);
  }

}

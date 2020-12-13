/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.shares;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import sancho.core.Sancho;
import sancho.view.viewFrame.ViewFrame;
import sancho.view.viewer.table.GTableView;

public class UploadTableView extends GTableView {
  public static final int NETWORK = 0;
  public static final int BYTES = 1;
  public static final int REQUESTS = 2;
  public static final int NAME = 3;
  public static final int SIZE = 4;

  public UploadTableView(ViewFrame viewFrame) {
    super(viewFrame);

    preferenceString = "upload";
    columnLabels = new String[]{"upload.network", "upload.uploaded", "upload.requests", "upload.name",
        "upload.size"};
    columnDefaultWidths = new int[]{100, 100, 100, 250, 100};
    columnAlignment = new int[]{SWT.LEFT, SWT.RIGHT, SWT.RIGHT, SWT.LEFT, SWT.RIGHT};

    gSorter = new UploadTableSorter(this);
    tableContentProvider = new UploadTableContentProvider(this);
    tableLabelProvider = new UploadTableLabelProvider(this);
    tableMenuListener = new UploadTableMenuListener(this);

    createContents(viewFrame.getChildComposite());
  }

  protected void createContents(Composite parent) {
    super.createContents(parent);
    sViewer.addSelectionChangedListener((UploadTableMenuListener) tableMenuListener);
  }

  public void setInput() {
    if (Sancho.hasCollectionFactory())
      sViewer.setInput(getCore().getSharedFileCollection());
  }
}
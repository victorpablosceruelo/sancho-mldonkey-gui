/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.transfer.pending;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import sancho.core.Sancho;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.viewFrame.ViewFrame;
import sancho.view.viewer.table.GTableView;

public class PendingTableView extends GTableView {
  public static final int NETWORK = 0;
  public static final int NAME = 1;
  public static final int SOFTWARE = 2;
  public static final int UPLOADED = 3;
  public static final int DOWNLOADED = 4;
  public static final int CONNECT_TIME = 5;
  public static final int SOCK_ADDR = 6;
  public static final int PORT = 7;
  public static final int KIND = 8;
  public static final int STATE = 9;
  public static final int FILENAME = 10;

  public PendingTableView(ViewFrame viewFrame) {
    super(viewFrame);

    preferenceString = "pending";
    columnLabels = new String[]{"pending.network", "pending.name", "pending.software", "pending.uploaded",
        "pending.downloaded", "pending.connectTime", "pending.addr", "pending.port", "pending.kind",
        "pending.state", "pending.filename"};

    columnDefaultWidths = new int[]{100, 100, 100, 100, 100, 100, 100, 100, 100, 150, 200};
    columnAlignment = new int[]{SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.RIGHT, SWT.RIGHT, SWT.RIGHT, SWT.RIGHT,
        SWT.RIGHT, SWT.LEFT, SWT.LEFT, SWT.LEFT};

    gSorter = new PendingTableSorter(this);
    tableContentProvider = new PendingTableContentProvider(this);
    tableLabelProvider = new PendingTableLabelProvider(this);
    tableMenuListener = new PendingTableMenuListener(this);

    createContents(viewFrame.getChildComposite());
  }

  protected void createContents(Composite parent) {
    super.createContents(parent);
    sViewer.addSelectionChangedListener((PendingTableMenuListener) tableMenuListener);
  }

  public void setInput() {
    if (Sancho.hasCollectionFactory()) {
      if (PreferenceLoader.loadBoolean("pollPending"))
        sViewer.setInput(getCore().getClientCollection().getPendingWeakMap());
      else
        sViewer.setInput(null);
    }
  }

  public void updateDisplay() {
    super.updateDisplay();
    if (PreferenceLoader.loadBoolean("pollPending")) {
      if (sViewer.getInput() == null)
        setInput();
    } else {
      if (sViewer.getInput() != null)
        sViewer.setInput(null);
    }
  }

}
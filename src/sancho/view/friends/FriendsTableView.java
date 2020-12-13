/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.friends;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import sancho.core.Sancho;
import sancho.model.mldonkey.enums.AbstractEnum;
import sancho.model.mldonkey.enums.EnumHostState;
import sancho.view.friends.clientDirectories.ClientDirectoriesTableView;
import sancho.view.viewFrame.ViewFrame;
import sancho.view.viewer.table.GTableView;

public class FriendsTableView extends GTableView {
  public static final int NAME = 0;
  public static final int STATE = 1;
  public static final int NETWORK = 2;
  public static final int SOFTWARE = 3;
  public static final int UPLOADED = 4;
  public static final int DOWNLOADED = 5;
  public static final int CONNECT_TIME = 6;
  public static final int SOCK_ADDR = 7;
  public static final int PORT = 8;
  public static final int KIND = 9;
  public static final int HAS_FILES = 10;

  public FriendsTableView(ViewFrame viewFrame) {
    super(viewFrame);

    preferenceString = "friends";
    columnLabels = new String[]{"friends.name", "friends.state", "friends.network", "friends.software",
        "friends.uploaded", "friends.downloaded", "friends.connectTime", "friends.addr", "friends.port",
        "friends.kind", "friends.hasFiles"};
    columnAlignment = new int[]{SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.RIGHT, SWT.RIGHT, SWT.RIGHT,
        SWT.RIGHT, SWT.RIGHT, SWT.LEFT, SWT.LEFT};
    columnDefaultWidths = new int[]{150, 100, 100, 100, 75, 75, 75, 100, 75, 75, 75};
    validStates = new AbstractEnum[]{EnumHostState.BLACKLISTED, EnumHostState.CONNECTED,
        EnumHostState.CONNECTED_AND_QUEUED, EnumHostState.CONNECTED_DOWNLOADING, EnumHostState.CONNECTED_INITIATING,
        EnumHostState.CONNECTING, EnumHostState.NEW_HOST, EnumHostState.NOT_CONNECTED, EnumHostState.NOT_CONNECTED_WAS_QUEUED};
    gSorter = new FriendsTableSorter(this);
    tableContentProvider = new FriendsTableContentProvider(this);
    tableLabelProvider = new FriendsTableLabelProvider(this);
    tableMenuListener = new FriendsTableMenuListener(this);

    createContents(viewFrame.getChildComposite());
  }

  protected void createContents(Composite parent) {
    super.createContents(parent);
    sViewer.addSelectionChangedListener((FriendsTableMenuListener) tableMenuListener);
    addMenuListener();
  }

  public void setDirectoryView(ClientDirectoriesTableView cDTV) {
    ((FriendsTableMenuListener) tableMenuListener).setDirectoriesView(cDTV);
  }

  public void setInput() {
    if (Sancho.hasCollectionFactory())
      sViewer.setInput(getCore().getClientCollection().getFriendsWeakMap());
  }

}

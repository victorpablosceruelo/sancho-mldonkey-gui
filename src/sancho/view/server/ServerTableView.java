/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.server;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import sancho.core.Sancho;
import sancho.model.mldonkey.enums.AbstractEnum;
import sancho.model.mldonkey.enums.EnumHostState;
import sancho.view.server.users.ServerUsersTableView;
import sancho.view.viewFrame.ViewFrame;
import sancho.view.viewer.table.GTableView;

public class ServerTableView extends GTableView {
  public static final int NETWORK = 0;
  public static final int NAME = 1;
  public static final int DESCRIPTION = 2;
  public static final int ADDRESS = 3;
  public static final int PORT = 4;
  public static final int SCORE = 5;
  public static final int USERS = 6;
  public static final int FILES = 7;
  public static final int STATE = 8;
  public static final int PREFERRED = 9;
  public static final int HIGH_ID = 10;

  public ServerTableView(ViewFrame viewFrame) {
    super(viewFrame);
    preferenceString = "server";

    columnLabels = new String[]{"servers.network", "servers.name", "servers.description", "servers.address",
        "servers.port", "servers.score", "servers.users", "servers.files", "servers.state",
        "servers.preferred", "servers.id"};

    columnDefaultWidths = new int[]{70, 160, 160, 120, 50, 55, 55, 60, 80, 50, 50};

    columnAlignment = new int[]{SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.RIGHT, SWT.RIGHT, SWT.RIGHT, SWT.RIGHT,
        SWT.RIGHT, SWT.LEFT, SWT.LEFT, SWT.LEFT};

    validStates = new AbstractEnum[]{EnumHostState.BLACKLISTED, EnumHostState.CONNECTED,
        EnumHostState.CONNECTED_INITIATING, EnumHostState.CONNECTING, EnumHostState.NOT_CONNECTED};

    tableContentProvider = new ServerTableContentProvider(this);
    tableLabelProvider = new ServerTableLabelProvider(this);
    gSorter = new ServerTableSorter(this);
    tableMenuListener = new ServerTableMenuListener(this);
    saveStateFilters = true;
    saveNetworkFilters = true;

    // Temporarily hide favorites column until it is implemented
    //    StringBuffer sb = new StringBuffer();
    //    sb.append(PreferenceLoader.loadString(preferenceString + "TableColumnsOff"));
    //    int ind;
    //    if ((ind = sb.indexOf("J")) == -1) {
    //      sb.append("J");
    //      PreferenceLoader.getPreferenceStore().setValue(preferenceString + "TableColumnsOff", sb.toString());
    //    }
    // end

    this.createContents(viewFrame.getChildComposite());

  }

  protected void createContents(Composite parent) {
    super.createContents(parent);
    sViewer.addSelectionChangedListener((ServerTableMenuListener) tableMenuListener);
    addMenuListener();
  }

  public void setInput() {
    if (Sancho.hasCollectionFactory())
      sViewer.setInput(getCore().getServerCollection());
  }

  public void setServerUsersTableView(ServerUsersTableView sUTV) {
    ((ServerTableMenuListener) tableMenuListener).setServerUsersTableView(sUTV);
  }

}
/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.rooms;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import sancho.core.Sancho;
import sancho.model.mldonkey.enums.AbstractEnum;
import sancho.model.mldonkey.enums.EnumRoomState;
import sancho.view.viewFrame.ViewFrame;
import sancho.view.viewer.table.GTableView;

public class RoomsTableView extends GTableView {
  public static final int NAME = 0;
  public static final int USERS = 1;
  public static final int STATE = 2;
  public static final int NETWORK = 3;
  public static final int NUMBER = 4;

  public RoomsTableView(ViewFrame viewFrame) {
    super(viewFrame);

    preferenceString = "rooms";
    columnLabels = new String[]{"rooms.name", "rooms.users", "rooms.state", "rooms.network", "rooms.number"};
    columnAlignment = new int[]{SWT.LEFT, SWT.RIGHT, SWT.LEFT, SWT.LEFT, SWT.RIGHT};
    columnDefaultWidths = new int[]{150, 50, 75, 150, 50};

    validStates = new AbstractEnum[]{EnumRoomState.OPEN, EnumRoomState.CLOSED, EnumRoomState.PAUSED};

    gSorter = new RoomsTableSorter(this);
    tableContentProvider = new RoomsTableContentProvider(this);
    tableLabelProvider = new RoomsTableLabelProvider(this);
    tableMenuListener = new RoomsTableMenuListener(this);

    createContents(viewFrame.getChildComposite());
  }

  protected void createContents(Composite parent) {
    super.createContents(parent);
    sViewer.addSelectionChangedListener((RoomsTableMenuListener) tableMenuListener);
    addMenuListener();
  }

  public void setInput() {
    if (Sancho.hasCollectionFactory())
      sViewer.setInput(getCore().getRoomCollection());
  }

}

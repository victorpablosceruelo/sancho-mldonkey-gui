/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.rooms.roomUsers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import sancho.core.Sancho;
import sancho.model.mldonkey.Room;
import sancho.view.viewFrame.ViewFrame;
import sancho.view.viewer.table.GTableView;

public class RoomUsersTableView extends GTableView {
  public static final int NAME = 0;
  public static final int TAGS = 1;
  public static final int ADDR = 2;
  public static final int PORT = 3;
  public static final int SERVER = 4;
  public Room room;

  public RoomUsersTableView(ViewFrame viewFrame, Room room) {
    super(viewFrame);
    this.room = room;

    preferenceString = "roomUsers";
    columnLabels = new String[]{"roomUsers.name", "roomUsers.tags", "roomUsers.addr", "roomUsers.port",
        "roomUsers.server"};
    columnAlignment = new int[]{SWT.LEFT, SWT.LEFT, SWT.RIGHT, SWT.RIGHT, SWT.RIGHT};
    columnDefaultWidths = new int[]{150, 150, 100, 50, 50};

    gSorter = new RoomUsersTableSorter(this);
    tableContentProvider = new RoomUsersTableContentProvider(this);
    tableLabelProvider = new RoomUsersTableLabelProvider(this);
    tableMenuListener = new RoomUsersTableMenuListener(this);

    createContents(viewFrame.getChildComposite());
  }

  protected void createContents(Composite parent) {
    super.createContents(parent);
    sViewer.addSelectionChangedListener((RoomUsersTableMenuListener) tableMenuListener);
    addMenuListener();
  }

  public void setInput() {
    if (Sancho.hasCollectionFactory() && room != null)
      sViewer.setInput(room.getUserMap());
  }
}
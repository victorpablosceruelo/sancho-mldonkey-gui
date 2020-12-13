/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.rooms.roomUsers;

import sancho.model.mldonkey.User;
import sancho.view.viewer.table.GTableLabelProvider;

public class RoomUsersTableLabelProvider extends GTableLabelProvider {
  public RoomUsersTableLabelProvider(RoomUsersTableView rTableView) {
    super(rTableView);
  }

  public String getColumnText(Object element, int columnIndex) {
    User user = (User) element;

    switch (cViewer.getColumnIDs()[columnIndex]) {
      case RoomUsersTableView.NAME :
        return user.getName();
      case RoomUsersTableView.TAGS :
        return user.getTagsString();
      case RoomUsersTableView.ADDR :
        return user.getAddr().toString();
      case RoomUsersTableView.PORT :
        return String.valueOf(user.getPort());
      case RoomUsersTableView.SERVER :
        return String.valueOf(user.getServerId());
      default :
        return "??";
    }
  }
}
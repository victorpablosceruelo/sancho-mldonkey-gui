/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.rooms.roomUsers;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import sancho.model.mldonkey.User;
import sancho.view.viewer.GSorter;

public class RoomUsersTableSorter extends GSorter {
  public RoomUsersTableSorter(RoomUsersTableView rTableView) {
    super(rTableView);
  }

  public int compare(Viewer viewer, Object obj1, Object obj2) {
    User user1 = (User) obj1;
    User user2 = (User) obj2;

    switch (cViewer.getColumnIDs()[columnIndex]) {
      case RoomUsersTableView.NAME :
        return compareStrings(user1.getName(), user2.getName());
      case RoomUsersTableView.TAGS :
        return compareStrings(user1.getTagsString(), user2.getTagsString());
      case RoomUsersTableView.ADDR :
        return compareAddrs(user1.getAddr(), user2.getAddr());
      case RoomUsersTableView.PORT :
        return compareInts(user1.getPort(), user2.getPort());
      case RoomUsersTableView.SERVER :
        return compareInts(user1.getServerId(), user2.getServerId());
      default :
        return compareDefault((TableViewer) viewer, columnIndex, obj1, obj2);
    }
  }
}
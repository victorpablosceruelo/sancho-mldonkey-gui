/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.server.users;

import org.eclipse.jface.viewers.Viewer;

import sancho.model.mldonkey.User;
import sancho.view.viewer.GSorter;

public class ServerUsersTableSorter extends GSorter {
  public ServerUsersTableSorter(ServerUsersTableView sTableViewer) {
    super(sTableViewer);
  }

  public int compare(Viewer viewer, Object obj1, Object obj2) {
    User user1 = (User) obj1;
    User user2 = (User) obj2;

    switch (cViewer.getColumnIDs()[columnIndex]) {
      case ServerUsersTableView.NAME :
        return compareStrings(user1.getName(), user2.getName());
      case ServerUsersTableView.TAGS :
        return compareStrings(user1.getTagsString(), user2.getTagsString());
      case ServerUsersTableView.ADDR :
        return compareAddrs(user1.getAddr(), user2.getAddr());
      case ServerUsersTableView.PORT :
        return compareInts(user1.getPort(), user2.getPort());
      default :
        return 0;
    }
  }
}


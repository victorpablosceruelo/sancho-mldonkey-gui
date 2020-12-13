/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.friends;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import sancho.model.mldonkey.Client;
import sancho.view.viewer.GSorter;

public class FriendsTableSorter extends GSorter {

  public FriendsTableSorter(FriendsTableView fTableView) {
    super(fTableView);
  }

  public boolean sortOrder(int columnIndex) {
    switch (cViewer.getColumnIDs()[columnIndex]) {
      case FriendsTableView.UPLOADED :
      case FriendsTableView.DOWNLOADED :
      case FriendsTableView.CONNECT_TIME :
      case FriendsTableView.SOCK_ADDR :
      case FriendsTableView.PORT :
        return false;

      default :
        return true;
    }
  }

  public int compare(Viewer viewer, Object obj1, Object obj2) {
    Client client1 = (Client) obj1;
    Client client2 = (Client) obj2;

    switch (cViewer.getColumnIDs()[columnIndex]) {
      case FriendsTableView.UPLOADED :
        return compareLongs(client1.getUploaded(), client2.getUploaded());

      case FriendsTableView.DOWNLOADED :
        return compareLongs(client1.getDownloaded(), client2.getDownloaded());

      case FriendsTableView.SOCK_ADDR :
        return compareAddrs(client1.getAddr(), client2.getAddr());
      case FriendsTableView.PORT :
        return compareInts(client1.getPort(), client2.getPort());
      case FriendsTableView.CONNECT_TIME :
        return compareInts(client1.getConnectedTime(), client2.getConnectedTime());
      case FriendsTableView.STATE :
        return compareClientStates(client1, client2);
      case FriendsTableView.HAS_FILES :
        return compareBooleans(client1.hasFiles(), client2.hasFiles());
      default :
        return compareDefault((TableViewer) viewer, columnIndex, obj1, obj2);
    }
  }
}
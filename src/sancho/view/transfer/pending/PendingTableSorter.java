/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.transfer.pending;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import sancho.model.mldonkey.Client;
import sancho.view.viewer.GSorter;

public class PendingTableSorter extends GSorter {
  public PendingTableSorter(PendingTableView uTableViewer) {
    super(uTableViewer);
  }

  public boolean sortOrder(int columnIndex) {
    switch (cViewer.getColumnIDs()[columnIndex]) {
      case PendingTableView.UPLOADED :
      case PendingTableView.DOWNLOADED :
      case PendingTableView.CONNECT_TIME :
      case PendingTableView.SOCK_ADDR :
      case PendingTableView.PORT :
        return false;

      default :
        return true;
    }
  }

  public int compare(Viewer viewer, Object obj1, Object obj2) {
    Client client1 = (Client) obj1;
    Client client2 = (Client) obj2;

    switch (cViewer.getColumnIDs()[columnIndex]) {
      case PendingTableView.UPLOADED :
        return compareLongs(client1.getUploaded(), client2.getUploaded());

      case PendingTableView.DOWNLOADED :
        return compareLongs(client1.getDownloaded(), client2.getDownloaded());

      case PendingTableView.STATE :
        return compareClientStates(client1, client2);

      case PendingTableView.SOCK_ADDR :
        return compareAddrs(client1.getAddr(), client2.getAddr());

      case PendingTableView.PORT :
        return compareInts(client1.getPort(), client2.getPort());

      case PendingTableView.CONNECT_TIME :
        return compareInts(client1.getConnectedTime(), client2.getConnectedTime());

      default :
        return compareDefault((TableViewer) viewer, columnIndex, obj1, obj2);

    }
  }
}
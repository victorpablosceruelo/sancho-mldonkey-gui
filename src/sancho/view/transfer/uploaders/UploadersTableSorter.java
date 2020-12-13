/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.transfer.uploaders;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import sancho.model.mldonkey.Client;
import sancho.view.viewer.GSorter;

public class UploadersTableSorter extends GSorter {
  public UploadersTableSorter(UploadersTableView uTableViewer) {
    super(uTableViewer);
  }

  public boolean sortOrder(int columnIndex) {
    switch (cViewer.getColumnIDs()[columnIndex]) {
      case UploadersTableView.UPLOADED :
      case UploadersTableView.DOWNLOADED :
      case UploadersTableView.CONNECT_TIME :
      case UploadersTableView.SOCK_ADDR :
      case UploadersTableView.PORT :
        return false;

      default :
        return true;
    }
  }

  public int compare(Viewer viewer, Object obj1, Object obj2) {
    Client client1 = (Client) obj1;
    Client client2 = (Client) obj2;

    switch (cViewer.getColumnIDs()[columnIndex]) {
      case UploadersTableView.UPLOADED :
        return compareLongs(client1.getUploaded(), client2.getUploaded());

      case UploadersTableView.DOWNLOADED :
        return compareLongs(client1.getDownloaded(), client2.getDownloaded());

      case UploadersTableView.STATE :
        return compareClientStates(client1, client2);

      case UploadersTableView.SOCK_ADDR :
        return compareAddrs(client1.getAddr(), client2.getAddr());

      case UploadersTableView.PORT :
        return compareInts(client1.getPort(), client2.getPort());

      case UploadersTableView.CONNECT_TIME :
        return compareInts(client1.getConnectedTime(), client2.getConnectedTime());

      default :
        return compareDefault((TableViewer) viewer, columnIndex, obj1, obj2);

    }
  }
}

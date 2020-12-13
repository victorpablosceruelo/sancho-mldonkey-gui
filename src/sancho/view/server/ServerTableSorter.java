/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.server;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import sancho.model.mldonkey.Server;
import sancho.view.viewer.GSorter;

public class ServerTableSorter extends GSorter {
  public ServerTableSorter(ServerTableView sTableViewer) {
    super(sTableViewer);
  }

  public boolean sortOrder(int columnIndex) {
    switch (cViewer.getColumnIDs()[columnIndex]) {
      case ServerTableView.NETWORK :
      case ServerTableView.NAME :
      case ServerTableView.DESCRIPTION :
      case ServerTableView.STATE :
        return true;

      default :
        return false;
    }
  }

  public int compare(Viewer viewer, Object obj1, Object obj2) {
    Server server1 = (Server) obj1;
    Server server2 = (Server) obj2;

    switch (cViewer.getColumnIDs()[columnIndex]) {
      case ServerTableView.NETWORK :
        return compareStrings(server1.getNetworkName(), server2.getNetworkName());

      case ServerTableView.NAME :
        return compareStrings(server1.getName(), server2.getName());

      case ServerTableView.DESCRIPTION :
        return compareStrings(server1.getDescription(), server2.getDescription());

      case ServerTableView.ADDRESS :
        return compareAddrs(server1.getAddr(), server2.getAddr());

      case ServerTableView.PORT :
        return compareInts(server1.getPort(), server2.getPort());

      case ServerTableView.SCORE :
        return compareInts(server1.getScore(), server2.getScore());

      case ServerTableView.USERS :
        return compareLongs(server1.getNumUsers(), server2.getNumUsers());

      case ServerTableView.FILES :
        return compareLongs(server1.getNumFiles(), server2.getNumFiles());

      case ServerTableView.STATE :
        return compareStrings(server1.getStateEnum().getName(), server2.getStateEnum().getName());

      case ServerTableView.PREFERRED :
        return compareBooleans(server1.isPreferred(), server2.isPreferred());

      default :
        return compareDefault((TableViewer) viewer, columnIndex, obj1, obj2);

    }
  }
}

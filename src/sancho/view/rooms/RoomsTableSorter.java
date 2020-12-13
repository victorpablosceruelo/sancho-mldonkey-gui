/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.rooms;

import org.eclipse.jface.viewers.Viewer;

import sancho.model.mldonkey.Room;
import sancho.view.viewer.GSorter;

public class RoomsTableSorter extends GSorter {
  public RoomsTableSorter(RoomsTableView rTableView) {
    super(rTableView);
  }

  public int compare(Viewer viewer, Object obj1, Object obj2) {
    Room room1 = (Room) obj1;
    Room room2 = (Room) obj2;

    switch (cViewer.getColumnIDs()[columnIndex]) {
      case RoomsTableView.NAME :
        return compareStrings(room1.getName(), room2.getName());
      case RoomsTableView.NUMBER :
        return compareInts(room1.getId(), room2.getId());
      case RoomsTableView.USERS :
        return compareInts(room1.getNumUsers(), room2.getNumUsers());
      case RoomsTableView.NETWORK :
        return compareStrings(room1.getNetworkName(), room2.getNetworkName());
      case RoomsTableView.STATE :
        return compareStrings(room1.getRoomState().getName(), room2.getRoomState().getName());
      default :
        return 0;
    }
  }
}
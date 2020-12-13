/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.rooms;

import org.eclipse.swt.graphics.Image;

import sancho.model.mldonkey.Room;
import sancho.view.viewer.table.GTableLabelProvider;

public class RoomsTableLabelProvider extends GTableLabelProvider {
  public RoomsTableLabelProvider(RoomsTableView rTableView) {
    super(rTableView);
  }

  public Image getColumnImage(Object arg0, int arg1) {
    Room room = (Room) arg0;

    switch (cViewer.getColumnIDs()[arg1]) {
      case RoomsTableView.NETWORK :
      case RoomsTableView.NAME :
        return room.getNetworkImage();
      default :
        return null;
    }
  }

  public String getColumnText(Object element, int columnIndex) {
    Room room = (Room) element;

    switch (cViewer.getColumnIDs()[columnIndex]) {
      case RoomsTableView.NAME :
        return room.getName();
      case RoomsTableView.USERS :
        return String.valueOf(room.getNumUsers());
      case RoomsTableView.STATE :
        return room.getRoomState().getName();
      case RoomsTableView.NETWORK :
        return room.getNetworkName();
      case RoomsTableView.NUMBER :
        return String.valueOf(room.getId());
      default :
        return "?";
    }
  }
}
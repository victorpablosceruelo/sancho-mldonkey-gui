/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.rooms;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.SelectionChangedEvent;

import sancho.model.mldonkey.Room;
import sancho.model.mldonkey.enums.EnumRoomState;
import sancho.view.utility.SResources;
import sancho.view.viewer.table.GTableMenuListener;

public class RoomsTableMenuListener extends GTableMenuListener {

  public RoomsTableMenuListener(RoomsTableView rTableView) {
    super(rTableView);
  }

  public void selectionChanged(SelectionChangedEvent event) {
    collectSelections(event, Room.class);
  }

  public void menuAboutToShow(IMenuManager menuManager) {
    if (selectedObjects.size() > 0) {
      menuManager.add(new SetRoomStateAction(EnumRoomState.OPEN));
      menuManager.add(new SetRoomStateAction(EnumRoomState.CLOSED));
      menuManager.add(new SetRoomStateAction(EnumRoomState.PAUSED));
    }
  }

  private class SetRoomStateAction extends Action {
    private EnumRoomState enumRoomState;

    public SetRoomStateAction(EnumRoomState enumRoomState) {
      super("Set: " + enumRoomState.getName());
      this.enumRoomState = enumRoomState;
      setImageDescriptor(SResources.getImageDescriptor("rooms.buttonActiveSmall"));
    }

    public void run() {
      for (int i = 0; i < selectedObjects.size(); i++) {
        Room room = (Room) selectedObjects.get(i);
        room.setRoomState(enumRoomState);
      }
    }
  }

}

/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.rooms.roomUsers;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.SelectionChangedEvent;

import sancho.model.mldonkey.User;
import sancho.view.viewer.actions.AddUserAsFriendAction;
import sancho.view.viewer.table.GTableMenuListener;

public class RoomUsersTableMenuListener extends GTableMenuListener {

  public RoomUsersTableMenuListener(RoomUsersTableView rTableView) {
    super(rTableView);
  }

  public void selectionChanged(SelectionChangedEvent event) {
    collectSelections(event, User.class);
  }

  public void menuAboutToShow(IMenuManager menuManager) {
    if (selectedObjects.size() > 0) {
      User[] userArray = new User[selectedObjects.size()];
      for (int i = 0; i < selectedObjects.size(); i++)
        userArray[i] = (User) selectedObjects.get(i);

      menuManager.add(new AddUserAsFriendAction(userArray));
    }
  }

}
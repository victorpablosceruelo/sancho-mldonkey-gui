/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.rooms.roomUsers;

import org.eclipse.swt.custom.SashForm;

import sancho.model.mldonkey.Room;
import sancho.view.utility.AbstractTab;
import sancho.view.viewFrame.SashViewFrame;

public class RoomUsersViewFrame extends SashViewFrame {
  public RoomUsersViewFrame(SashForm parentSashForm, String prefString, String prefImageString,
      AbstractTab aTab, Room room) {
    super(parentSashForm, prefString, prefImageString, aTab, true);

    gView = new RoomUsersTableView(this, room);
    createViewListener(new RoomUsersViewListener(this));
  }
}
/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.rooms.roomUsers;

import java.util.Observable;

import org.eclipse.jface.viewers.Viewer;

import sancho.utility.ObjectMap;
import sancho.view.utility.SResources;
import sancho.view.viewer.table.GTableContentProviderOM;

public class RoomUsersTableContentProvider extends GTableContentProviderOM {

  public static final String S_ROOM_USERS = SResources.getString("t.r.roomUsers");

  public RoomUsersTableContentProvider(RoomUsersTableView rTableView) {
    super(rTableView);
  }

  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    super.inputChanged(viewer, oldInput, newInput);

    if (oldInput != null)
      ((Observable) oldInput).deleteObserver(this);

    if (newInput != null) {
      ((ObjectMap) newInput).addObserver(this);
      updateHeaderLabel(((ObjectMap) newInput).size());
    }
  }

  public void update(final Observable o, final Object obj) {
    updateViewer((ObjectMap) o, ((Integer) obj).intValue(), false);
  }

  public void updateHeaderLabel(int size) {
    gView.getViewFrame().updateCLabelText(S_ROOM_USERS + SResources.S_COLON + SResources.S_SPACE + size);
  }

}
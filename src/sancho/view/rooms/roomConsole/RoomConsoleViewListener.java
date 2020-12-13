/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.rooms.roomConsole;

import org.eclipse.jface.action.IMenuManager;

import sancho.view.viewFrame.SashViewFrame;
import sancho.view.viewFrame.SashViewListener;

class RoomConsoleViewListener extends SashViewListener {
  public RoomConsoleViewListener(SashViewFrame sashViewFrame) {
    super(sashViewFrame);
  }

  public void menuAboutToShow(IMenuManager menuManager) {
    createSashActions(menuManager, "t.r.roomUsers");
  }
}
/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.friends.clientDirectories;

import org.eclipse.jface.action.IMenuManager;

import sancho.view.viewFrame.SashViewFrame;
import sancho.view.viewFrame.SashViewListener;

public class ClientDirectoriesViewListener extends SashViewListener {

  public ClientDirectoriesViewListener(SashViewFrame cSashViewFrame) {
    super(cSashViewFrame);
  }

  public void menuAboutToShow(IMenuManager menuManager) {
    createSashActions(menuManager, "l.clientFiles");
  }
}
/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.friends.clientFiles;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;

import sancho.view.viewFrame.SashViewFrame;
import sancho.view.viewFrame.SashViewListener;
import sancho.view.viewer.actions.ColumnSelectorAction;

public class ClientFilesViewListener extends SashViewListener {

  public ClientFilesViewListener(SashViewFrame cSashViewFrame) {
    super(cSashViewFrame);
  }

  public void menuAboutToShow(IMenuManager menuManager) {
    // columnSelector
    menuManager.add(new ColumnSelectorAction(gView));
    menuManager.add(new Separator());
    createDynamicColumnSubMenu(menuManager);
    // for macOS
    createSortByColumnSubMenu(menuManager);
    // flip sash/maximize sash
    createSashActions(menuManager, "l.clientDirectories");
  }
}

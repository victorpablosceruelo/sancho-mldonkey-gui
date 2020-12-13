/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.downloadComplete;

import org.eclipse.jface.action.IMenuManager;

import sancho.view.viewFrame.ViewFrame;
import sancho.view.viewFrame.ViewListener;

public class DownloadCompleteViewListener extends ViewListener {

  public DownloadCompleteViewListener(ViewFrame viewFrame) {
    super(viewFrame);
  }

  public void menuAboutToShow(IMenuManager menuManager) {
    createDynamicColumnSubMenu(menuManager);
    // for macOS
    createSortByColumnSubMenu(menuManager);
  }
}
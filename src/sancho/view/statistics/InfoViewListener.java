/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.statistics;

import org.eclipse.jface.action.IMenuManager;

import sancho.view.viewFrame.SashViewFrame;
import sancho.view.viewFrame.SashViewListener;

public class InfoViewListener extends SashViewListener {
  public InfoViewListener(SashViewFrame viewFrame) {
    super(viewFrame);
  }

  public void menuAboutToShow(IMenuManager menuManager) {
    // flip sash/maximize sash
    createSashActions(menuManager, "tab.statistics.tooltip");
  }
}

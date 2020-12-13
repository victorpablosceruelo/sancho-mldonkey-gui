/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewFrame.actions;

import org.eclipse.jface.action.Action;

import sancho.view.utility.SResources;
import sancho.view.viewFrame.TabbedViewFrame;

public class ToggleTabsAction extends Action {

  TabbedViewFrame viewFrame;

  public ToggleTabsAction(TabbedViewFrame viewFrame) {
    super(SResources.getString("mi.toggleTabs"));
    setImageDescriptor(SResources.getImageDescriptor("toggle"));
    this.viewFrame = viewFrame;
  }

  public void run() {
    viewFrame.toggleTabs();
  }

}
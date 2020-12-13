/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.shares;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;

import sancho.view.utility.SResources;
import sancho.view.viewFrame.TabbedViewFrame;
import sancho.view.viewFrame.ViewFrame;
import sancho.view.viewFrame.ViewListener;
import sancho.view.viewFrame.actions.ToggleTabsAction;
import sancho.view.viewer.actions.ColumnSelectorAction;
import sancho.view.viewer.actions.RefreshUploadsAction;
import sancho.view.viewer.actions.RemoveAllFiltersAction;

public class UploadViewListener extends ViewListener {

  public UploadViewListener(ViewFrame viewFrame) {
    super(viewFrame);
  }

  public void menuAboutToShow(IMenuManager menuManager) {
    // refresh table
    menuManager.add(new RefreshUploadsAction(gView));
    menuManager.add(new Separator());
    // columnSelector
    menuManager.add(new ColumnSelectorAction(gView));
    menuManager.add(new Separator());
    createDynamicColumnSubMenu(menuManager);

    // for macOS
    createSortByColumnSubMenu(menuManager);

    // filter submenu			
    MenuManager filterSubMenu = new MenuManager(SResources.getString("mi.filter"));

    // all filters
    filterSubMenu.add(new RemoveAllFiltersAction(gView));
    filterSubMenu.add(new Separator());

    // network filters
    createEnabledNetworkFilterSubMenu(filterSubMenu);

    menuManager.add(filterSubMenu);
    menuManager.add(new ToggleTabsAction((TabbedViewFrame) viewFrame));
  }
}


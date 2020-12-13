/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.rooms;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;

import sancho.view.utility.SResources;
import sancho.view.viewFrame.SashViewFrame;
import sancho.view.viewFrame.SashViewListener;
import sancho.view.viewer.actions.ColumnSelectorAction;
import sancho.view.viewer.actions.RemoveAllFiltersAction;

public class RoomsViewListener extends SashViewListener {
  public RoomsViewListener(SashViewFrame sashViewFrame) {
    super(sashViewFrame);
  }

  public void menuAboutToShow(IMenuManager menuManager) {

    // columnSelector
    menuManager.add(new ColumnSelectorAction(gView));
    menuManager.add(new Separator());
    createDynamicColumnSubMenu(menuManager);
    // for macOS
    createSortByColumnSubMenu(menuManager);

    // filter submenu
    MenuManager filterSubMenu = new MenuManager(SResources.getString("mi.show"));

    // all filters
    filterSubMenu.add(new RemoveAllFiltersAction(gView));
    filterSubMenu.add(new Separator());

    // network filters
    createEnabledNetworkFilterSubMenu(filterSubMenu);

    // state filter
    filterSubMenu.add(new Separator());

    createStateFilterMenuItems(filterSubMenu);

    menuManager.add(filterSubMenu);

    // flip sash/maximize sash
    createSashActions(menuManager, "l.room");
  }
}
/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.transfer.downloads;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;

import sancho.model.mldonkey.enums.EnumFileState;
import sancho.view.utility.SResources;
import sancho.view.viewFrame.TabbedSashViewFrame;
import sancho.view.viewFrame.TabbedSashViewListener;
import sancho.view.viewFrame.actions.ToggleTabsAction;
import sancho.view.viewer.actions.ColumnSelectorAction;
import sancho.view.viewer.actions.ExclusionStateFilterAction;
import sancho.view.viewer.actions.RemoveAllFiltersAction;

public class DownloadViewListener extends TabbedSashViewListener {

  public DownloadViewListener(TabbedSashViewFrame sashViewFrame) {
    super(sashViewFrame);
  }

  public void menuAboutToShow(IMenuManager menuManager) {
    // columnSelector
    menuManager.add(new ColumnSelectorAction(gView));
    menuManager.add(new Separator());
    createDynamicColumnSubMenu(menuManager);
    // sortMenu for macOS
    createSortByColumnSubMenu(menuManager);

    // filter submenu
    MenuManager filterSubMenu = new MenuManager(SResources.getString("mi.show"));

    // all filters
    filterSubMenu.add(new RemoveAllFiltersAction(gView));

    // network filters
    filterSubMenu.add(new Separator());
    createEnabledNetworkFilterSubMenu(filterSubMenu);

    // state filter - exclusionary
    filterSubMenu.add(new Separator());
    filterSubMenu.add(new ExclusionStateFilterAction(EnumFileState.PAUSED.getName(), gView,
        EnumFileState.PAUSED));
    filterSubMenu.add(new ExclusionStateFilterAction(EnumFileState.QUEUED.getName(), gView,
        EnumFileState.QUEUED));

    filterSubMenu.add(new Separator());

    createExtensionFilterMenuItems(filterSubMenu);
    menuManager.add(filterSubMenu);
    menuManager.add(new ToggleTabsAction((TabbedSashViewFrame) viewFrame));

    filterSubMenu.add(new Separator());
    // flip sash/maximize sash
    createSashActions(menuManager, "l.uploaders");
  }

}
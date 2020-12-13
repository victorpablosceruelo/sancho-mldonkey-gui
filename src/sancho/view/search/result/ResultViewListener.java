/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.search.result;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.custom.CTabItem;

import sancho.view.utility.SResources;
import sancho.view.viewFrame.CTabFolderViewFrame;
import sancho.view.viewFrame.CTabFolderViewListener;
import sancho.view.viewer.GView;
import sancho.view.viewer.actions.ColumnSelectorAction;
import sancho.view.viewer.actions.RemoveAllFiltersAction;

public class ResultViewListener extends CTabFolderViewListener {

	public ResultViewListener(CTabFolderViewFrame cTabFolderViewFrame) {
		super(cTabFolderViewFrame);
	}

	public void menuAboutToShow(IMenuManager menuManager) {
		if (cTabFolder != null) {
			// check if a table exists in any tab
			CTabItem[] cTabItems = cTabFolder.getItems();

			for (int i = 0; i < cTabItems.length; i++) {
				if (cTabItems[i].getData(GView.S_GVIEW) != null) {
					menuManager.add(new ColumnSelectorAction(cTabFolder));
					break;
				}
			}

			// filters available if currentTab has a table
			if (cTabFolder.getSelection() != null
					&& cTabFolder.getSelection().getData(GView.S_GVIEW) != null) {
				gView = (GView) cTabFolder.getSelection().getData(GView.S_GVIEW);
				menuManager.add(new Separator());
				createDynamicColumnSubMenu(menuManager);
				// for macOS
				createSortByColumnSubMenu(menuManager);

				// filter submenu
				MenuManager filterSubMenu = new MenuManager(SResources
						.getString("mi.show"));

				// all filters
				filterSubMenu.add(new RemoveAllFiltersAction(gView));
				filterSubMenu.add(new Separator());

				// network filters
				createEnabledNetworkFilterSubMenu(filterSubMenu);

				// state (avail) filter
				filterSubMenu.add(new Separator());

				createStateFilterMenuItems(filterSubMenu);

				menuManager.add(filterSubMenu);
			}
		}
	}
}

/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.shares;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.SelectionChangedEvent;

import sancho.model.mldonkey.SharedFile;
import sancho.view.utility.SResources;
import sancho.view.viewer.actions.CopyED2KLinkToClipboardAction;
import sancho.view.viewer.table.GTableMenuListener;
import sancho.view.viewer.table.GTableView;

public class UploadTableMenuListener extends GTableMenuListener {

  public UploadTableMenuListener(GTableView gTableViewer) {
    super(gTableViewer);
  }

  public void selectionChanged(SelectionChangedEvent event) {
    collectSelections(event, SharedFile.class);
  }

  public void menuAboutToShow(IMenuManager menuManager) {
    if (selectedObjects.size() > 0) {
      String[] linkList = new String[selectedObjects.size()];

      for (int i = 0; i < selectedObjects.size(); i++)
        linkList[i] = ((SharedFile) selectedObjects.get(i)).getED2K();

      MenuManager clipboardMenu = new MenuManager(SResources.getString("m.d.copyTo"));
      clipboardMenu.add(new CopyED2KLinkToClipboardAction(false, linkList));
      clipboardMenu.add(new CopyED2KLinkToClipboardAction(true, linkList));
      menuManager.add(clipboardMenu);

    }
  }
}

/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.downloadComplete;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;

import sancho.view.viewer.actions.CopyED2KLinkToClipboardAction;
import sancho.view.viewer.table.GTableMenuListener;

public class DownloadCompleteTableMenuListener extends GTableMenuListener
    implements
      ISelectionChangedListener {

  public DownloadCompleteTableMenuListener(DownloadCompleteTableView fTableView) {
    super(fTableView);
  }

  public void selectionChanged(SelectionChangedEvent event) {
    collectSelections(event, DownloadCompleteItem.class);
  }

  public void menuAboutToShow(IMenuManager menuManager) {
    if (selectedObjects.size() > 0) {
      String[] linkList = new String[selectedObjects.size()];

      for (int i = 0; i < selectedObjects.size(); i++)
        linkList[i] = ((DownloadCompleteItem) selectedObjects.get(i)).getLink();

      menuManager.add(new CopyED2KLinkToClipboardAction(false, linkList));
      menuManager.add(new CopyED2KLinkToClipboardAction(true, linkList));
    }
  }

}

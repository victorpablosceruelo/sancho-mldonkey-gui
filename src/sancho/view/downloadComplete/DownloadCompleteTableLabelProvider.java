/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.downloadComplete;

import org.eclipse.swt.graphics.Image;

import sancho.view.utility.SResources;
import sancho.view.viewer.table.GTableLabelProvider;

public class DownloadCompleteTableLabelProvider extends GTableLabelProvider {
  public DownloadCompleteTableLabelProvider(DownloadCompleteTableView fTableView) {
    super(fTableView);
  }

  public Image getColumnImage(Object element, int columnIndex) {

    switch (cViewer.getColumnIDs()[columnIndex]) {
      default :
        return null;
    }
  }

  public String getColumnText(Object element, int columnIndex) {
    DownloadCompleteItem item = (DownloadCompleteItem) element;

    switch (cViewer.getColumnIDs()[columnIndex]) {
      case DownloadCompleteTableView.NAME :
        return item.getName();
      case DownloadCompleteTableView.SIZE :
        return item.getSizeString();
      case DownloadCompleteTableView.HASH :
        return item.getHash();
      case DownloadCompleteTableView.DATE :
        return item.getDateString();
      default :
        return SResources.S_ES;
    }
  }
}

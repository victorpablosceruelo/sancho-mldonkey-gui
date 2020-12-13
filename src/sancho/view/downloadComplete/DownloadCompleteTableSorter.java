/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.downloadComplete;

import org.eclipse.jface.viewers.Viewer;

import sancho.view.viewer.GSorter;

public class DownloadCompleteTableSorter extends GSorter {
  public DownloadCompleteTableSorter(DownloadCompleteTableView fTableView) {
    super(fTableView);
    setLastSort(true);
  }

  public boolean sortOrder(int columnIndex) {
    return true;
  }

  public int compare(Viewer viewer, Object obj1, Object obj2) {
    DownloadCompleteItem item1 = (DownloadCompleteItem) obj1;
    DownloadCompleteItem item2 = (DownloadCompleteItem) obj2;

    switch (cViewer.getColumnIDs()[columnIndex]) {

      case DownloadCompleteTableView.NAME :
        return compareStrings(item1.getName(), item2.getName());
      case DownloadCompleteTableView.SIZE :
        return compareLongs(item1.getSize(), item2.getSize());
      case DownloadCompleteTableView.HASH :
        return compareStrings(item1.getHash(), item2.getHash());
      case DownloadCompleteTableView.DATE :
        return compareLongs(item1.getDateLong(), item2.getDateLong());
      default :
        return 0;
    }
  }
}

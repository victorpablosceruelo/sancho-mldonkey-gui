/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.shares;

import org.eclipse.jface.viewers.Viewer;

import sancho.model.mldonkey.SharedFile;
import sancho.view.viewer.GSorter;

public class UploadTableSorter extends GSorter {
  public UploadTableSorter(UploadTableView uTableViewer) {
    super(uTableViewer);
  }

  public boolean sortOrder(int columnIndex) {
    switch (cViewer.getColumnIDs()[columnIndex]) {
      case UploadTableView.BYTES :
      case UploadTableView.REQUESTS :
        return false;
      default :
        return true;
    }
  }

  public int compare(Viewer viewer, Object obj1, Object obj2) {
    SharedFile sharedFile1 = (SharedFile) obj1;
    SharedFile sharedFile2 = (SharedFile) obj2;

    switch (cViewer.getColumnIDs()[columnIndex]) {
      case UploadTableView.NETWORK :
        return compareStrings(sharedFile1.getNetworkName(), // NPE?
            sharedFile2.getNetworkName());
      case UploadTableView.BYTES :
        return compareLongs(sharedFile1.getBytesUploaded(), sharedFile2.getBytesUploaded());
      case UploadTableView.REQUESTS :
        return compareLongs(sharedFile1.getRequests(), sharedFile2.getRequests());
      case UploadTableView.NAME :
        return compareStrings(sharedFile1.getName(), sharedFile2.getName());
      case UploadTableView.SIZE :
        return compareLongs(sharedFile1.getSize(), sharedFile2.getSize());
      default :
        return 0;
    }
  }
}
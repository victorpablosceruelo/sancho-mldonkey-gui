/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.shares;

import org.eclipse.swt.graphics.Image;

import sancho.model.mldonkey.SharedFile;
import sancho.view.utility.SResources;
import sancho.view.viewer.table.GTableLabelProvider;

public class UploadTableLabelProvider extends GTableLabelProvider {
  public UploadTableLabelProvider(UploadTableView uTableViewer) {
    super(uTableViewer);
  }

  public Image getColumnImage(Object element, int columnIndex) {
    switch (cViewer.getColumnIDs()[columnIndex]) {
      case UploadTableView.NETWORK :
        SharedFile sharedFile = (SharedFile) element;
        return sharedFile.getNetworkImage();
      default :
        return null;
    }
  }

  public String getColumnText(Object element, int columnIndex) {
    SharedFile sharedFile = (SharedFile) element;

    switch (cViewer.getColumnIDs()[columnIndex]) {
      case UploadTableView.NETWORK :
        return sharedFile.getNetworkName();
      case UploadTableView.BYTES :
        return sharedFile.getUploadedString();
      case UploadTableView.REQUESTS :
        return String.valueOf(sharedFile.getRequests());
      case UploadTableView.NAME :
        return sharedFile.getName();
      case UploadTableView.SIZE :
        return sharedFile.getSizeString();
      default :
        return SResources.S_ES;
    }
  }
}
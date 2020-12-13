/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.transfer.uploaders;

import org.eclipse.swt.graphics.Image;

import sancho.model.mldonkey.Client;
import sancho.view.utility.SResources;
import sancho.view.viewer.table.GTableLabelProvider;

public class UploadersTableLabelProvider extends GTableLabelProvider {
  public UploadersTableLabelProvider(UploadersTableView uTableViewer) {
    super(uTableViewer);
  }

  public Image getColumnImage(Object element, int columnIndex) {
    Client client = (Client) element;

    switch (cViewer.getColumnIDs()[columnIndex]) {
      case UploadersTableView.STATE :
        return client.getStateEnum().getImage();

      case UploadersTableView.NETWORK :
        return client.getEnumNetwork().getImage();

      case UploadersTableView.SOFTWARE :
        return client.getSoftwareImage();

      default :
        return null;
    }
  }

  public String getColumnText(Object element, int columnIndex) {
    Client client = (Client) element;

    switch (cViewer.getColumnIDs()[columnIndex]) {
      case UploadersTableView.STATE :
        return client.getDetailedClientActivity();

      case UploadersTableView.NAME :
        return client.getName();

      case UploadersTableView.NETWORK :
        return client.getEnumNetwork().getName();

      case UploadersTableView.KIND :
        return client.getModeString();

      case UploadersTableView.SOFTWARE :
        return client.getSoftware();

      case UploadersTableView.UPLOADED :
        return client.getUploadedString();

      case UploadersTableView.CONNECT_TIME :
        return client.getConnectedTimeString();

      case UploadersTableView.DOWNLOADED :
        return client.getDownloadedString();

      case UploadersTableView.SOCK_ADDR :
        return client.getAddr().toString();

      case UploadersTableView.PORT :
        return String.valueOf(client.getPort());

      case UploadersTableView.FILENAME :
        return client.getUploadFilename();

      default :
        return SResources.S_ES;
    }
  }
}
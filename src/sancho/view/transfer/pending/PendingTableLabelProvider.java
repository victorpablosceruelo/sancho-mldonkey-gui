/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.transfer.pending;

import org.eclipse.swt.graphics.Image;

import sancho.model.mldonkey.Client;
import sancho.view.utility.SResources;
import sancho.view.viewer.table.GTableLabelProvider;

public class PendingTableLabelProvider extends GTableLabelProvider {
  public PendingTableLabelProvider(PendingTableView uTableViewer) {
    super(uTableViewer);
  }

  public Image getColumnImage(Object element, int columnIndex) {
    Client client = (Client) element;

    switch (cViewer.getColumnIDs()[columnIndex]) {
      case PendingTableView.STATE :
        return client.getStateEnum().getImage();

      case PendingTableView.NETWORK :
        return client.getEnumNetwork().getImage();

      case PendingTableView.SOFTWARE :
        return client.getSoftwareImage();

      default :
        return null;
    }
  }

  public String getColumnText(Object element, int columnIndex) {
    Client client = (Client) element;

    switch (cViewer.getColumnIDs()[columnIndex]) {
      case PendingTableView.STATE :
        return client.getDetailedClientActivity();

      case PendingTableView.NAME :
        return client.getName();

      case PendingTableView.NETWORK :
        return client.getEnumNetwork().getName();

      case PendingTableView.KIND :
        return client.getModeString();

      case PendingTableView.SOFTWARE :
        return client.getSoftware();

      case PendingTableView.UPLOADED :
        return client.getUploadedString();

      case PendingTableView.CONNECT_TIME :
        return client.getConnectedTimeString();

      case PendingTableView.DOWNLOADED :
        return client.getDownloadedString();

      case PendingTableView.SOCK_ADDR :
        return client.getAddr().toString();

      case PendingTableView.PORT :
        return String.valueOf(client.getPort());

      case PendingTableView.FILENAME :
        return client.getUploadFilename();

      default :
        return SResources.S_ES;
    }
  }
}

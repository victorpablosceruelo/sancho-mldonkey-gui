/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.transfer.clients;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import sancho.model.mldonkey.Client;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.utility.SResources;
import sancho.view.viewer.table.GTableLabelProvider;

public class ClientTableLabelProvider extends GTableLabelProvider implements IColorProvider {

  public static final String RS_TRUE = SResources.getString("l.true");
  public static final String RS_FALSE = SResources.getString("l.false");

  boolean displayColors;
  Color hasFilesColor;
  Color connectedColor;
  Color disconnectedColor;

  public ClientTableLabelProvider(ClientTableView cTableViewer) {
    super(cTableViewer);
  }

  public Image getColumnImage(Object element, int columnIndex) {
    Client client = (Client) element;

    switch (cViewer.getColumnIDs()[columnIndex]) {
      case ClientTableView.STATE :
        return client.getStateEnum().getImage();

      case ClientTableView.NETWORK :
        return client.getEnumNetwork().getImage();

      case ClientTableView.SOFTWARE :
        return client.getSoftwareImage();

      default :
        return null;
    }
  }

  public String getColumnText(Object element, int columnIndex) {
    Client client = (Client) element;

    switch (cViewer.getColumnIDs()[columnIndex]) {
      case ClientTableView.STATE :
        return client.getDetailedClientActivity();

      case ClientTableView.NAME :
        return client.getName();

      case ClientTableView.NETWORK :
        return client.getEnumNetwork().getName();

      case ClientTableView.KIND :
        return client.getModeString();

      case ClientTableView.SOFTWARE :
        return client.getSoftware();

      case ClientTableView.UPLOADED :
        return client.getUploadedString();

      case ClientTableView.DOWNLOADED :
        return client.getDownloadedString();

      case ClientTableView.SOCK_ADDR :
        return client.getAddr().toString();

      case ClientTableView.PORT :
        return String.valueOf(client.getPort());

      case ClientTableView.CONNECT_TIME :
        return client.getConnectedTimeString();

      case ClientTableView.HAS_FILES :
        return client.hasFiles() ? RS_TRUE : RS_FALSE;

      default :
        return SResources.S_ES;
    }
  }

  public Color getForeground(Object element) {
    if (!displayColors)
      return null;

    Client client = (Client) element;

    if (client.hasFiles())
      return hasFilesColor;
    else if (client.isConnected())
      return connectedColor;
    else
      return disconnectedColor;

  }

  public Color getBackground(Object element) {
    return null;
  }

  public void updateDisplay() {
    super.updateDisplay();
    displayColors = PreferenceLoader.loadBoolean("displayTableColors");
    connectedColor = PreferenceLoader.loadColor("clientsConnectedColor");
    hasFilesColor = PreferenceLoader.loadColor("clientsHasFilesColor");
    disconnectedColor = PreferenceLoader.loadColor("clientsDisconnectedColor");
  }

}
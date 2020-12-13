/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.friends;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import sancho.model.mldonkey.Client;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.utility.SResources;
import sancho.view.viewer.table.GTableLabelProvider;

public class FriendsTableLabelProvider extends GTableLabelProvider implements IColorProvider {

  boolean colors;
  Color hasFilesColor;
  Color connectedColor;
  Color disconnectedColor;

  public static final String RS_TRUE = SResources.getString("l.true");
  public static final String RS_FALSE = SResources.getString("l.false");

  public FriendsTableLabelProvider(FriendsTableView fTableView) {
    super(fTableView);
  }

  public Image getColumnImage(Object element, int columnIndex) {
    Client client = (Client) element;

    switch (cViewer.getColumnIDs()[columnIndex]) {
      case FriendsTableView.NAME :
        if (client.isConnected())
          return SResources.getImage(client.hasFiles() ? "FriendsButtonSmallPlus" : "tab.friends.buttonSmall");
        else
          return SResources.getImage(client.hasFiles() ? "FriendsButtonSmallBWPlus" : "FriendsButtonSmallBW");

      case FriendsTableView.STATE :
        return client.getStateEnum().getImage();

      case FriendsTableView.NETWORK :
        return client.getEnumNetwork().getImage();

      case FriendsTableView.SOFTWARE :
        return client.getSoftwareImage();

      default :
        return null;
    }
  }

  public String getColumnText(Object element, int columnIndex) {
    Client client = (Client) element;

    switch (cViewer.getColumnIDs()[columnIndex]) {
      case FriendsTableView.NAME :
        return client.getName();

      case FriendsTableView.STATE :
        return client.getDetailedClientActivity();

      case FriendsTableView.NETWORK :
        return client.getEnumNetwork().getName();

      case FriendsTableView.KIND :
        return client.getModeString();

      case FriendsTableView.SOFTWARE :
        return client.getSoftware();

      case FriendsTableView.UPLOADED :
        return client.getUploadedString();

      case FriendsTableView.DOWNLOADED :
        return client.getDownloadedString();

      case FriendsTableView.SOCK_ADDR :
        return client.getAddr().toString();

      case FriendsTableView.PORT :
        return String.valueOf(client.getPort());

      case FriendsTableView.CONNECT_TIME :
        return client.getConnectedTimeString();

      case FriendsTableView.HAS_FILES :
        return client.hasFiles() ? RS_TRUE : RS_FALSE;

      default :
        return "?";
    }
  }

  public Color getForeground(Object element) {
    if (!colors)
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
    colors = PreferenceLoader.loadBoolean("displayTableColors");
    connectedColor = PreferenceLoader.loadColor("clientsConnectedColor");
    hasFilesColor = PreferenceLoader.loadColor("clientsHasFilesColor");
    disconnectedColor = PreferenceLoader.loadColor("clientsDisconnectedColor");
  }
}
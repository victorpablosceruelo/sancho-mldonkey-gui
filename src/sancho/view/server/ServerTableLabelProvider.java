/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.server;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import sancho.model.mldonkey.Server;
import sancho.model.mldonkey.enums.EnumHostState;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.utility.SResources;
import sancho.view.viewer.table.GTableLabelProvider;

public class ServerTableLabelProvider extends GTableLabelProvider implements IColorProvider {

  private boolean colors;
  private Color connectedColor;
  private Color connectingColor;
  private Color disconnectColor;

  public ServerTableLabelProvider(ServerTableView sTableViewer) {
    super(sTableViewer);
  }

  public Image getColumnImage(Object element, int columnIndex) {
    switch (cViewer.getColumnIDs()[columnIndex]) {
      case ServerTableView.NETWORK :
        Server server = (Server) element;
        return server.getNetworkImage();
      default :
        return null;
    }
  }

  public String getColumnText(Object element, int columnIndex) {
    Server server = (Server) element;

    switch (cViewer.getColumnIDs()[columnIndex]) {
      case ServerTableView.NETWORK :
        return server.getNetworkName();

      case ServerTableView.NAME :
        return server.getName();

      case ServerTableView.DESCRIPTION :
        return server.getDescription();

      case ServerTableView.ADDRESS :
        return server.getAddr().toString();

      case ServerTableView.PORT :
        return String.valueOf(server.getPort());

      case ServerTableView.SCORE :
        return String.valueOf(server.getScore());

      case ServerTableView.USERS :
        return String.valueOf(server.getNumUsers());

      case ServerTableView.FILES :
        return String.valueOf(server.getNumFiles());

      case ServerTableView.STATE :
        return server.getStateEnum().getName();

      case ServerTableView.PREFERRED :
        return server.getPreferredString();

      case ServerTableView.HIGH_ID :
        return server.getHighLowIDString();

      default :
        return SResources.S_ES;
    }
  }

  public Color getForeground(Object arg0) {
    if (!colors)
      return null;

    Server server = (Server) arg0;

    if (server.isConnected())
      return connectedColor;
    else if (server.getStateEnum() == EnumHostState.CONNECTING)
      return connectingColor;
    else if (server.getStateEnum() == EnumHostState.NOT_CONNECTED)
      return disconnectColor;

    return null;
  }

  public Color getBackground(Object element) {
    return null;
  }

  public void updateDisplay() {
    super.updateDisplay();
    colors = PreferenceLoader.loadBoolean("displayTableColors");
    connectedColor = PreferenceLoader.loadColor("serverConnectedColor");
    connectingColor = PreferenceLoader.loadColor("serverConnectingColor");
    disconnectColor = PreferenceLoader.loadColor("serverDisconnectedColor");
  }
}
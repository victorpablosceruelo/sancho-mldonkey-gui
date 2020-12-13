/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.transfer.clients;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.SelectionChangedEvent;

import sancho.model.mldonkey.Client;
import sancho.model.mldonkey.File;
import sancho.view.viewer.CustomTableViewer;
import sancho.view.viewer.actions.AddClientAsFriendAction;
import sancho.view.viewer.actions.ClientDetailAction;
import sancho.view.viewer.actions.ClientFilesDialogAction;
import sancho.view.viewer.actions.ConnectClientAction;
import sancho.view.viewer.actions.DisconnectClientAction;
import sancho.view.viewer.actions.GetClientFilesAction;
import sancho.view.viewer.table.GTableMenuListener;

public class ClientTableMenuListener extends GTableMenuListener {

  public ClientTableMenuListener(ClientTableView cTableViewer) {
    super(cTableViewer);
  }

  public void selectionChanged(SelectionChangedEvent event) {
    collectSelections(event, Client.class);
  }

  public void menuAboutToShow(IMenuManager menuManager) {
    if (selectedObjects.size() > 0) {
      Client[] clientArray = new Client[selectedObjects.size()];

      List connected = new ArrayList();
      List disconnected = new ArrayList();

      for (int i = 0; i < selectedObjects.size(); i++) {
        Client client = (Client) selectedObjects.get(i);
        if (client.isConnected())
          connected.add(client);
        else
          disconnected.add(client);
        clientArray[i] = client;
      }

      Client[] connectedClients = new Client[connected.size()];
      for (int i = 0; i < connected.size(); i++)
        connectedClients[i] = (Client) connected.get(i);

      Client[] disconnectedClients = new Client[disconnected.size()];
      for (int i = 0; i < disconnected.size(); i++)
        disconnectedClients[i] = (Client) disconnected.get(i);

      CustomTableViewer cv = (CustomTableViewer) gView.getViewer();
      Object input = cv.getInput();

      if (!(input instanceof File)) {
        input = null;
      }

      menuManager.add(new ClientDetailAction(gView.getShell(), (File) input, (Client) selectedObjects.get(0),
          gView.getCore()));
      menuManager.add(new AddClientAsFriendAction(clientArray));
      menuManager.add(new GetClientFilesAction(clientArray));
      if (clientArray[0].hasFiles())
        menuManager.add(new ClientFilesDialogAction(gView.getShell(), clientArray[0]));

      if (connectedClients.length > 0)
        menuManager.add(new DisconnectClientAction(clientArray));

      if (disconnectedClients.length > 0)
        menuManager.add(new ConnectClientAction(clientArray));

    }
  }
}
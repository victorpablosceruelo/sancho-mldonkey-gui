/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.friends;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.SelectionChangedEvent;

import sancho.model.mldonkey.Client;
import sancho.model.mldonkey.ClientCollection;
import sancho.view.FriendsTab;
import sancho.view.friends.clientDirectories.ClientDirectoriesTableView;
import sancho.view.utility.SResources;
import sancho.view.viewer.actions.ClientDetailAction;
import sancho.view.viewer.actions.ConnectClientAction;
import sancho.view.viewer.actions.DisconnectClientAction;
import sancho.view.viewer.actions.GetClientFilesAction;
import sancho.view.viewer.table.GTableMenuListener;

public class FriendsTableMenuListener extends GTableMenuListener {
  private ClientDirectoriesTableView clientDirectoriesTableView;

  public FriendsTableMenuListener(FriendsTableView fTableView) {
    super(fTableView);
  }

  public void selectionChanged(SelectionChangedEvent event) {
    collectSelections(event, Client.class);
    if (selectedObjects.size() > 0)
      clientDirectoriesTableView.setInput(selectedObjects.get(0));
  }

  public void setDirectoriesView(ClientDirectoriesTableView cTDV) {
    clientDirectoriesTableView = cTDV;
  }

  public void menuAboutToShow(IMenuManager menuManager) {
    if (selectedObjects.size() > 0) {
      menuManager.add(new SendMessageAction());

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

      menuManager.add(new ClientDetailAction(gView.getShell(), null, (Client) selectedObjects.get(0), gView
          .getCore()));

      menuManager.add(new GetClientFilesAction(clientArray));
      menuManager.add(new RemoveFriendAction());
      if (connectedClients.length > 0)
        menuManager.add(new DisconnectClientAction(clientArray));

      if (disconnectedClients.length > 0)
        menuManager.add(new ConnectClientAction(clientArray));

    }

    if (gView.getTable().getItemCount() > 0)
      menuManager.add(new RemoveAllFriendsAction());

    menuManager.add(new AddByIPAction());
  }

  /**
   * RemoveFriendAction
   */
  private class RemoveFriendAction extends Action {
    public RemoveFriendAction() {
      super();

      String num = ((selectedObjects.size() > 1) ? (" (" + selectedObjects.size() + ")") : SResources.S_ES);
      setText(SResources.getString("mi.f.removeFriend") + num);
      setImageDescriptor(SResources.getImageDescriptor("FriendsButtonSmallBW"));
    }

    public void run() {
      for (int i = 0; i < selectedObjects.size(); i++) {
        Client client = (Client) selectedObjects.get(i);
        client.removeAsFriend();
      }
    }
  }

  /**
   * RemoveAllFriendsAction
   */
  private class RemoveAllFriendsAction extends Action {
    public RemoveAllFriendsAction() {
      super(SResources.getString("mi.f.removeAllFriends"));
      setImageDescriptor(SResources.getImageDescriptor("FriendsButtonSmallBW"));
    }

    public void run() {
      ClientCollection.removeAllFriends(gView.getCore());
    }
  }

  /**
   * SendMessageAction
   */
  private class SendMessageAction extends Action {
    public SendMessageAction() {
      super();

      String num = ((selectedObjects.size() > 1) ? (" (" + selectedObjects.size() + ")") : SResources.S_ES);
      setText(SResources.getString("mi.f.sendMessage") + num);
      setImageDescriptor(SResources.getImageDescriptor("resume"));
    }

    public void run() {
      for (int i = 0; i < selectedObjects.size(); i++) {
        Client client = (Client) selectedObjects.get(i);
        FriendsTab messagesTab = (FriendsTab) gView.getViewFrame().getGuiTab();
        messagesTab.openTab(client);
      }
    }
  }

  /**
   * AddByIPAction
   */
  private class AddByIPAction extends Action {
    public AddByIPAction() {
      super(SResources.getString("mi.f.addByIP"));
      setImageDescriptor(SResources.getImageDescriptor("tab.friends.buttonSmall"));
    }

    public void run() {
      new AddFriendDialog(gView.getShell(), gView.getCore()).open();
    }
  }
}
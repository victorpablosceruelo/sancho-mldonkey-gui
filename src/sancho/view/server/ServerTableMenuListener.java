/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.server;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.SelectionChangedEvent;

import sancho.core.Sancho;
import sancho.model.mldonkey.Server;
import sancho.model.mldonkey.enums.EnumHostState;
import sancho.view.MainWindow;
import sancho.view.server.users.ServerUsersTableView;
import sancho.view.utility.SResources;
import sancho.view.viewer.table.GTableMenuListener;

public class ServerTableMenuListener extends GTableMenuListener {
  private ServerUsersTableView serverUsersTableView;

  public ServerTableMenuListener(ServerTableView sTableViewer) {
    super(sTableViewer);
  }

  public void menuAboutToShow(IMenuManager menuManager) {
    if ((selectedObjects.size() > 0) && ((Server) selectedObjects.get(0)).isConnected())
      menuManager.add(new DisconnectAction());

    if ((selectedObjects.size() > 0)
        && ((Server) selectedObjects.get(0)).getStateEnum() == EnumHostState.NOT_CONNECTED)
      menuManager.add(new ConnectAction());

    if (selectedObjects.size() > 0) {
      menuManager.add(new ConnectMoreAction());
      menuManager.add(new GetServerUsersAction());
      menuManager.add(new CopyServerLink());
      menuManager.add(new RemoveServerAction());
      menuManager.add(new BlackListAction());

      if (gView.getCore() != null && gView.getCore().getProtocol() > 27)
        menuManager.add(new PreferredServerAction());

    }
  }

  public void selectionChanged(SelectionChangedEvent event) {
    collectSelections(event, Server.class);
    if (selectedObjects.size() > 0)
      serverUsersTableView.setInput((Server) selectedObjects.get(0));
  }

  public void setServerUsersTableView(ServerUsersTableView sUTV) {
    this.serverUsersTableView = sUTV;
  }

  private class BlackListAction extends Action {
    public BlackListAction() {
      super(SResources.getString("m.srv.blacklist"));
      setImageDescriptor(SResources.getImageDescriptor("gun"));
    }

    public void run() {
      for (int i = 0; i < selectedObjects.size(); i++)
        ((Server) selectedObjects.get(i)).blacklist();
    }
  }

  private class ConnectAction extends Action {
    public ConnectAction() {
      super(SResources.getString("m.srv.connect"));
      setImageDescriptor(SResources.getImageDescriptor("menu-connect"));
    }

    public void run() {
      if (!Sancho.hasCollectionFactory())
        return;

      for (int i = 0; i < selectedObjects.size(); i++)
        ((Server) selectedObjects.get(i)).connect();
    }
  }

  private class ConnectMoreAction extends Action {
    public ConnectMoreAction() {
      super(SResources.getString("m.srv.connectMore"));
      setImageDescriptor(SResources.getImageDescriptor("plus"));
    }

    public void run() {
      if (!Sancho.hasCollectionFactory())
        return;
      gView.getCore().getServerCollection().connectMore();
    }
  }

  private class CopyServerLink extends Action {
    public CopyServerLink() {
      super(SResources.getString("m.srv.copyTo"));
      setImageDescriptor(SResources.getImageDescriptor("copy"));
    }

    public void run() {
      String links = SResources.S_ES;
      String lSeparator = System.getProperty("line.separator");

      for (int i = 0; i < selectedObjects.size(); i++) {
        Server server = (Server) selectedObjects.get(i);
        if (links.length() > 0)
          links += lSeparator;
        links += server.getLink();
      }
      MainWindow.copyToClipboard(links);
    }
  }

  private class DisconnectAction extends Action {
    public DisconnectAction() {
      super(SResources.getString("m.srv.disconnect"));
      setImageDescriptor(SResources.getImageDescriptor("menu-disconnect"));
    }

    public void run() {
      for (int i = 0; i < selectedObjects.size(); i++)
        ((Server) selectedObjects.get(i)).disconnect();
    }
  }

  private class GetServerUsersAction extends Action {
    public GetServerUsersAction() {
      super(SResources.getString("m.srv.getServerUsers"));
      setImageDescriptor(SResources.getImageDescriptor("rotate"));
    }

    public void run() {
      for (int i = 0; i < selectedObjects.size(); i++)
        ((Server) selectedObjects.get(i)).getServerUsers();
    }
  }

  private class PreferredServerAction extends Action {

    public PreferredServerAction() {
      super(SResources.getString("m.srv.preferredServer"));
      setImageDescriptor(SResources.getImageDescriptor("heart"));
    }

    public void run() {
      for (int i = 0; i < selectedObjects.size(); i++)
        ((Server) selectedObjects.get(i)).togglePreferred();
    }
  }

  private class RemoveServerAction extends Action {
    public RemoveServerAction() {
      super(SResources.getString("m.srv.removeServer"));
      setImageDescriptor(SResources.getImageDescriptor("minus"));
    }

    public void run() {
      for (int i = 0; i < selectedObjects.size(); i++)
        ((Server) selectedObjects.get(i)).remove();
    }
  }

}
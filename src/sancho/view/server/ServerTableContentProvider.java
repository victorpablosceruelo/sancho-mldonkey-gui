/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.server;

import java.util.Observable;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.TableItem;

import sancho.model.mldonkey.Server;
import sancho.model.mldonkey.ServerCollection;
import sancho.model.mldonkey.enums.EnumHostState;
import sancho.view.utility.SResources;
import sancho.view.viewer.CustomTableViewer;
import sancho.view.viewer.actions.AbstractFilterAction;
import sancho.view.viewer.table.GTableContentProvider;

public class ServerTableContentProvider extends GTableContentProvider {

  private static final String RS_SERVERS = SResources.getString("tab.servers");
  private static final String RS_CONNECTED = SResources.getString("l.connected");

  public ServerTableContentProvider(ServerTableView sTableViewer) {
    super(sTableViewer);
  }

  public Object[] getElements(Object inputElement) {
    synchronized (inputElement) {
      ((ServerCollection) inputElement).clearAllLists();
      Object[] oArray = ((ServerCollection) inputElement).getServers();
      return oArray;
    }
  }

  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    super.inputChanged(viewer, oldInput, newInput);

    if (oldInput != null)
      ((Observable) oldInput).deleteObserver(this);

    if (newInput != null) {
      if (gView.isActive()) {
        ((Observable) newInput).addObserver(this);
        updateLabel((ServerCollection) newInput);
      }
    }
  }

  public void setActive(boolean b) {
    ServerCollection serverCollection;

    if (b) {
      if ((serverCollection = (ServerCollection) gView.getViewer().getInput()) != null) {
        serverCollection.addObserver(this);
        updateServerTable(serverCollection);
      }
    } else {
      if ((serverCollection = (ServerCollection) gView.getViewer().getInput()) != null)
        serverCollection.deleteObserver(this);
    }
  }

  public void update(final Observable o, Object obj) {
    if ((gView.getViewer() == null) || gView.getTable().isDisposed())
      return;

    if (obj instanceof ServerCollection)
      gView.getTable().getDisplay().asyncExec(new Runnable() { // sync
            public void run() {
              if ((gView.getViewer() == null) || gView.getTable().isDisposed())
                return;
              gView.refresh();
            }
          });

    else if (o instanceof ServerCollection)
      gView.getTable().getDisplay().asyncExec(new Runnable() {
        public void run() {
          updateServerTable((ServerCollection) o);
        }
      });
  }

  public void updateServerTable(ServerCollection serverCollection) {
    if ((gView == null) || gView.isDisposed())
      return;

    if (serverCollection.removed()) {
      synchronized (serverCollection) {
        ((TableViewer) gView.getViewer()).remove(serverCollection.getRemovedArray());
        serverCollection.clearRemoved();
      }
    }

    if (serverCollection.added()) {
      synchronized (serverCollection) {
        Object[] oArray = serverCollection.getAddedArray();
        if (oArray.length < 99) {
          ((TableViewer) gView.getViewer()).add(oArray);
          serverCollection.clearAdded();
        } else
          gView.refresh();
      }
    }

    if (serverCollection.updated()) {
      synchronized (serverCollection) {
        Object[] oArray = serverCollection.getUpdatedArray();
        if (oArray.length > 99) {
          gView.refresh();
        } else if (AbstractFilterAction.isStateFilteredExcept(this.gView, EnumHostState.NOT_CONNECTED)) {
          if (requiresRefresh(oArray, gView.getTable().getItems()))
            tableViewer.refresh();
          else {
            ((CustomTableViewer) gView.getViewer()).update(serverCollection.getUpdatedArray(),
                SResources.SA_Z);
          }
        } else {
          ((CustomTableViewer) gView.getViewer()).update(serverCollection.getUpdatedArray(), null);
        }

        serverCollection.clearUpdated();
      }
    }
    updateLabel(serverCollection);
  }

  protected boolean requiresRefresh(Object[] serverArray, TableItem[] tableItemArray) {
    for (int i = 0; i < serverArray.length; i++) {
      Server server = (Server) serverArray[i];
      if (!server.isDisconnected() && !containsServerInfo(server, tableItemArray))
        return true;
    }
    return false;
  }

  private boolean containsServerInfo(Server server, TableItem[] tableItemArray) {
    for (int i = 0; i < tableItemArray.length; i++)
      if (!tableItemArray[i].isDisposed() && server == (Server) tableItemArray[i].getData())
        return true;
    return false;
  }

  protected void updateLabel(ServerCollection serverCollection) {
    gView.getViewFrame().updateCLabelText(
        RS_SERVERS + SResources.S_COLON + serverCollection.getConnected() + SResources.S_SLASH2
            + serverCollection.size() + SResources.S_SPACE + RS_CONNECTED);
  }
}
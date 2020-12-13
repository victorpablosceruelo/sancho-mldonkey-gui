/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.friends.clientDirectories;

import java.util.Map;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.SelectionChangedEvent;

import sancho.model.mldonkey.Client;
import sancho.view.friends.clientFiles.ClientFilesTableView;
import sancho.view.viewer.table.GTableMenuListener;

public class ClientDirectoriesTableMenuListener extends GTableMenuListener {

  private ClientFilesTableView clientFilesTableView;

  public ClientDirectoriesTableMenuListener(ClientDirectoriesTableView rTableViewer) {
    super(rTableViewer);
  }

  public void setDirectoriesView(ClientFilesTableView cFDV) {
    clientFilesTableView = cFDV;
  }

  public void selectionChanged(SelectionChangedEvent event) {
    collectSelections(event, String.class);

    if (selectedObjects.size() > 0) {
      Client client = (Client) gView.getViewer().getInput();
      Map map = client.getClientFilesResultMap(selectedObjects.get(0));
      clientFilesTableView.setInput(map);
    } else {
      clientFilesTableView.setInput(null);
    }
  }

  public void setFilesView(ClientFilesTableView cFTV) {
    clientFilesTableView = cFTV;
  }

  public void menuAboutToShow(IMenuManager menuManager) {
  }

}

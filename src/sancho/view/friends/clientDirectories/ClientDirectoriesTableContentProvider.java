/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.friends.clientDirectories;

import org.eclipse.jface.viewers.Viewer;

import sancho.model.mldonkey.Client;
import sancho.view.friends.clientFiles.ClientFilesTableView;
import sancho.view.viewer.table.GTableContentProvider;

public class ClientDirectoriesTableContentProvider extends GTableContentProvider {

  ClientFilesTableView clientFilesTableView;

  public ClientDirectoriesTableContentProvider(ClientDirectoriesTableView rTableViewer) {
    super(rTableViewer);
  }

  public Object[] getElements(Object inputElement) {
    if (inputElement instanceof Client) {
      Client client = (Client) inputElement;
      if (client.hasFiles())
        return client.getFileDirectories();
    }
    return EMPTY_ARRAY;
  }

  public void setFilesView(ClientFilesTableView cFTV) {
    clientFilesTableView = cFTV;
  }

  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

    if (clientFilesTableView != null)
      clientFilesTableView.setInput(null);

    if (newInput != null) {
      Client client = (Client) newInput;
      if (client.hasFiles()) {
        clientFilesTableView.setInput(client.getFirstResultMap());
      }
    }

    super.inputChanged(viewer, oldInput, newInput);
  }

}

/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.server.users;

import org.eclipse.swt.custom.CLabel;

import sancho.model.mldonkey.Server;
import sancho.view.viewer.table.GTableContentProvider;

public class ServerUsersTableContentProvider extends GTableContentProvider {

  public ServerUsersTableContentProvider(ServerUsersTableView cTableViewer, CLabel headerCLabel) {
    super(cTableViewer);
  }

  public Object[] getElements(Object inputElement) {
    if (inputElement instanceof Server) {
      Server server = (Server) inputElement;
      if (server.hasUsers()) {
        return server.getUsers();
      }
    }
    return EMPTY_ARRAY;
  }
 
}

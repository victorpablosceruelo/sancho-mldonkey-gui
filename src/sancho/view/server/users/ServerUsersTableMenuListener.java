/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.server.users;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;

import sancho.view.viewer.table.GTableMenuListener;

public class ServerUsersTableMenuListener extends GTableMenuListener
    implements
      ISelectionChangedListener,
      IMenuListener {

  public ServerUsersTableMenuListener(ServerUsersTableView cTableViewer) {
    super(cTableViewer);
  }

  public void selectionChanged(SelectionChangedEvent event) {
  }

  public void menuAboutToShow(IMenuManager menuManager) {
  }
}
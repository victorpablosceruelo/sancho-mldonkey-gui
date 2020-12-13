/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewer.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Shell;

import sancho.model.mldonkey.Client;
import sancho.view.transfer.ClientFilesDialog;
import sancho.view.utility.SResources;

public class ClientFilesDialogAction extends Action {

  Shell shell;
  Client client;

  public ClientFilesDialogAction(Shell shell, Client client) {
    super(SResources.getString("mi.viewClientFiles"));
    setImageDescriptor(SResources.getImageDescriptor("search_small"));
    this.shell = shell;
    this.client = client;
  }

  public void run() {
    new ClientFilesDialog(shell, client).open();
  }
}
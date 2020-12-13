/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewer.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Shell;

import sancho.core.ICore;
import sancho.model.mldonkey.Client;
import sancho.model.mldonkey.File;
import sancho.view.transfer.ClientDetailDialog;
import sancho.view.utility.SResources;

public class ClientDetailAction extends Action {
  private File file;
  private Client client;
  private ICore core;
  private Shell parentShell;

  public ClientDetailAction(Shell parentShell, File file, Client client, ICore core) {
    super(SResources.getString("m.d.clientDetails"));
    setImageDescriptor(SResources.getImageDescriptor("info"));
    this.parentShell = parentShell;
    this.file = file;
    this.client = client;
    this.core = core;
  }

  public void run() {
    new ClientDetailDialog(parentShell, file, client, core).open();
  }
}

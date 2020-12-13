/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewer.actions;

import org.eclipse.jface.action.Action;

import sancho.model.mldonkey.Client;
import sancho.view.utility.SResources;

public class GetClientFilesAction extends Action {

  Client[] clientArray;

  public GetClientFilesAction(Client[] clientArray) {
    super(SResources.getString("mi.getClientFiles"));
    setImageDescriptor(SResources.getImageDescriptor("rotate"));
    this.clientArray = clientArray;
  }

  public void run() {
    for (int i = 0; i < clientArray.length; i++) {
      if (clientArray[i] != null)
        clientArray[i].requestClientFiles();
    }
  }
}
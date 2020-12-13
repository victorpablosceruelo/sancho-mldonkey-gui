/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewer.actions;

import org.eclipse.jface.action.Action;

import sancho.model.mldonkey.Client;
import sancho.view.utility.SResources;

public class ConnectClientAction extends Action {

  Client[] clientArray;

  public ConnectClientAction(Client[] clientArray) {
    super(SResources.getString("mi.connectClient"));
    setImageDescriptor(SResources.getImageDescriptor("menu-connect"));
    this.clientArray = clientArray;
  }

  public void run() {
    for (int i = 0; i < clientArray.length; i++) {
      clientArray[i].connect();
    }
  }
}

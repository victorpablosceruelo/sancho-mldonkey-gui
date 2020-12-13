/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewer.actions;

import org.eclipse.jface.action.Action;

import sancho.model.mldonkey.Client;
import sancho.view.utility.SResources;

public class DisconnectClientAction extends Action {

  Client[] clientArray;

  public DisconnectClientAction(Client[] clientArray) {
    super(SResources.getString("mi.disconnectClient"));
    setImageDescriptor(SResources.getImageDescriptor("menu-disconnect"));
    this.clientArray = clientArray;
  }

  public void run() {
    for (int i = 0; i < clientArray.length; i++) {
      clientArray[i].disconnect();
    }
  }
}

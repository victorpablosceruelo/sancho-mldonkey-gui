/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.statusline.actions;

import org.eclipse.jface.action.Action;

import sancho.model.mldonkey.Network;
import sancho.view.utility.SResources;

public class NetworkConnectMoreAction extends Action {

  Network network;

  public NetworkConnectMoreAction(Network network) {
    super(SResources.getString("sl.n.connect"));
    setImageDescriptor(SResources.getImageDescriptor("plus"));
    this.network = network;
  }

  public void run() {
    network.getCore().getServerCollection().connectMore();
  }
}

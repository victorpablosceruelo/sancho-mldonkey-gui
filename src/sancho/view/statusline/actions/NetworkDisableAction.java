/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.statusline.actions;

import org.eclipse.jface.action.Action;

import sancho.model.mldonkey.Network;
import sancho.view.utility.SResources;

public class NetworkDisableAction extends Action {
  Network network;

  public NetworkDisableAction(Network network) {
    super(SResources.getString("sl.n.disable"));
    setImageDescriptor(SResources.getImageDescriptor("menu-disconnect"));
    this.network = network;
  }

  public void run() {
    network.toggleEnabled();
  }
}

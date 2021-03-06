/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewer.actions;

import org.eclipse.jface.action.Action;

import sancho.model.mldonkey.Client;
import sancho.view.utility.SResources;

public class AddClientAsFriendAction extends Action {
  private Client[] clientArray;

  public AddClientAsFriendAction(Client[] clientArray) {
    super(SResources.getString("dd.c.addFriend"));
    setImageDescriptor(SResources.getImageDescriptor("tab.friends.buttonSmall"));
    this.clientArray = clientArray;
  }

  public void run() {
    for (int i = 0; i < clientArray.length; i++)
      clientArray[i].addAsFriend();
  }
}

/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewer.actions;

import org.eclipse.jface.action.Action;

import sancho.model.mldonkey.User;
import sancho.view.utility.SResources;

public class AddUserAsFriendAction extends Action {
  private User[] userArray;

  public AddUserAsFriendAction(User[] userArray) {
    super(SResources.getString("dd.c.addFriend"));
    setImageDescriptor(SResources.getImageDescriptor("tab.friends.buttonSmall"));
    this.userArray = userArray;
  }

  public void run() {
    for (int i = 0; i < userArray.length; i++)
      userArray[i].addAsFriend();
  }
}

/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;

import sancho.core.ICore;
import sancho.model.mldonkey.utility.MessageBuffer;

public class UserCollection extends ACollection_Int {

  UserCollection(ICore communication) {
    super(communication);
  }

  public void read(MessageBuffer messageBuffer) {
    int userID = messageBuffer.getInt32();
    User user = (User) get(userID);
    if (user != null) {
      user.read(userID, messageBuffer);
    } else {
      user = core.getCollectionFactory().getUser();
      user.read(userID, messageBuffer);
      put(userID, user);
    }
    this.setChanged();
    this.notifyObservers(user);
  }
}

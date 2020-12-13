/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;

import sancho.core.ICore;
import sancho.model.mldonkey.utility.MessageBuffer;

public class Room3 extends Room {
  private int numUsers;

  Room3(ICore core) {
    super(core);
  }

  public void read(int roomNumber, MessageBuffer messageBuffer) {
    super.read(roomNumber, messageBuffer);
    synchronized (this) {
      this.numUsers = messageBuffer.getInt32();
    }
  }

  public synchronized int getNumUsers() {
    return numUsers;
  }
}
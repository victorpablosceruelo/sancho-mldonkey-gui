/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;

import sancho.core.ICore;
import sancho.model.mldonkey.utility.MessageBuffer;
import sancho.utility.SwissArmy;

public class Client20 extends Client19 {

  protected int connectedTime;

  public Client20(ICore core) {
    super(core);
  }

  public synchronized int getConnectedTime() {
    return connectedTime;
  }

  public synchronized String getConnectedTimeString() {
    return SwissArmy.calcStringOfSeconds(getConnectedTime());
  }

  protected void readMore(MessageBuffer messageBuffer) {
    super.readMore(messageBuffer);
    this.connectedTime = messageBuffer.getInt32();
  }
}
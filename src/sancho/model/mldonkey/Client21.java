/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;

import sancho.core.ICore;
import sancho.model.mldonkey.utility.MessageBuffer;
import sancho.utility.SwissArmy;
import sancho.view.utility.SResources;

public class Client21 extends Client20 {

  protected String emuleMod;

  public Client21(ICore core) {
    super(core);
  }

  public synchronized String getConnectedTimeString() {
    return this.connectedTime > 76340000 ? SResources.S_DASH : SwissArmy.calcStringOfSeconds(connectedTime);
  }

  public synchronized String getSoftware() {
    if (emuleMod != null && emuleMod.length() > 0)
      return super.getSoftware() + SResources.S_OB + emuleMod + SResources.S_CB;
    else
      return super.getSoftware();
  }

  protected void readMore(MessageBuffer messageBuffer) {
    super.readMore(messageBuffer);
    this.emuleMod = messageBuffer.getString();
  }
}
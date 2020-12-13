/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import sancho.core.ICore;
import sancho.core.Sancho;
import sancho.model.mldonkey.enums.EnumNetwork;
import sancho.model.mldonkey.utility.MessageBuffer;

public class File18 extends File {
  private Map availMap;

  File18(ICore core) {
    super(core);
  }

  protected void readAvailability(MessageBuffer messageBuffer) {
    int len = messageBuffer.getUInt16();
    
    if (this.availMap == null)
      this.availMap = new HashMap(len);
    else
      this.availMap.clear();

    boolean foundMultiNet = false;

    String oldAvail = getAvail();

    for (int i = 0; i < len; i++) {
      int networkID = messageBuffer.getInt32();
      Network network = (Network) core.getNetworkCollection().get(networkID);
      if (network == null) {
        String s = messageBuffer.getString();
        Sancho.pDebug("readAvail: " + s);
        continue;
      }

      if (network.getEnumNetwork() == EnumNetwork.MULTINET) {
        this.avail = messageBuffer.getString();
        foundMultiNet = true;
      } else {
        String tempAvail = messageBuffer.getString();
        if (!foundMultiNet)
          this.avail = tempAvail;
        this.availMap.put(network, tempAvail);
      }
    }

    if (oldAvail != null && !oldAvail.equals(getAvail())) {
      addChangedBits(CHANGED_AVAIL);
    }

    setRelativeAvail();
  }

  public boolean hasAvails() {
    return true;
  }

  public synchronized String getAvails(Network network) {
    return (String) availMap.get(network);
  }

  public synchronized Set getAllAvailNetworks() {
    return this.availMap.keySet();
  }
}
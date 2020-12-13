/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;

import sancho.core.ICore;
import sancho.model.mldonkey.utility.MessageBuffer;

public class ClientStats18 extends ClientStats {

  ClientStats18(ICore core) {
    super(core);
  }

  public void readNetworks(MessageBuffer messageBuffer) {
    int len = messageBuffer.getUInt16();

    if (connectedNetworks == null || connectedNetworks.length != len)
      connectedNetworks = new Network[len];

    Network network;
    for (int i = 0; i < len; i++) {
      network = (Network) core.getNetworkCollection().get(messageBuffer.getInt32());
      if (network != null)
        core.getNetworkCollection().setConnectedServers(network, messageBuffer.getInt32());
      connectedNetworks[i] = network;
    }
  }
}

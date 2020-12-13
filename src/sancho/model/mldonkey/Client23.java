/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;

import sancho.core.ICore;
import sancho.model.mldonkey.utility.OpCodes;

public class Client23 extends Client21 {

  public Client23(ICore core) {
    super(core);
  }

  public void connect() {
    core.send(OpCodes.S_CONNECT_CLIENT, new Integer(getId()));
  }

  public void disconnect() {
    core.send(OpCodes.S_DISCONNECT_CLIENT, new Integer(getId()));
  }

}
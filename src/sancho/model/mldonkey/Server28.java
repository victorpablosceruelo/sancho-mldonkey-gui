/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;

import sancho.core.ICore;
import sancho.model.mldonkey.utility.MessageBuffer;

public class Server28 extends Server {

  Server28(ICore core) {
    super(core);
  }

  protected long readNUsers(MessageBuffer messageBuffer) {
    return messageBuffer.getUInt64();
  }

  protected long readNFiles(MessageBuffer messageBuffer) {
    return messageBuffer.getUInt64();
  }
}
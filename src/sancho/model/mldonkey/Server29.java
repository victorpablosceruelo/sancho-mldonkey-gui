/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;

import sancho.core.ICore;
import sancho.model.mldonkey.utility.MessageBuffer;

public class Server29 extends Server28 {

  Server29(ICore core) {
    super(core);
  }

  protected boolean readPreferred(MessageBuffer messageBuffer) {
    return messageBuffer.getBool();
  }

}
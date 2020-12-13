/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;

import sancho.core.ICore;
import sancho.model.mldonkey.utility.MessageBuffer;

public class File25 extends File24 {

  File25(ICore core) {
    super(core);
  }

  protected long readDownloaded(MessageBuffer messageBuffer) {
    return messageBuffer.getUInt64();
  }

  protected long readSize(MessageBuffer messageBuffer) {
    return messageBuffer.getUInt64();
  }

}

/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;

import sancho.core.ICore;
import sancho.model.mldonkey.utility.MessageBuffer;

public class SharedFile25 extends SharedFile {

  SharedFile25(ICore core) {
    super(core);
  }
  
  protected long readSize(MessageBuffer messageBuffer) {
    return messageBuffer.getUInt64();
  }
}

/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;

import sancho.core.ICore;
import sancho.model.mldonkey.utility.MessageBuffer;
import sancho.utility.SwissArmy;

public class File24 extends File21 {

  File24(ICore core) {
    super(core);
  }

  public synchronized String getAgeString() {
    return SwissArmy.calcStringOfSeconds(this.getAge());
  }

  protected long readAge(MessageBuffer messageBuffer) {
    if (core.getFileCollection().eta2())
      ageTS = System.currentTimeMillis();
    return messageBuffer.getInt32();
  }

  protected int[] readChunkAges(MessageBuffer messageBuffer) {
    return messageBuffer.getInt32List();
  }

  protected String readComment(MessageBuffer messageBuffer) {
    return messageBuffer.getString();
  }
}

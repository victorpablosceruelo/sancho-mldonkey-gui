/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;

import sancho.core.ICore;
import sancho.model.mldonkey.utility.MessageBuffer;

public class Result27 extends Result25 {

  String[] uids;
  int resultTime;

  Result27(ICore core) {
    super(core);
  }

  public String getMd4() {
    if (md4 == null) {
      for (int i = 0; i < uids.length; i++)
        if (uids[i].startsWith("urn:ed2k:"))
          return uids[i].substring(9).intern();
    }
    return super.getMd4();
  }

  protected void readUIDs(MessageBuffer messageBuffer) {
    uids = messageBuffer.getStringList();
  }

  public void read(int id, MessageBuffer messageBuffer) {
    super.read(id, messageBuffer);
    resultTime = messageBuffer.getInt32();
  }

}
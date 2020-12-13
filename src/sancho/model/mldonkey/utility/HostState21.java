/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey.utility;

import sancho.model.mldonkey.enums.EnumHostState;

public class HostState21 extends HostState {
  protected int fileNum = -1;

  public synchronized EnumHostState read(MessageBuffer messageBuffer) {
    EnumHostState state = super.read(messageBuffer);
    if (state == EnumHostState.CONNECTED_DOWNLOADING)
      this.fileNum = messageBuffer.getInt32();
    return state;
  }

  public synchronized int getFileNum() {
    return fileNum;
  }

}
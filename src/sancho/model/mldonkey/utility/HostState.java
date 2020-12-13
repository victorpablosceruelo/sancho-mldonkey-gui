/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey.utility;

import sancho.model.mldonkey.enums.EnumHostState;

public class HostState {
  protected int rank;

  public synchronized int getRank() {
    return rank;
  }

  public int getFileNum() {
    return -1;
  }

  protected static int readRank(byte b) {
    switch (b) {
      case 4 :
        return -1;
      case 10 :
        return -2;
      default :
        return 0;
    }
  }

  public synchronized EnumHostState read(MessageBuffer messageBuffer) {
    byte b = messageBuffer.getByte();
    EnumHostState state = EnumHostState.byteToEnum(b);
    if (state == EnumHostState.CONNECTED_AND_QUEUED || state == EnumHostState.NOT_CONNECTED_WAS_QUEUED)
      this.rank = messageBuffer.getInt32();
    else
      this.rank = readRank(b);
    return state;
  }

}
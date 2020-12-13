/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey.utility;

import sancho.model.mldonkey.enums.EnumFileState;
import sancho.view.utility.SResources;

public class FileState {
  private EnumFileState state;
  private String reason;

  public synchronized String getReason() {
    return reason != null ? reason : SResources.S_ES;
  }

  public synchronized EnumFileState getState() {
    return state;
  }

  // guiEncoding#buf_state
  public void read(MessageBuffer messageBuffer) {
    synchronized (this) {
      this.state = EnumFileState.byteToEnum(messageBuffer.getByte());
      if (this.state == EnumFileState.ABORTED)
        this.reason = messageBuffer.getString();
    }
  }

}
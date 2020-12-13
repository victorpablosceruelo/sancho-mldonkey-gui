/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.core;

import java.net.Socket;

import sancho.model.mldonkey.utility.MessageBuffer;
import sancho.model.mldonkey.utility.OpCodes;

public class MLDonkeyCoreMonitor extends MLDonkeyCore {

  public MLDonkeyCoreMonitor(Socket socket, String username, String password, boolean requestPollMode) {
    super(socket, username, password, requestPollMode);
  }

  void processMessage(int opCode, MessageBuffer messageBuffer) {
    switch (opCode) {
      case OpCodes.R_CORE_PROTOCOL :
        readCoreProtocol(messageBuffer);
        break;
      case OpCodes.R_CLIENT_STATS :
        if (!initialized)
          notifyInitialized();
        this.getClientStats().read(messageBuffer);
        break;
      case OpCodes.R_BAD_PASSWORD :
        disconnect();
        this.semaphore = true;
        break;
      default :
        break;
    }
  }

}
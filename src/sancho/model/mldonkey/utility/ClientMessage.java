/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey.utility;

import sancho.core.ICore;

public class ClientMessage {

  private int id;
  private String messageText;

  public synchronized int getId() {
    return id;
  }

  public synchronized String getText() {
    return messageText;
  }

  public synchronized void read(MessageBuffer messageBuffer) {
    this.id = messageBuffer.getInt32();
    this.messageText = messageBuffer.getString();
  }

  public static void sendMessage(ICore core, int clientId, String messageText) {
    Object[] oArray = new Object[2];
    oArray[0] = new Integer(clientId);
    oArray[1] = messageText;

    core.send(OpCodes.S_MESSAGE_TO_CLIENT, oArray);
  }

}
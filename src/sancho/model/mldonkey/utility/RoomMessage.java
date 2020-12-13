/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey.utility;

import sancho.model.mldonkey.enums.EnumMessage;

public class RoomMessage {
  protected String message;
  protected int fromInt;
  protected int roomNumber;
  protected EnumMessage enumMessage;

  public synchronized String getMessage() {
    return message;
  }

  public synchronized EnumMessage getMessageType() {
    return enumMessage;
  }

  public synchronized int getFrom() {
    return fromInt;
  }

  public synchronized int getRoomNumber() {
    return roomNumber;
  }

  public void read(MessageBuffer messageBuffer) {
    synchronized (this) {
      roomNumber = messageBuffer.getInt32();
      enumMessage = EnumMessage.intToEnum(messageBuffer.getInt8());

      if (enumMessage != EnumMessage.SERVER)
        fromInt = messageBuffer.getInt32();

      message = messageBuffer.getString();
    }
  }
}
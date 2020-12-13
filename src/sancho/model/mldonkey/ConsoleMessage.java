/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;

import java.util.Observable;

import sancho.model.mldonkey.utility.MessageBuffer;

public class ConsoleMessage extends Observable implements IObject {

  private static final int MAX_SIZE = 32767 * 8;
  private StringBuffer message = new StringBuffer();

  public synchronized String getMessage() {
    String string = message.toString();
    message.setLength(0);
    return string;
  }

  // guiEncoding#console
  public void read(MessageBuffer messageBuffer) {
    synchronized (this) {
      if (message.length() > MAX_SIZE)
        message.setLength(0);
      message.append(messageBuffer.getString());
    }
    this.setChanged();
    this.notifyObservers(this);
  }

}
/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.console;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.swt.widgets.Composite;

import sancho.core.Sancho;
import sancho.model.mldonkey.utility.OpCodes;

public class MessageConsole extends Console {

  private static final SimpleDateFormat sdFormatter = new SimpleDateFormat("[HH:mm:ss] ");
  protected int clientId;

  public MessageConsole(Composite parent, int style, int clientId) {
    super(parent, style);
    this.clientId = clientId;
  }

  public void prefixAppend() {
    infoDisplay.append(getTimeStamp() + "> ");
  }

  public String getTimeStamp() {
    return sdFormatter.format(new Date());
  }

  public void sendMessage() {
    if (!Sancho.hasCollectionFactory())
      return;

    Object[] oArray = new Object[2];
    oArray[0] = new Integer(clientId);
    oArray[1] = input.getText();

    Sancho.send(OpCodes.S_MESSAGE_TO_CLIENT, oArray);
  }

}
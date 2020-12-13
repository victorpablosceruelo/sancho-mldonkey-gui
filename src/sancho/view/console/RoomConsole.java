/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.console;

import org.eclipse.swt.widgets.Composite;

import sancho.core.Sancho;
import sancho.model.mldonkey.utility.OpCodes;

public class RoomConsole extends Console {

  protected int roomNumber;

  public RoomConsole(Composite parent, int style, int roomNumber) {
    super(parent, style);
    this.roomNumber = roomNumber;
  }

  public void sendMessage() {
    if (!Sancho.hasCollectionFactory())
      return;

    Object[] oArray = new Object[4];
    oArray[0] = new Integer(roomNumber);
    oArray[1] = new Byte((byte) 1); // 
    oArray[2] = new Integer(0);
    oArray[3] = input.getText();

    Sancho.send(OpCodes.S_SEND_MESSAGE, oArray);
  }

}
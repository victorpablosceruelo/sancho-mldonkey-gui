/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey.utility;

import sancho.model.mldonkey.enums.EnumClientMode;
import sancho.view.utility.SResources;

public class Kind {

  Kind() {
    this.addr = UtilityFactory.getAddr();
  }

  private int port;
  private String name;
  private String hash;
  private Addr addr;

  public synchronized String getHash() {
    return hash != null ? hash : SResources.S_ES;
  }

  public synchronized String getName() {
    return name != null ? name : SResources.S_ES;
  }

  public Addr getAddr() {
    return addr;
  }

  public synchronized int getPort() {
    return port;
  }

  // guiEncoding#buf_kind
  public synchronized EnumClientMode read(MessageBuffer messageBuffer) {
    EnumClientMode mode = EnumClientMode.byteToEnum(messageBuffer.getByte());
    if (mode == EnumClientMode.DIRECT) {
      this.addr.read(false, messageBuffer);
      this.port = messageBuffer.getUInt16();
    } else {
      this.name = messageBuffer.getString();
      this.hash = messageBuffer.getMd4();
      this.addr.setUnknown();
    }
    return mode;
  }
}
/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey.utility;

import sancho.model.mldonkey.enums.EnumClientMode;
import sancho.view.utility.SResources;

public class Addr {

  private byte[] byteArray;
  private String ipString;

  public int compareTo(Object object) {
    if (object instanceof Addr) {
      Addr addr = (Addr) object;
      if (this.hasHostName() && !addr.hasHostName())
        return 1;
      else if (!this.hasHostName() && addr.hasHostName())
        return -1;
      else if (this.hasHostName() && addr.hasHostName())
        return this.getIpString().compareToIgnoreCase(addr.getIpString());
      else if (addr.getByteAddress() == null)
        return 1;
      else if (this.getByteAddress() == null)
        return -1;
      else
        return compare(this.getByteAddress(), addr.getByteAddress());
    } else
      return -1;
  }

  public synchronized byte[] getByteAddress() {
    return byteArray;
  }

  private synchronized String getIpString() {
    if (ipString != null)
      return ipString;

    if (byteArray != null) {
      StringBuffer stringBuffer = new StringBuffer(16);
      stringBuffer.append(byteArray[0] & 0xFF);
      stringBuffer.append(SResources.S_DOT);
      stringBuffer.append(byteArray[1] & 0xFF);
      stringBuffer.append(SResources.S_DOT);
      stringBuffer.append(byteArray[2] & 0xFF);
      stringBuffer.append(SResources.S_DOT);
      stringBuffer.append(byteArray[3] & 0xFF);
      return stringBuffer.toString();
    }

    return SResources.S_ES;
  }

  public synchronized boolean hasHostName() {
    return byteArray == null && ipString != null;
  }

  public synchronized void read(boolean isString, MessageBuffer messageBuffer) {
    if (isString) {
      this.ipString = messageBuffer.getString();
      this.byteArray = null;
    } else {

      if (this.byteArray == null)
        this.byteArray = new byte[4];

      messageBuffer.getIP(this.byteArray);
      ipString = null;
      
      /* 
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append(byteArray[0] & 0xFF);
      stringBuffer.append(SResources.S_DOT);
      stringBuffer.append(byteArray[1] & 0xFF);
      stringBuffer.append(SResources.S_DOT);
      stringBuffer.append(byteArray[2] & 0xFF);
      stringBuffer.append(SResources.S_DOT);
      stringBuffer.append(byteArray[3] & 0xFF);
      this.ipString = stringBuffer.toString();
      
      */
    }
  }

  //guiEncoding.ml#buf_addr
  public void read(MessageBuffer messageBuffer) {
    this.read(messageBuffer.getBool(), messageBuffer);
  }

  public synchronized void setUnknown() {
    byteArray = null;
  }

  public String toString() {
    if (getByteAddress() == null && getIpString().equals(SResources.S_ES))
      return EnumClientMode.FIREWALLED.getName();
    else
      return this.getIpString();
  }

  private static int compare(byte[] byteArray1, byte[] byteArray2) {
    if (byteArray1 == null)
      return -1;
    if (byteArray2 == null)
      return 1;

    int i1;
    int i2;
    for (int i = 0; i < byteArray1.length; i++) {
      i1 = byteArray1[i] & 0xff;
      i2 = byteArray2[i] & 0xff;
      if (i1 != i2)
        return i1 - i2;
    }
    return 0;
  }
}
/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey.utility;

import sancho.model.mldonkey.enums.EnumType;
import sancho.view.utility.SResources;

public class Tag {
  private EnumType enumType;
  private String name;
  private int value;
  private String stringValue;

  public synchronized String getName() {
    return name != null ? name : SResources.S_ES;
  }

  public synchronized String getStringValue() {
    return stringValue != null ? stringValue : SResources.S_ES;
  }

  public synchronized EnumType getType() {
    return enumType;
  }

  public synchronized int getValue() {
    return value;
  }

  // guiEncoding#buf_tag
  public void read(MessageBuffer messageBuffer) {
    synchronized (this) {
      this.name = messageBuffer.getString();
      this.enumType = EnumType.byteToEnum(messageBuffer.getByte());

      if (this.enumType == EnumType.STRING)
        this.stringValue = messageBuffer.getString();
      else if (this.enumType == EnumType.U_INT16)
        this.value = messageBuffer.getUInt16();
      else if (this.enumType == EnumType.U_INT8)
        this.value = messageBuffer.getInt8();
      else
        this.value = messageBuffer.getInt32();
    }
  }

  public synchronized String toString() {
    if (this.enumType == EnumType.STRING)
      return getStringValue();
    else
      return String.valueOf(getValue());
  }

}
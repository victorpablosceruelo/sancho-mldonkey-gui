/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey.enums;

public class EnumFormat extends AbstractEnum {
  public static final EnumFormat UNKNOWN = new EnumFormat(1, "unknown");
  public static final EnumFormat GENERIC = new EnumFormat(2, "generic");
  public static final EnumFormat AVI = new EnumFormat(4, "avi");
  public static final EnumFormat MP3 = new EnumFormat(8, "mp3");

  private EnumFormat(int i, String resString) {
    super(i, "e.format." + resString);
  }

  public static EnumFormat byteToEnum(byte b) {
    switch (b) {
      case 1 :
        return EnumFormat.GENERIC;
      case 2 :
        return EnumFormat.AVI;
      case 3 :
        return EnumFormat.MP3;
      default :
        return EnumFormat.UNKNOWN;
    }
  }

}

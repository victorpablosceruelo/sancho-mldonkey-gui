/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey.enums;

public class EnumMessage extends AbstractEnum {
  public static final EnumMessage UNKNOWN = new EnumMessage(0, "unknown");
  public static final EnumMessage SERVER = new EnumMessage(1, "server");
  public static final EnumMessage PUBLIC = new EnumMessage(2, "public");
  public static final EnumMessage PRIVATE = new EnumMessage(4, "pivate");

  private EnumMessage(int i, String resString) {
    super(i, "e.message." + resString);
  }

  public static final EnumMessage intToEnum(int i) {
    switch (i) {
      case 0 :
        return EnumMessage.SERVER;
      case 1 :
        return EnumMessage.PUBLIC;
      case 2 :
        return EnumMessage.PRIVATE;
      default :
        return EnumMessage.UNKNOWN;
    }

  }

}

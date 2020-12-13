/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey.enums;

public class EnumClientType extends AbstractEnum {
  public static final EnumClientType SOURCE = new EnumClientType(1, "source");
  public static final EnumClientType FRIEND = new EnumClientType(2, "friend");
  public static final EnumClientType CONTACT = new EnumClientType(4, "contact");

  private EnumClientType(int i, String resString) {
    super(i, "e.clientType." + resString);
  }

  public static EnumClientType byteToEnum(byte b) {
    switch (b) {
      case 1 :
        return EnumClientType.FRIEND;
      case 2 :
        return EnumClientType.CONTACT;
      default :
        return EnumClientType.SOURCE;
    }
  }
}

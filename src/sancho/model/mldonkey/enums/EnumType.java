/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey.enums;

import sancho.core.Sancho;

public class EnumType extends AbstractEnum {
  public static final EnumType UNKNOWN = new EnumType(0, "UNKNOWN");
  public static final EnumType UNSIGNED_INT = new EnumType(1, "UNSIGNED_INT");
  public static final EnumType SIGNED_INT = new EnumType(2, "SIGNED_INT");
  public static final EnumType STRING = new EnumType(4, "STRING");
  public static final EnumType IPADDRESS = new EnumType(8, "IPADDRESS");
  public static final EnumType U_INT16 = new EnumType(16, "UINT16");
  public static final EnumType U_INT8 = new EnumType(32, "UINT8");
  public static final EnumType PAIR = new EnumType(64, "PAIR");

  private EnumType(int i, String resString) {
    super(i, resString);
  }

  public static EnumType byteToEnum(byte b) {
    switch (b) {
      case 0 :
        return EnumType.UNSIGNED_INT;
      case 1 :
        return EnumType.SIGNED_INT;
      case 2 :
        return EnumType.STRING;
      case 3 :
        return EnumType.IPADDRESS;
      case 4 :
        return EnumType.U_INT16;
      case 5 :
        return EnumType.U_INT8;
      case 6 :
        return EnumType.PAIR;
      default :
        Sancho.pDebug("ET: Unknown Type");
        return EnumType.UNKNOWN;
    }
  }
}
/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey.enums;

import sancho.core.Sancho;

public class EnumTagType extends AbstractEnum {
  public static final EnumTagType INT = new EnumTagType(1, "Int");
  public static final EnumTagType STRING = new EnumTagType(2, "String");
  public static final EnumTagType BOOL = new EnumTagType(4, "Bool");
  public static final EnumTagType FILE = new EnumTagType(8, "File");
  public static final EnumTagType IP_LIST = new EnumTagType(16, "Ip List");
  public static final EnumTagType IP = new EnumTagType(32, "Ip");
  public static final EnumTagType ADDR = new EnumTagType(64, "Addr");
  public static final EnumTagType FLOAT = new EnumTagType(128, "Float");
  public static final EnumTagType MD4 = new EnumTagType(256, "MD4");
  public static final EnumTagType SHA1 = new EnumTagType(512, "SHA1");
  public static final EnumTagType INT64 = new EnumTagType(1024, "Int64");

  private EnumTagType(int i, String resString) {
    super(i, resString);
  }

  public static EnumTagType optionByteToEnum(byte b) {
    switch (b) {
      case 1 :
        return EnumTagType.BOOL;
      case 2 :
        return EnumTagType.FILE;
      default :
        return EnumTagType.STRING;
    }
  }

  public static EnumTagType stringToEnum(String string) {
    if (string.equalsIgnoreCase("String"))
      return EnumTagType.STRING;
    else if (string.equalsIgnoreCase("Ip List"))
      return EnumTagType.IP_LIST;
    else if (string.equalsIgnoreCase("Int"))
      return EnumTagType.INT;
    else if (string.equalsIgnoreCase("Bool"))
      return EnumTagType.BOOL;
    else if (string.equalsIgnoreCase("Ip"))
      return EnumTagType.IP;
    else if (string.equalsIgnoreCase("Addr"))
      return EnumTagType.ADDR;
    else if (string.equalsIgnoreCase("Integer"))
      return EnumTagType.INT;
    else if (string.equalsIgnoreCase("Float"))
      return EnumTagType.FLOAT;
    else if (string.equalsIgnoreCase("Md4"))
      return EnumTagType.MD4;
    else if (string.equalsIgnoreCase("Sha1"))
      return EnumTagType.SHA1;
    else if (string.equalsIgnoreCase("Int64"))
      return EnumTagType.INT64;
    else {
      Sancho.pDebug("EnumTagType unknown" + string);
      return EnumTagType.STRING;
    }
  }
}

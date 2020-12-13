/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey.enums;

public class EnumClientMode extends AbstractEnum {
  public static final EnumClientMode DIRECT = new EnumClientMode(1, "direct");
  public static final EnumClientMode FIREWALLED = new EnumClientMode(2, "firewalled");

  private EnumClientMode(int i, String resString) {
    super(i, "e.clientmode." + resString);
  }

  public static EnumClientMode byteToEnum(byte b) {
    switch (b) {
      case 1 :
        return EnumClientMode.FIREWALLED;
      default :
        return EnumClientMode.DIRECT;
    }
  }

}
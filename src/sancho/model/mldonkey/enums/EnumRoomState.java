/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey.enums;

public class EnumRoomState extends AbstractEnum {
  public static final EnumRoomState UNKNOWN = new EnumRoomState(0, "unknown");
  public static final EnumRoomState OPEN = new EnumRoomState(1, "open");
  public static final EnumRoomState CLOSED = new EnumRoomState(2, "closed");
  public static final EnumRoomState PAUSED = new EnumRoomState(4, "paused");

  private EnumRoomState(int i, String resString) {
    super(i, "e.roomState." + resString);
  }

  public static EnumRoomState intToEnum(int i) {
    switch (i) {
      case 0 :
        return EnumRoomState.OPEN;
      case 1 :
        return EnumRoomState.CLOSED;
      case 2 :
        return EnumRoomState.PAUSED;
      default :
        return EnumRoomState.UNKNOWN;
    }
  }

}

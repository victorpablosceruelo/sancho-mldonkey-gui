/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey.enums;

public class EnumFileState extends AbstractEnum {
  public static final EnumFileState UNKNOWN = new EnumFileState(0, "unknown");
  public static final EnumFileState DOWNLOADING = new EnumFileState(1, "downloading");
  public static final EnumFileState PAUSED = new EnumFileState(2, "paused");
  public static final EnumFileState DOWNLOADED = new EnumFileState(4, "downloaded");
  public static final EnumFileState SHARED = new EnumFileState(8, "shared");
  public static final EnumFileState CANCELLED = new EnumFileState(16, "cancelled");
  public static final EnumFileState NEW = new EnumFileState(32, "new");
  public static final EnumFileState ABORTED = new EnumFileState(64, "aborted");
  public static final EnumFileState QUEUED = new EnumFileState(128, "queued");

  private EnumFileState(int i, String resString) {
    super(i, "e.fileState." + resString);
  }

  public static EnumFileState byteToEnum(byte b) {
    switch (b) {
      case 0 :
        return EnumFileState.DOWNLOADING;
      case 1 :
        return EnumFileState.PAUSED;
      case 2 :
        return EnumFileState.DOWNLOADED;
      case 3 :
        return EnumFileState.SHARED;
      case 4 :
        return EnumFileState.CANCELLED;
      case 5 :
        return EnumFileState.NEW;
      case 6 :
        return EnumFileState.ABORTED;
      case 7 :
        return EnumFileState.QUEUED;
      default :
        return EnumFileState.UNKNOWN;
    }
  }

}

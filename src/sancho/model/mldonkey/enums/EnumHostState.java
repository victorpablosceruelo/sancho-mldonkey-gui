/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey.enums;

import org.eclipse.swt.graphics.Image;

import sancho.view.utility.SResources;

public class EnumHostState extends AbstractEnum {

  private static final String S_I_TRANSFERRING = "ep_transferring";
  private static final String S_I_CONNECTING = "ep_connecting";
  private static final String S_I_ASKING = "ep_asking";
  private static final String S_I_NO_NEEDED = "ep_noneeded";
  private static final String S_I_UNKNOWN = "ep_unknown";

  public static final EnumHostState UNKNOWN = new EnumHostState(0, "unknown");
  public static final EnumHostState NOT_CONNECTED = new EnumHostState(1, "notConnected");
  public static final EnumHostState CONNECTING = new EnumHostState(2, "connecting");
  public static final EnumHostState CONNECTED_INITIATING = new EnumHostState(4, "connectedInitiating");
  public static final EnumHostState CONNECTED_DOWNLOADING = new EnumHostState(8, "connectedDownloading");
  public static final EnumHostState CONNECTED = new EnumHostState(16, "connected");
  public static final EnumHostState CONNECTED_AND_QUEUED = new EnumHostState(32, "connectedAndQueued");
  public static final EnumHostState NEW_HOST = new EnumHostState(64, "newHost");
  public static final EnumHostState REMOVE_HOST = new EnumHostState(128, "removeHost");
  public static final EnumHostState BLACKLISTED = new EnumHostState(256, "blacklisted");
  public static final EnumHostState NOT_CONNECTED_WAS_QUEUED = new EnumHostState(512, "notConnectedWasQueued");

  private EnumHostState(int i, String resString) {
    super(i, "e.state." + resString);
  }

  public static EnumHostState byteToEnum(byte b) {
    switch (b) {
      case 0 :
        return EnumHostState.NOT_CONNECTED;
      case 1 :
        return EnumHostState.CONNECTING;
      case 2 :
        return EnumHostState.CONNECTED_INITIATING;
      case 3 :
        return EnumHostState.CONNECTED_DOWNLOADING;
      case 4 :
        return EnumHostState.CONNECTED;
      case 5 :
        return EnumHostState.CONNECTED_AND_QUEUED;
      case 6 :
        return EnumHostState.NEW_HOST;
      case 7 :
        return EnumHostState.REMOVE_HOST;
      case 8 :
        return EnumHostState.BLACKLISTED;
      case 9 :
        return EnumHostState.NOT_CONNECTED_WAS_QUEUED;
      case 10 :
        return EnumHostState.CONNECTED;
      default :
        return EnumHostState.UNKNOWN;
    }
  }

  public Image getImage() {
    if (this == EnumHostState.CONNECTED_DOWNLOADING)
      return SResources.getImage(S_I_TRANSFERRING);
    else if ((this == EnumHostState.CONNECTING) || (this == EnumHostState.CONNECTED_INITIATING))
      return SResources.getImage(S_I_CONNECTING);
    else if ((this == EnumHostState.CONNECTED_AND_QUEUED) || (this == EnumHostState.NOT_CONNECTED_WAS_QUEUED))
      return SResources.getImage(S_I_ASKING);
    else if (this == EnumHostState.BLACKLISTED)
      return SResources.getImage(S_I_NO_NEEDED);
    else
      return SResources.getImage(S_I_UNKNOWN);
  }

}
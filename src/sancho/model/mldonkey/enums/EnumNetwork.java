/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey.enums;

import org.eclipse.swt.graphics.Image;

import sancho.view.utility.SResources;

public class EnumNetwork extends AbstractEnum {
  public static final EnumNetwork UNKNOWN = new EnumNetwork(0, "unknown");
  public static final EnumNetwork DONKEY = new EnumNetwork(1, "donkey");
  public static final EnumNetwork SOULSEEK = new EnumNetwork(2, "soulseek");
  public static final EnumNetwork GNUT = new EnumNetwork(4, "gnutella");
  public static final EnumNetwork GNUT2 = new EnumNetwork(8, "gnutella2");
  public static final EnumNetwork OV = new EnumNetwork(16, "overnet");
  public static final EnumNetwork BT = new EnumNetwork(32, "bittorrent");
  public static final EnumNetwork FT = new EnumNetwork(64, "fasttrack");
  public static final EnumNetwork OPENNP = new EnumNetwork(128, "opennap");
  public static final EnumNetwork DC = new EnumNetwork(256, "directconnect");
  public static final EnumNetwork MULTINET = new EnumNetwork(512, "multinet");
  public static final EnumNetwork FILETP = new EnumNetwork(1024, "filetp");

  protected String resName;

  private EnumNetwork(int i, String resString) {
    super(i, "e.network." + resString);
    this.resName = "e.network." + resString;
  }

  public Image getImage() {
    return getImage("connected");
  }

  public Image getImage(String imageType) {
    if (this == EnumNetwork.OV)
      return SResources.getImage("e.network.donkey." + imageType);
    return SResources.getImage(resName + "." + imageType);
  }

  public static EnumNetwork stringToEnum(String string) {
    if (string.equals("Donkey"))
      return EnumNetwork.DONKEY;
    else if (string.equals("Fasttrack"))
      return EnumNetwork.FT;
    else if (string.equals("Soulseek"))
      return EnumNetwork.SOULSEEK;
    else if (string.equals("BitTorrent"))
      return EnumNetwork.BT;
    else if (string.equals("Overnet"))
      return EnumNetwork.OV;
    else if (string.equals("Gnutella"))
      return EnumNetwork.GNUT;
    else if (string.equals("Gnutella2") || string.equals("G2"))
      return EnumNetwork.GNUT2;
    else if (string.equals("Direct Connect"))
      return EnumNetwork.DC;
    else if (string.equals("Open Napster"))
      return EnumNetwork.OPENNP;
    else if (string.equals("MultiNet"))
      return EnumNetwork.MULTINET;
    else if (string.equals("FileTP"))
      return EnumNetwork.FILETP;
    else
      return EnumNetwork.UNKNOWN;
  }

  public String getTempFilePrefix() {
    if (this == EnumNetwork.BT)
      return "BT-";
    else if (this == EnumNetwork.FT)
      return "FT-";
    else if (this == EnumNetwork.SOULSEEK)
      return "SK-";
    else if (this == EnumNetwork.DC)
      return "DC-";
    else if (this == EnumNetwork.GNUT)
      return "GNUT-";
    else if (this == EnumNetwork.GNUT2)
      return "GNUT-";
    else if (this == EnumNetwork.OPENNP)
      return "ON-";
    else
      return "";
  }
  
  public String getDefaultOptionPrefix() {
    if (this == EnumNetwork.BT)
      return "BT-";
    else if (this == EnumNetwork.FT)
      return "FT-";
    else if (this == EnumNetwork.SOULSEEK)
      return "SLSK-";
    else if (this == EnumNetwork.DC)
      return "DC-";
    else if (this == EnumNetwork.GNUT)
      return "GNUT-";
    else if (this == EnumNetwork.GNUT2)
      return "G2-";
    else if (this == EnumNetwork.OPENNP)
      return "OpenNap-";
    else if (this == EnumNetwork.FILETP) 
      return "FTP-";
    else
      return "";
  }

}

/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;

import org.eclipse.swt.graphics.Image;

import sancho.core.ICore;
import sancho.model.mldonkey.enums.EnumNetwork;
import sancho.model.mldonkey.utility.MessageBuffer;
import sancho.model.mldonkey.utility.OpCodes;
import sancho.view.utility.SResources;

public class Network extends AObject {
  private static final String RS_CONNECTED_TO = SResources.getString("sl.n.connectedTo");
  private static final String RS_ENABLED = SResources.getString("sl.n.enabled");
  private static final String RS_DISABLED = SResources.getString("sl.n.disabled");

  private static final String S_CONNECTED = "connected";
  private static final String S_DISABLED = "disabled";
  private static final String S_DISCONNECTED = "disconnected";
  private static final String S_BAD_CONNECT = "badconnect";

  private static StringBuffer stringBuffer = new StringBuffer();

  protected String configFile;
  protected long downloaded;
  private boolean enabled;
  private EnumNetwork enumNetwork;
  protected int id;
  protected String name;
  protected long uploaded;

  Network(ICore core) {
    super(core);
  }

  public boolean equals(EnumNetwork enumNetwork) {
    return this.getEnumNetwork() == enumNetwork;
  }

  public boolean equals(Object obj) {
    return (obj instanceof Network && getId() == ((Network) obj).getId());
  }

  public synchronized String getConfigFile() {
    return configFile != null ? configFile : SResources.S_ES;
  }

  public synchronized long getDownloaded() {
    return downloaded;
  }

  public synchronized EnumNetwork getEnumNetwork() {
    return enumNetwork;
  }

  public synchronized int getId() {
    return id;
  }

  public synchronized Image getImage() {
    if (this.isEnabled()) {
      if (core.getProtocol() < 18 || isVirtual() || (!this.hasServers() && !this.hasSupernodes()))
        return enumNetwork.getImage(S_CONNECTED);

      int maxConnnectedServers = core.getOptionCollection().getMaxConnected(this);
      int currentConnectedServers = numConnectedServers();

      if ((currentConnectedServers >= 1 && enumNetwork == EnumNetwork.DC)
          || currentConnectedServers >= maxConnnectedServers)
        return enumNetwork.getImage(S_CONNECTED);
      else
        return enumNetwork.getImage(currentConnectedServers == 0 ? S_DISCONNECTED : S_BAD_CONNECT);
    } else
      return enumNetwork.getImage(S_DISABLED);
  }

  public String getName() {
    return name != null ? name : SResources.S_ES;
  }

  public String getToolTip() {
    stringBuffer.setLength(0);
    if (isEnabled() && (hasServers() || hasSupernodes())) {
      stringBuffer.append(getName());
      stringBuffer.append(SResources.S_SPACE);
      stringBuffer.append(RS_CONNECTED_TO);
      stringBuffer.append(numConnectedServers());
      return stringBuffer.toString();
    } else if (isVirtual())
      return getName();
    else {
      stringBuffer.append(getName());
      stringBuffer.append(SResources.S_SPACE);
      stringBuffer.append(isEnabled() ? RS_ENABLED : RS_DISABLED);
      return stringBuffer.toString();
    }
  }

  public synchronized long getUploaded() {
    return uploaded;
  }

  public synchronized boolean hasChat() {
    return this.enumNetwork == EnumNetwork.DONKEY || this.enumNetwork == EnumNetwork.OV;
  }

  public int hashCode() {
    return getId();
  }

  public synchronized boolean hasRooms() {
    return this.enumNetwork == EnumNetwork.SOULSEEK || this.enumNetwork == EnumNetwork.DC;
  }

  public synchronized boolean hasServers() {
    return this.enumNetwork != EnumNetwork.BT && this.enumNetwork != EnumNetwork.FT
        && this.enumNetwork != EnumNetwork.GNUT;
  }

  public synchronized boolean hasSupernodes() {
    return this.enumNetwork == EnumNetwork.FT || this.enumNetwork == EnumNetwork.GNUT
        || this.enumNetwork == EnumNetwork.GNUT2;
  }

  public synchronized boolean hasUpload() {
    return this.enumNetwork == EnumNetwork.DONKEY || this.enumNetwork == EnumNetwork.OV
        || this.enumNetwork == EnumNetwork.BT || this.enumNetwork == EnumNetwork.GNUT
        || this.enumNetwork == EnumNetwork.DC;
  }

  public synchronized boolean isEnabled() {
    return enabled;
  }

  public boolean isMultinet() {
    return false;
  }

  public synchronized boolean isSearchable() {
    return this.enumNetwork != EnumNetwork.BT;
  }

  public boolean isVirtual() {
    return false;
  }

  public int numConnectedServers() {
    return core.getServerCollection().getConnected(this.getEnumNetwork());
  }

  public void read(int networkID, MessageBuffer messageBuffer) {
    synchronized (this) {
      this.id = networkID;
      this.name = messageBuffer.getString();
      this.enabled = messageBuffer.getBool();
      this.configFile = messageBuffer.getString();
      this.uploaded = messageBuffer.getUInt64();
      this.downloaded = messageBuffer.getUInt64();

      this.enumNetwork = EnumNetwork.stringToEnum(this.name);
    }

    if (!this.isEnabled())
      this.core.getServerCollection().removeAll(this.getEnumNetwork());
  }

  // guiEncoding#buf_network
  public void read(MessageBuffer messageBuffer) {
    read(messageBuffer.getInt32(), messageBuffer);
  }

  protected void setConnectedServers(int i) {
  }

  public void toggleEnabled() {
    Object[] oArray = new Object[2];
    oArray[0] = new Integer(this.getId());
    oArray[1] = new Byte(isEnabled() ? (byte) 0 : (byte) 1);
    core.send(OpCodes.S_ENABLE_NETWORK, oArray);
  }
}
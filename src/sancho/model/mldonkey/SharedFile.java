/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;

import gnu.regexp.RE;
import gnu.regexp.REException;

import org.eclipse.swt.graphics.Image;

import sancho.core.ICore;
import sancho.core.Sancho;
import sancho.model.mldonkey.enums.EnumNetwork;
import sancho.model.mldonkey.utility.MessageBuffer;
import sancho.utility.SwissArmy;
import sancho.view.utility.SResources;

public class SharedFile extends AObject {

  static RE pathRE;
  protected long bytesUploaded;
  protected int id;
  protected String md4;
  protected String name;
  protected EnumNetwork networkEnum;
  protected int requests;
  protected long size;

  SharedFile(ICore core) {
    super(core);
  }

  public synchronized long getBytesUploaded() {
    return bytesUploaded;
  }

  public String getED2K() {
    return "ed2k://|file|" + this.getName() + "|" + this.getSize() + "|" + this.getMd4() + "|/";
  }

  public synchronized int getId() {
    return id;
  }

  public synchronized String getMd4() {
    return md4 != null ? md4 : SResources.S_ES;
  }

  public synchronized String getName() {
    return name != null ? name : SResources.S_ES;
  }

  public synchronized int getRequests() {
    return requests;
  }

  public synchronized String getNetworkName() {
    return networkEnum.getName();
  }

  public synchronized Image getNetworkImage() {
    return networkEnum.getImage();
  }

  public synchronized long getSize() {
    return size;
  }

  public synchronized String getSizeString() {
    return SwissArmy.calcStringSize(size);
  }

  public synchronized String getUploadedString() {
    return SwissArmy.calcStringSize(bytesUploaded);
  }

  public void parseName() {
    if (this.name.indexOf("/") != -1)
      this.name = pathRE.getMatch(this.name).toString();
  }

  public void read(int id, MessageBuffer messageBuffer) {
    synchronized (this) {
      this.id = id;
      this.networkEnum = readNetworkEnum(messageBuffer);
      this.name = messageBuffer.getString();
      this.size = readSize(messageBuffer);
      this.bytesUploaded = messageBuffer.getUInt64();
      this.requests = messageBuffer.getInt32();
      this.md4 = messageBuffer.getMd4();
      this.parseName();
    }
  }

  // guiEncoding#buf_shared_info
  public void read(MessageBuffer messageBuffer) {
    read(messageBuffer.getInt32(), messageBuffer);
  }

  protected long readSize(MessageBuffer messageBuffer) {
    return messageBuffer.getInt32() & 0xFFFFFFFFL;
  }

  public boolean readUpdate(int id, MessageBuffer messageBuffer) {
    long oldUpload = getBytesUploaded();
    int oldRequests = getRequests();
    read(id, messageBuffer);
    return oldUpload != getBytesUploaded() || oldRequests != getRequests();
  }

  protected EnumNetwork readNetworkEnum(MessageBuffer messageBuffer) {
    return core.getNetworkCollection().getNetworkEnum(messageBuffer.getInt32());
  }

  public boolean upload(int id, MessageBuffer messageBuffer) {
    long oldUpload = getBytesUploaded();
    int oldRequests = getRequests();
    synchronized (this) {
      this.bytesUploaded = messageBuffer.getUInt64();
      this.requests = messageBuffer.getInt32();
    }
    return oldUpload != getBytesUploaded() || oldRequests != getRequests();
  }

  static {
    try {
      pathRE = new RE("[^/]*$");
    } catch (REException e) {
      Sancho.pDebug("SharedFile: " + e);
    }
  }

}
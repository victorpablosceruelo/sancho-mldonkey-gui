/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;

import sancho.core.ICore;
import sancho.model.mldonkey.utility.Addr;
import sancho.model.mldonkey.utility.MessageBuffer;
import sancho.model.mldonkey.utility.OpCodes;
import sancho.model.mldonkey.utility.Tag;
import sancho.model.mldonkey.utility.UtilityFactory;
import sancho.view.utility.SResources;

public class User extends AObject {

  private static String S_OSB = "[";
  private static String S_CSB = "] ";

  private Addr addr;
  private int id;
  private String md4;
  private String name;
  private int port;
  private int serverId;
  private Tag[] tags;

  User(ICore core) {
    super(core);
    addr = UtilityFactory.getAddr();
  }

  public void addAsFriend() {
    core.send(OpCodes.S_ADD_USER_FRIEND, new Integer(getId()));
  }

  public Addr getAddr() {
    return addr;
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

  public synchronized int getPort() {
    return port;
  }

  public synchronized int getServerId() {
    return serverId;
  }

  public synchronized String getTagsString() {
    StringBuffer stringBuffer = new StringBuffer();
    for (int i = 0; i < tags.length; i++) {
      stringBuffer.append(S_OSB);
      stringBuffer.append(tags[i]);
      stringBuffer.append(S_CSB);
    }
    return stringBuffer.toString();
  }

  public synchronized void read(int userID, MessageBuffer messageBuffer) {
    synchronized (this) {
      this.id = userID;
      this.md4 = messageBuffer.getMd4();
      this.name = messageBuffer.getString();
      this.addr.read(false, messageBuffer);
      this.port = messageBuffer.getUInt16();
      this.tags = messageBuffer.getTagList();
      this.serverId = messageBuffer.getInt32();
    }
  }

  // guiEncoding#buf_user
  public void read(MessageBuffer messageBuffer) {
    read(messageBuffer.getInt32(), messageBuffer);
  }
}
/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;

import sancho.core.ICore;
import sancho.model.mldonkey.utility.MessageBuffer;

public class Network18 extends Network {

  private int connectedServers;
  private boolean chat;
  private boolean multinet;
  private boolean rooms;
  private boolean search;
  private boolean servers;
  private boolean supernodes;
  private boolean upload;
  private boolean virtual;

  Network18(ICore core) {
    super(core);
  }

  public synchronized boolean hasChat() {
    return this.chat;
  }

  public synchronized boolean hasRooms() {
    return this.rooms;
  }

  public synchronized boolean hasServers() {
    return this.servers;
  }

  public synchronized boolean hasSupernodes() {
    return this.supernodes;
  }

  public synchronized boolean hasUpload() {
    return this.upload;
  }

  public synchronized boolean isMultinet() {
    return this.multinet;
  }

  public synchronized boolean isSearchable() {
    return this.search;
  }

  public synchronized boolean isVirtual() {
    return this.virtual;
  }

  public synchronized int numConnectedServers() {
    return connectedServers;
  }

  // guiEncoding#buf_network
  public void read(int networkID, MessageBuffer messageBuffer) {
    super.read(networkID, messageBuffer);

    synchronized (this) {
      this.connectedServers = messageBuffer.getInt32();
      int len = messageBuffer.getUInt16();

      for (int i = 0; i < len; i++) {
        switch (messageBuffer.getUInt16()) {
          case 0 :
            this.servers = true;
            break;
          case 1 :
            this.rooms = true;
            break;
          case 2 :
            this.multinet = true;
            break;
          case 3 :
            this.virtual = true;
            break;
          case 4 :
            this.search = true;
            break;
          case 5 :
            this.chat = true;
            break;
          case 6 :
            this.supernodes = true;
            break;
          case 7 :
            this.upload = true;
            break;
          default :
            break;
        }
      }
    }
  }

  public void read(MessageBuffer messageBuffer) {
    read(messageBuffer.getInt32(), messageBuffer);
  }

  protected synchronized void setConnectedServers(int i) {
    this.connectedServers = i;
  }
}
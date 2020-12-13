/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.swt.graphics.Image;

import sancho.core.ICore;
import sancho.model.mldonkey.enums.EnumHostState;
import sancho.model.mldonkey.enums.EnumNetwork;
import sancho.model.mldonkey.utility.Addr;
import sancho.model.mldonkey.utility.HostState;
import sancho.model.mldonkey.utility.MessageBuffer;
import sancho.model.mldonkey.utility.OpCodes;
import sancho.model.mldonkey.utility.Tag;
import sancho.model.mldonkey.utility.UtilityFactory;
import sancho.utility.SwissArmy;
import sancho.view.utility.SResources;

public class Server extends AObject {
  private static final String RS_UNKNOWN = SResources.getString("l.unknown");
  private static final String RS_HIGH_ID = SResources.getString("l.highID");
  private static final String RS_LOW_ID = SResources.getString("l.lowID");
  private static final String RS_TRUE = SResources.getString("l.true");
  private static final String RS_FALSE = SResources.getString("l.false");

  private Addr addr;
  private String description;
  private boolean preferred;
  private int id;
  private String name;
  private long numFiles;
  private long numUsers;
  private int port;
  private int score;
  private HostState state;
  private EnumHostState stateEnum;
  private Tag[] tagList;
  private Map userMap;
  private EnumNetwork networkEnum;

  Server(ICore core) {
    super(core);
    state = UtilityFactory.getHostState(core);
    addr = UtilityFactory.getAddr();
  }

  public synchronized void addUserInfo(User user) {
    getUserMap().put(user, null);
  }

  public void blacklist() {
    core.send(OpCodes.S_CONSOLE_MESSAGE, "bs " + getAddr().toString());
  }

  public void checkConnected(EnumHostState oldState) {
    EnumHostState newState = getStateEnum();
    if (oldState != null && oldState != newState) {
      if (newState == EnumHostState.CONNECTED)
        core.getServerCollection().setConnected(+1);
      else if (oldState == EnumHostState.CONNECTED)
        core.getServerCollection().setConnected(-1);
    }
  }

  private void checkRemovedState() {
    if (this.getStateEnum() == EnumHostState.REMOVE_HOST)
      core.getServerCollection().remove(this);
  }

  public void connect() {
    setState(EnumHostState.CONNECTING);
  }

  public void disconnect() {
    setState(EnumHostState.NOT_CONNECTED);
  }

  public boolean equals(Object obj) {
    return (obj instanceof Server && getId() == ((Server) obj).getId());
  }

  public Addr getAddr() {
    return addr;
  }

  public synchronized String getDescription() {
    if (description == null || name == null || name.equals(SResources.S_ES))
      return SResources.S_ES;
    else
      return description;
  }

  public String getHighLowIDString() {
    if (this.getEnumNetwork() == EnumNetwork.DONKEY && isConnected())
      return getState().getRank() == -2 ? RS_HIGH_ID : RS_LOW_ID;
    return SResources.S_ES;
  }

  public synchronized int getId() {
    return id;
  }

  public String getLink() {
    if (this.getEnumNetwork() == EnumNetwork.DONKEY)
      return "ed2k://|" + this.getName() + "|" + this.getAddr().toString() + "|" + this.getPort();

    return this.getAddr().toString() + ":" + this.getPort();
  }

  public synchronized String getName() {
    return name == null || name.equals(SResources.S_ES) ? RS_UNKNOWN : name;
  }

  public synchronized long getNumFiles() {
    return numFiles;
  }

  public synchronized long getNumUsers() {
    return numUsers;
  }

  public synchronized int getPort() {
    return port;
  }

  public synchronized int getScore() {
    return score;
  }

  public void getServerUsers() {
    core.send(OpCodes.S_GET_SERVER_USERS, new Integer(getId()));
  }

  public HostState getState() {
    return state;
  }

  public synchronized EnumHostState getStateEnum() {
    return stateEnum;
  }

  public Tag[] getTagList() {
    return tagList;
  }

  protected synchronized Map getUserMap() {
    if (userMap == null)
      userMap = Collections.synchronizedMap(new WeakHashMap());
    return userMap;
  }

  public synchronized Object[] getUsers() {
    return SwissArmy.toArray(getUserMap().keySet());
  }

  public int hashCode() {
    return getId();
  }

  public synchronized boolean hasUsers() {
    return !(userMap == null);
  }

  public boolean isConnected() {
    return this.getStateEnum() == EnumHostState.CONNECTED;
  }

  public boolean isDisconnected() {
    return this.getStateEnum() == EnumHostState.NOT_CONNECTED;
  }

  public synchronized boolean isPreferred() {
    return this.preferred;
  }

  public synchronized EnumNetwork getEnumNetwork() {
    return networkEnum;
  }

  public synchronized String getNetworkName() {
    return networkEnum.getName();
  }

  public synchronized Image getNetworkImage() {
    return networkEnum.getImage();
  }

  public String getPreferredString() {
    if (getEnumNetwork() == EnumNetwork.DONKEY)
      return isPreferred() ? RS_TRUE : RS_FALSE;
    return SResources.S_ES;
  }

  public void togglePreferred() {
    String msg = "preferred " + (isPreferred() ? "false" : "true") + " " + getAddr().toString();
    core.send(OpCodes.S_CONSOLE_MESSAGE, msg);
  }

  public void read(int serverID, int networkID, MessageBuffer messageBuffer) {
    EnumHostState oldState = getStateEnum();
    synchronized (this) {
      this.id = serverID;
      this.networkEnum = readNetworkEnum(networkID);
      this.addr.read(messageBuffer);
      this.port = readPort(messageBuffer);
      this.score = messageBuffer.getInt32();
      this.tagList = messageBuffer.getTagList();
      this.numUsers = readNUsers(messageBuffer);
      this.numFiles = readNFiles(messageBuffer);

      this.stateEnum = this.state.read(messageBuffer);
      this.name = messageBuffer.getString();
      this.description = messageBuffer.getString();
      this.preferred = readPreferred(messageBuffer);
    }
    this.checkConnected(oldState);
    this.checkRemovedState();
  }

  // guiEncoding#buf_server
  public void read(MessageBuffer messageBuffer) {
    read(messageBuffer.getInt32(), messageBuffer.getInt32(), messageBuffer);
  }

  protected EnumNetwork readNetworkEnum(int networkID) {
    return core.getNetworkCollection().getNetworkEnum(networkID);
  }

  protected boolean readPreferred(MessageBuffer messageBuffer) {
    return false;
  }

  protected long readNUsers(MessageBuffer messageBuffer) {
    return messageBuffer.getInt32();
  }

  protected long readNFiles(MessageBuffer messageBuffer) {
    return messageBuffer.getInt32();
  }

  protected int readPort(MessageBuffer messageBuffer) {
    return (int) (messageBuffer.getUInt16() & 0xFFFFL);
  }

  public void readUpdate(MessageBuffer messageBuffer) {
    EnumHostState oldState = getStateEnum();
    synchronized (this) {
      this.stateEnum = this.state.read(messageBuffer);
    }
    this.checkConnected(oldState);
    this.checkRemovedState();
  }

  public void remove() {
    setState(EnumHostState.REMOVE_HOST);
  }

  public void serverUser(MessageBuffer messageBuffer) {
    User user = (User) core.getUserCollection().get(messageBuffer.getInt32());
    if (user != null)
      addUserInfo(user);
  }

  public void setState(EnumHostState enumState) {
    short opCode = 0;

    if (enumState == EnumHostState.NOT_CONNECTED) {
      opCode = OpCodes.S_DISCONNECT_SERVER;
    } else if (enumState == EnumHostState.REMOVE_HOST) {
      opCode = OpCodes.S_REMOVE_SERVER;
    } else if (enumState == EnumHostState.CONNECTING || enumState == EnumHostState.CONNECTED) {
      opCode = OpCodes.S_CONNECT_SERVER;
    }

    if (opCode != 0)
      core.send(opCode, new Integer(this.getId()));

  }
}
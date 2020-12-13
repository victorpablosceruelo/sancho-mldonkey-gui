/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project. 
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;

import org.eclipse.swt.graphics.Image;

import sancho.core.ICore;
import sancho.model.mldonkey.enums.EnumNetwork;
import sancho.model.mldonkey.enums.EnumRoomState;
import sancho.model.mldonkey.utility.MessageBuffer;
import sancho.model.mldonkey.utility.OpCodes;
import sancho.utility.ObjectMap;
import sancho.view.utility.SResources;

public class Room extends AObject {
  protected EnumRoomState roomState;
  protected String name;
  protected int id;
  protected EnumNetwork networkEnum;
  protected ObjectMap userMap = null;

  Room(ICore core) {
    super(core);
  }

  public synchronized String getName() {
    return name != null ? name : SResources.S_ES;
  }

  public synchronized EnumRoomState getRoomState() {
    return roomState;
  }

  public int getNumUsers() {
    return -1;
  }

  public synchronized int getId() {
    return id;
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

  public void read(MessageBuffer messageBuffer) {
    read(messageBuffer.getInt32(), messageBuffer);
  }

  public void read(int roomNumber, MessageBuffer messageBuffer) {
    synchronized (this) {
      this.id = roomNumber;
      this.networkEnum = readNetworkEnum(messageBuffer);
      this.name = messageBuffer.getString();
      this.roomState = EnumRoomState.intToEnum(messageBuffer.getInt8());
    }
  }

  protected EnumNetwork readNetworkEnum(MessageBuffer messageBuffer) {
    return core.getNetworkCollection().getNetworkEnum(messageBuffer.getInt32());
  }

  public void addUser(User user) {
    getUserMap().add(user);
  }

  public void removeUser(User user) {
    getUserMap().remove(user);
  }

  public ObjectMap getUserMap() {
    if (userMap == null)
      userMap = new ObjectMap(true);
    return userMap;
  }

  public void close() {
    setRoomState(EnumRoomState.CLOSED);
  }

  public void open() {
    setRoomState(EnumRoomState.OPEN);
  }

  public void setRoomState(EnumRoomState enumRoomState) {
    Object[] oArray = new Object[2];
    oArray[0] = new Integer(getId());
    oArray[1] = new Byte(enumRoomState.getByteValue());
    core.send(OpCodes.S_SET_ROOM_STATE, oArray);
  }
}
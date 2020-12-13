/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;

import gnu.trove.TObjectProcedure;

import java.util.ArrayList;
import java.util.List;

import sancho.core.ICore;
import sancho.core.Sancho;
import sancho.model.mldonkey.enums.EnumRoomState;
import sancho.model.mldonkey.utility.MessageBuffer;
import sancho.model.mldonkey.utility.RoomMessage;
import sancho.model.mldonkey.utility.UtilityFactory;

public class RoomCollection extends ACollection_Int2 {

  RoomCollection(ICore core) {
    super(core);
  }

  public void addUser(MessageBuffer messageBuffer) {
    int roomNumber = messageBuffer.getInt32();
    int userNumber = messageBuffer.getInt32();

    if (containsKey(roomNumber)) {
      Room room = (Room) get(roomNumber);
      if (core.getUserCollection().containsKey(userNumber)) {
        room.addUser((User) core.getUserCollection().get(userNumber));
      } else {
        Sancho.pDebug("RDE");
      }
    }
  }

  public Room[] getAllOpenRooms() {
    GetAllOpenRooms getAllOpenRooms = new GetAllOpenRooms();
    forEachValue(getAllOpenRooms);
    Room[] roomArray = new Room[getAllOpenRooms.getRoomList().size()];
    getAllOpenRooms.getRoomList().toArray(roomArray);
    return roomArray;
  }

  public Room getRoom(int key) {
    return (Room) super.get(key);
  }

  public void read(MessageBuffer messageBuffer) {

    int roomNumber = messageBuffer.getInt32();
    Room room = (Room) get(roomNumber);
    if (room != null) {
      room.read(roomNumber, messageBuffer);
      addToUpdated(room);
    } else {
      room = core.getCollectionFactory().getRoom();
      room.read(roomNumber, messageBuffer);
      put(roomNumber, room);
      addToAdded(room);
    }

    this.setChanged();
    this.notifyObservers(room);
  }

  public void removeUser(MessageBuffer messageBuffer) {
    int roomNumber = messageBuffer.getInt32();
    int userNumber = messageBuffer.getInt32();
    if (containsKey(roomNumber)) {
      Room room = (Room) get(roomNumber);
      if (core.getUserCollection().containsKey(userNumber))
        room.removeUser((User) core.getUserCollection().get(userNumber));
    }
  }

  public void roomMessage(MessageBuffer messageBuffer) {
    RoomMessage roomMessage = UtilityFactory.getRoomMessage(core);
    roomMessage.read(messageBuffer);
    this.setChanged();
    this.notifyObservers(roomMessage);
  }

  static class GetAllOpenRooms implements TObjectProcedure {
    List roomList = new ArrayList();

    public boolean execute(Object object) {
      Room room = (Room) object;
      if (room.getRoomState() == EnumRoomState.OPEN)
        roomList.add(room);
      return true;
    }

    public List getRoomList() {
      return roomList;
    }
  }

}
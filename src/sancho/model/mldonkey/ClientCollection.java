/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;

import gnu.trove.TIntArrayList;
import gnu.trove.TIntObjectProcedure;
import gnu.trove.TLongIntHashMap;
import gnu.trove.TObjectProcedure;
import sancho.core.ICore;
import sancho.core.Sancho;
import sancho.model.mldonkey.enums.EnumClientType;
import sancho.model.mldonkey.utility.MessageBuffer;
import sancho.model.mldonkey.utility.OpCodes;
import sancho.utility.ObjectMap;

public class ClientCollection extends ACollection_Int {
  private TLongIntHashMap cleanHistoryMap = new TLongIntHashMap();
  private ObjectMap friendsWeakMap = new ObjectMap(true);
  private ObjectMap pendingWeakMap = new ObjectMap(true);
  private ObjectMap uploadersWeakMap = new ObjectMap(true);

  ClientCollection(ICore core) {
    super(core);
  }

  public void clean(MessageBuffer messageBuffer) {
    int[] iList = messageBuffer.getInt32List();
    synchronized (this) {
      cleanHistoryMap.put(System.currentTimeMillis(), iList.length);
    }
    retainEntries(new CleanIntMap(new TIntArrayList(iList)));
    cleanDeadClients();
  }

  public void cleanDeadClients() {
    retainEntries(new CleanDeadClients());
  }

  public static void removeAllFriends(ICore core) {
    core.send(OpCodes.S_REMOVE_ALL_FRIENDS);
  }

  public void clientFile(MessageBuffer messageBuffer) {
    int clientID = messageBuffer.getInt32();
    Client client = getClient(clientID);

    if (client != null) {
      boolean hadFiles = client.hasFiles();
      client.readClientFile(messageBuffer);
      if (!hadFiles)
        updateWeak(client);
    } else
      Sancho.pDebug("Client " + clientID + " not found");
  }

  public void dispose() {
    friendsWeakMap.deleteObservers();
    uploadersWeakMap.deleteObservers();
    forEachValue(new DisposeAll());
    super.dispose();
  }

  public Client getClient(int key) {
    return (Client) super.get(key);
  }

  public ObjectMap getFriendsWeakMap() {
    return friendsWeakMap;
  }

  public synchronized TLongIntHashMap getHistoryMap() {
    return cleanHistoryMap;
  }

  public ObjectMap getPendingWeakMap() {
    return pendingWeakMap;
  }

  public ObjectMap getUploadersWeakMap() {
    return uploadersWeakMap;
  }

  public void pending(MessageBuffer messageBuffer) {
    processMap(pendingWeakMap, messageBuffer);
  }

  // unsync'd now.. ok?
  public void processMap(ObjectMap map, MessageBuffer messageBuffer) {
    int[] clients = messageBuffer.getInt32List();
    for (int i = 0; i < clients.length; i++) {
      core.send(OpCodes.S_GET_CLIENT_INFO, new Integer(clients[i]));
      if (containsKey(clients[i])) {
        map.addOrUpdate(get(clients[i]));
      }
    }
    TIntArrayList intList = new TIntArrayList(clients);
    Object[] fullMap = map.getKeyArray();
    for (int i = 0; i < fullMap.length; i++) {
      Client client = (Client) fullMap[i];
      if (!intList.contains(client.getId())) {
        map.remove(client);
        if (client.countObservers() == 0) {
          removeSource(client.getId(), client);
        }
      }
    }
  }

  public void read(MessageBuffer messageBuffer) {
    int clientID = messageBuffer.getInt32();
    Client client = (Client) get(clientID);
    if (client != null) {
      client.read(clientID, messageBuffer);
    } else {
      client = core.getCollectionFactory().getClient();
      client.read(clientID, messageBuffer);
      this.put(clientID, client);
    }
    updateWeak(client);
  }

  public void readUpdate(MessageBuffer messageBuffer) {
    int key = messageBuffer.getInt32();
    if (containsKey(key))
      getClient(key).readUpdate(messageBuffer);
  }

  public void removeSource(int num, Client client) {
    if (!friendsWeakMap.containsKey(client) && !uploadersWeakMap.containsKey(client)
        && !pendingWeakMap.containsKey(client)) {
      remove(num);
    }
  }

  public void updateAvailability(MessageBuffer messageBuffer) {
    int fileID = messageBuffer.getInt32();
    int clientID = messageBuffer.getInt32();
    String availability = messageBuffer.getString();
    File file = (File) core.getFileCollection().get(fileID);
    Client client = (Client) get(clientID);
    if (client != null && file != null)
      client.putAvail(fileID, availability);
  }

  public void updateUploaders(ICore core) {
    synchronized (uploadersWeakMap) {
      Object[] oArray = uploadersWeakMap.getKeyArray();
      for (int i = 0; i < oArray.length; i++) {
        Client client = (Client) oArray[i];
        core.send(OpCodes.S_GET_CLIENT_INFO, new Integer(client.getId()));
      }
    }
  }

  public void updateFriends(Client client) {
    if (client.getEnumClientType() == EnumClientType.FRIEND)
      friendsWeakMap.addOrUpdate(client);
    else
      friendsWeakMap.remove(client);
  }

  public void updateWeak(Client client) {
    updateFriends(client);

    if (core.getProtocol() < 23) {
      if (client.isUploader() && client.isConnected())
        uploadersWeakMap.addOrUpdate(client);
      else
        uploadersWeakMap.remove(client);
    }
  }

  public void uploaders(MessageBuffer messageBuffer) {
    processMap(uploadersWeakMap, messageBuffer);
  }

  static class DisposeAll implements TObjectProcedure {
    public boolean execute(Object object) {
      ((Client) object).deleteObservers();
      return true;
    }
  }

  class CleanDeadClients implements TIntObjectProcedure {
    public boolean execute(int i, Object object) {
      Client client = (Client) object;
      if (client.countObservers() == 0) {
        if (!friendsWeakMap.containsKey(client) && !uploadersWeakMap.containsKey(client)
            && !pendingWeakMap.containsKey(client)) {
          return false;
        }
      }
      return true;
    }
  }
}
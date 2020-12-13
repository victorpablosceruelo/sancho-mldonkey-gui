/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;

import java.net.InetAddress;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TLongIntHashMap;
import gnu.trove.procedure.TIntObjectProcedure;
import gnu.trove.procedure.TObjectProcedure;
import sancho.core.ICore;
import sancho.core.Sancho;
import sancho.model.mldonkey.enums.EnumHostState;
import sancho.model.mldonkey.enums.EnumNetwork;
import sancho.model.mldonkey.utility.MessageBuffer;
import sancho.model.mldonkey.utility.OpCodes;
import sancho.view.preferences.PreferenceLoader;

public class ServerCollection extends ACollection_Int2 {

  private TLongIntHashMap cleanHistoryMap = new TLongIntHashMap();
  private boolean displayNodes;
  protected int numConnected;

  ServerCollection(ICore core) {
    super(core);
    updatePreferences();
  }

  public void addServer(Network network, InetAddress iNetAddress, short port) {
    if (iNetAddress == null || network == null)
      return;

    Object[] oArray = new Object[3];
    oArray[0] = new Integer(network.getId());
    oArray[1] = iNetAddress.getAddress();
    oArray[2] = new Short(port);

    core.send(OpCodes.S_ADD_SERVER, oArray);
  }

  public void addServerList(String url) {
    String type = url.toLowerCase().endsWith(".met") ? "server.met" : "ocl";
    core.send(OpCodes.S_CONSOLE_MESSAGE, "add_url " + type + " " + url);
  }

  public void clean(MessageBuffer messageBuffer) {
    int[] iList = messageBuffer.getInt32List();
    synchronized (this) {
      cleanHistoryMap.put(System.currentTimeMillis(), iList.length);
    }
    retainEntries(new CleanIntMap(new TIntArrayList(iList)));
    this.setChanged();
    this.notifyObservers(this);
  }

  public void cleanOldServers() {
    core.send(OpCodes.S_CLEAN_OLD_SERVERS);
  }

  public void connectMore() {
    core.send(OpCodes.S_CONNECT_MORE);
  }

  public int getConnected() {
    return numConnected;
  }

  public int getConnected(EnumNetwork networkEnum) {
    CountNetworkConnected countNetworkConnected = new CountNetworkConnected(networkEnum);
    forEachValue(countNetworkConnected);
    return countNetworkConnected.getCount();
  }

  public synchronized TLongIntHashMap getHistoryMap() {
    return cleanHistoryMap;
  }

  public Object[] getServers() {
    return getValues();
  }

  private void put(int id, Server server) {
    if (server == null)
      return;

    super.put(id, server);
    addToAdded(server);
    if (server.isConnected())
      setConnected(+1);
  }

  // guiEncoding#buf_server
  public void read(MessageBuffer messageBuffer) {
    int id = messageBuffer.getInt32();
    int networkID = messageBuffer.getInt32();

    Network network = (Network) this.core.getNetworkCollection().get(networkID);
    if (network == null)
      return;

    if (!displayNodes && !network.hasServers())
      return;

    Server server = (Server) get(id);

    if (server != null) {
      server.read(id, networkID, messageBuffer);
      if (server.getStateEnum() != EnumHostState.REMOVE_HOST)
        addToUpdated(server);
    } else {
      server = core.getCollectionFactory().getServer();
      server.read(id, networkID, messageBuffer);
      if (server.getStateEnum() != EnumHostState.REMOVE_HOST)
        this.put(server.getId(), server);
    }

    this.setChanged();
    this.notifyObservers();
  }

  public void readUpdate(MessageBuffer messageBuffer) {
    Server server = (Server) get(messageBuffer.getInt32());
    if (server != null) {
      server.readUpdate(messageBuffer);
      if (server.getStateEnum() != EnumHostState.REMOVE_HOST) {
        addToUpdated(server);
        this.setChanged();
        this.notifyObservers();
      }

    }
  }

  public Object remove(int key) {
    Server server = (Server) super.get(key);
    if (server != null && server.isConnected())
      setConnected(-1);
    return super.remove(key);
  }

  public void remove(Server server) {
    super.remove(server.getId());
    addToRemoved(server);
    this.setChanged();
    this.notifyObservers();
  }

  public void removeAll(EnumNetwork enumNetwork) {
    this.retainEntries(new RemoveNetworkServers(enumNetwork));
    this.setChanged();
    this.notifyObservers();
  }

  public boolean removeServerInfo(int key) {
    if (!containsKey(key))
      return false;
    else {
      ((Server) get(key)).setState(EnumHostState.REMOVE_HOST);
      return true;
    }
  }

  public void serverUser(MessageBuffer messageBuffer) {
    int id = messageBuffer.getInt32();

    Server server = (Server) get(id);
    if (server != null) {
      server.serverUser(messageBuffer);
      addToUpdated(server);
    } else {
      if (Sancho.debug)
        Sancho.pDebug("su-nf:" + id);
    }
  }

  public void setConnected(int i) {
    numConnected += i;
  }

  public void updatePreferences() {
    displayNodes = PreferenceLoader.loadBoolean("displayNodes");
  }

  static class CountNetworkConnected implements TObjectProcedure<Server> {
    private int counter;

    private EnumNetwork networkEnum;

    public CountNetworkConnected(EnumNetwork networkEnum) {
      this.networkEnum = networkEnum;
    }

    public boolean execute(Server object) {
      Server server = (Server) object;
      if (server.isConnected() && server.getEnumNetwork() == networkEnum)
        counter++;
      return true;
    }

    public int getCount() {
      return counter;
    }
  }

  static class RemoveNetworkServers implements TIntObjectProcedure<Object> {
    private EnumNetwork enumNetwork;

    public RemoveNetworkServers(EnumNetwork enumNetwork) {
      this.enumNetwork = enumNetwork;
    }

    public boolean execute(int i, Object object) {
      Server server = (Server) object;
      if (enumNetwork == server.getEnumNetwork())
        return false;
      return true;
    }
  }
}
/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;

import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.swt.graphics.Image;

import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import sancho.core.ICore;
import sancho.model.mldonkey.enums.AbstractEnum;
import sancho.model.mldonkey.enums.EnumClientMode;
import sancho.model.mldonkey.enums.EnumClientType;
import sancho.model.mldonkey.enums.EnumHostState;
import sancho.model.mldonkey.enums.EnumNetwork;
import sancho.model.mldonkey.utility.Addr;
import sancho.model.mldonkey.utility.HostState;
import sancho.model.mldonkey.utility.Kind;
import sancho.model.mldonkey.utility.MessageBuffer;
import sancho.model.mldonkey.utility.OpCodes;
import sancho.model.mldonkey.utility.Tag;
import sancho.model.mldonkey.utility.UtilityFactory;
import sancho.view.utility.SResources;

public class Client extends AObjectO {

  public static final String RS_TRANSFERRING = SResources.getString("l.transferring");
  public static final String RS_RANK = SResources.getString("l.rank");

  protected static final String S_Q = " (Q: ";

  public static final int CONNECTED = 1;
  public static final int DISCONNECTED = 2;
  public static final int TRANSFERRING_ADD = 4;
  public static final int TRANSFERRING_REM = 8;

  public static final int CHANGED_AVAIL = 16;
  public static final int READ_CLIENT_FILE = 32;

  public static final Integer iTA = new Integer(TRANSFERRING_ADD);
  public static final Integer iTAC = new Integer(TRANSFERRING_ADD | CONNECTED);
  public static final Integer iTR = new Integer(TRANSFERRING_REM);
  public static final Integer iTRD = new Integer(TRANSFERRING_REM | DISCONNECTED);
  public static final Integer iD = new Integer(DISCONNECTED);
  public static final Integer iC = new Integer(CONNECTED);

  public static final Integer iCHANGED_AVAIL = new Integer(CHANGED_AVAIL);
  public static final Integer iREAD_CLIENT_FILE = new Integer(READ_CLIENT_FILE);

  protected gnu.trove.impl.hash.THash avail;
  protected int chatPort;
  protected gnu.trove.map.hash.THashMap<String, Object> clientFilesMap;
  protected EnumClientType enumClientType;
  protected int id;
  protected Kind kind;
  protected String name;
  protected EnumNetwork networkEnum;
  protected int rating;
  protected HostState state;
  protected Tag[] tag;
  protected EnumHostState stateEnum;
  protected EnumClientMode clientModeEnum;

  Client(ICore core) {
    super(core);
    state = UtilityFactory.getHostState(core);
    kind = UtilityFactory.getKind(core);
  }

  public void addAsFriend() {
    core.send(OpCodes.S_ADD_CLIENT_FRIEND, new Integer(getId()));
  }

  public void connect() {
  }

  public void disconnect() {
  }

  public boolean equals(Object obj) {
    return (obj instanceof Client && getId() == ((Client) obj).getId());
  }

  public Addr getAddr() {
    return this.kind.getAddr();
  }

  private TIntObjectHashMap<Object> getAvailMap() {
    if (avail == null)
      avail = new TIntObjectHashMap<>();
    return (TIntObjectHashMap<Object>) avail;
  }

  public String getClientActivity() {
    if (this.getStateEnum() == EnumHostState.CONNECTED_DOWNLOADING)
      return RS_TRANSFERRING;
    else
      return RS_RANK + this.getStateRank();
  }

  public THashMap<String, Object> getClientFilesMap() {
    if (clientFilesMap == null)
      clientFilesMap = new THashMap();
    return clientFilesMap;
  }

  public synchronized Map getClientFilesResultMap(Object key) {
    if (clientFilesMap == null)
      return null;
    return (Map) clientFilesMap.get(key);
  }

  public synchronized EnumClientMode getClientModeEnum() {
    return clientModeEnum;
  }

  public int getConnectedTime() {
    return 0;
  }

  public String getConnectedTimeString() {
    return SResources.S_ES;
  }

  // public static StringBuffer stringBuffer = new StringBuffer();

  public String getDetailedClientActivity() {
    EnumHostState state = this.getStateEnum();
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(state.getName());

    if (state == EnumHostState.CONNECTED_DOWNLOADING && getStateFileNum() != -1) {
      stringBuffer.append(SResources.S_OB);
      stringBuffer.append(getState().getFileNum());
      stringBuffer.append(SResources.S_CB);
      return stringBuffer.toString();
    } else if (state == EnumHostState.CONNECTED_DOWNLOADING || this.getStateRank() <= 0)
      return stringBuffer.toString();
    else {
      stringBuffer.append(S_Q);
      stringBuffer.append(getState().getRank());
      stringBuffer.append(SResources.S_CB);
      return stringBuffer.toString();
    }
  }

  public long getDownloaded() {
    return 0;
  }

  public String getDownloadedString() {
    return SResources.S_ES;
  }

  public synchronized EnumClientType getEnumClientType() {
    return enumClientType;
  }

  public synchronized EnumNetwork getEnumNetwork() {
    return networkEnum;
  }

  public synchronized String getFileAvailability(int id) {
    return (String) getAvailMap().get(id);
  }

  public synchronized Object[] getFileDirectories() {
    return clientFilesMap.keySet().toArray();
  }

  public Map getFirstResultMap() {
    synchronized (this) {
      String key = (String) getFileDirectories()[0];
      return getClientFilesResultMap(key);
    }
  }

  public String getHash() {
    return this.kind.getHash();
  }

  public synchronized int getId() {
    return id;
  }

  public String getModeString() {
    return getClientModeEnum().getName();
  }

  public synchronized String getName() {
    return name != null ? name : SResources.S_ES;
  }

  public int getNumChunks(int id) {
    int numChunks = 0;
    String availability = getFileAvailability(id);

    if (availability != null) {
      for (int i = 0; i < availability.length(); i++)
        if (availability.charAt(i) == '1')
          numChunks++;
    }

    return numChunks;
  }

  public synchronized int getPort() {
    return this.kind.getPort();
  }

  public synchronized int getRating() {
    return rating;
  }

  public String getSoftware() {
    return SResources.S_ES;
  }

  public Image getSoftwareImage() {
    return null;
  }

  public synchronized HostState getState() {
    return state;
  }

  public synchronized EnumHostState getStateEnum() {
    return stateEnum;
  }

  public synchronized int getStateFileNum() {
    return state.getFileNum();
  }

  public synchronized int getStateRank() {
    return state.getRank();
  }

  public long getUploaded() {
    return 0;
  }

  public String getUploadedString() {
    return SResources.S_ES;
  }

  public String getUploadFilename() {
    return SResources.S_ES;
  }

  public synchronized boolean hasFiles() {
    return !(clientFilesMap == null);
  }

  public int hashCode() {
    return getId();
  }

  public boolean isConnected() {
    return isConnected(this.getStateEnum());
  }

  public boolean isConnected(AbstractEnum enumState) {
    return (enumState == EnumHostState.CONNECTED_DOWNLOADING
        || enumState == EnumHostState.CONNECTED_INITIATING || enumState == EnumHostState.CONNECTED_AND_QUEUED || enumState == EnumHostState.CONNECTED);
  }

  public boolean isTransferring() {
    return isTransferring(getStateEnum());
  }

  public boolean isTransferring(AbstractEnum state) {
    return state == EnumHostState.CONNECTED_DOWNLOADING;
  }

  public boolean isTransferring(int fileNum) {
    return isTransferring() && getStateFileNum() == fileNum;
  }

  public boolean isUploader() {
    return false;
  }

  public void onChangedState(AbstractEnum oldState) {
    this.setChanged();

    if (oldState != getStateEnum()) {
      if (isTransferring()) {
        if (isConnected(oldState))
          this.notifyObservers(iTA);
        else
          this.notifyObservers(iTAC);
      } else if (isTransferring(oldState)) {
        if (isConnected())
          this.notifyObservers(iTR);
        else
          this.notifyObservers(iTRD);
      } else {
        if (isConnected(oldState)) {
          if (isConnected()) {
            // this.notifyObservers();

          } else
            this.notifyObservers(iD);
        } else {
          if (isConnected())
            this.notifyObservers(iC);
          else {
            // this.notifyObservers();

          }
        }
      }
    }

    //else
    // this.notifyObservers(this);

    this.clearChanged();

  }

  public void putAvail(int fileId, String avail) {
    synchronized (this) {
      getAvailMap().put(fileId, avail);
    }
    this.setChanged();
    this.notifyObservers(iCHANGED_AVAIL);
  }

  public void read(int clientID, MessageBuffer messageBuffer) {
    AbstractEnum oldState = this.getStateEnum();
    AbstractEnum oldType = this.getEnumClientType();

    synchronized (this) {
      this.id = clientID;
      this.networkEnum = this.core.getNetworkCollection().getNetworkEnum(messageBuffer.getInt32());
      this.clientModeEnum = this.kind.read(messageBuffer);
      this.stateEnum = this.state.read(messageBuffer);
      this.enumClientType = EnumClientType.byteToEnum(messageBuffer.getByte());
      this.tag = messageBuffer.getTagList();
      this.name = messageBuffer.getString();
      this.rating = messageBuffer.getInt32();
      readMore(messageBuffer);
    }
    onChangedType(oldType);
    onChangedState(oldState);
  }

  protected void onChangedType(AbstractEnum oldType) {
    if (oldType != null && oldType != getEnumClientType())
      core.getClientCollection().updateFriends(this);
  }

  protected void readMore(MessageBuffer messageBuffer) {
    this.chatPort = messageBuffer.getInt32();
  }

  // guiEncoding#buf_client
  public void read(MessageBuffer messageBuffer) {
    read(messageBuffer.getInt32(), messageBuffer);
  }

  public void readClientFile(MessageBuffer messageBuffer) {

    String dirName = messageBuffer.getString();
    int resultNum = messageBuffer.getInt32();

    Result result = (Result) core.getResultCollection().getResult(resultNum);

    if (result == null)
      return;

    synchronized (this) { // TODO: this
      THashMap<String, Object> hashMap = getClientFilesMap();
      Map resultMap;

      if (hashMap.containsKey(dirName)) {
        resultMap = (WeakHashMap) hashMap.get(dirName);
      } else {
        resultMap = new WeakHashMap();
        hashMap.put(dirName, resultMap);
      }
      resultMap.put(result, null);
    }
    this.setChanged();
    this.notifyObservers(iREAD_CLIENT_FILE);
  }

  public void readUpdate(MessageBuffer messageBuffer) {
    AbstractEnum oldState = getStateEnum();
    synchronized (this) {
      this.stateEnum = this.state.read(messageBuffer);
    }
    onChangedState(oldState);
  }

  public void removeAsFriend() {
    core.send(OpCodes.S_REMOVE_FRIEND, new Integer(getId()));
  }

  public void requestClientFiles() {
    core.send(OpCodes.S_GET_CLIENT_FILES, new Integer(getId()));
  }

}
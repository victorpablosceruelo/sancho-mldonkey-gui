/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.core;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

import sancho.model.mldonkey.ClientCollection;
import sancho.model.mldonkey.ClientStats;
import sancho.model.mldonkey.CollectionFactory;
import sancho.model.mldonkey.ConsoleMessage;
import sancho.model.mldonkey.DefineSearchesCollection;
import sancho.model.mldonkey.FileCollection;
import sancho.model.mldonkey.NetworkCollection;
import sancho.model.mldonkey.OptionCollection;
import sancho.model.mldonkey.ResultCollection;
import sancho.model.mldonkey.RoomCollection;
import sancho.model.mldonkey.ServerCollection;
import sancho.model.mldonkey.SharedFileCollection;
import sancho.model.mldonkey.UserCollection;
import sancho.model.mldonkey.utility.ClientMessage;
import sancho.model.mldonkey.utility.MessageBuffer;
import sancho.model.mldonkey.utility.MessageEncoder;
import sancho.model.mldonkey.utility.OpCodes;
import sancho.model.mldonkey.utility.UtilityFactory;
import sancho.view.SharesTab;
import sancho.view.TransferTab;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.utility.AbstractTab;

public class MLDonkeyCore extends Observable implements ICore {

  protected static final int MAX_PROTOCOL = 30;
  protected int activeProtocol;
  protected AbstractTab activeTab;
  protected CollectionFactory collectionFactory;
  protected boolean connected;
  protected boolean connectionDenied;
  protected int coreProtocol;
  protected boolean initialized;
  protected boolean invalidPassword = true;
  protected int max_from_gui;
  protected int max_to_gui;
  protected MessageEncoder messageEncoder;
  protected MessageBuffer messageBuffer;
  protected String password;
  protected boolean pollMode;
  protected boolean pollPending;
  protected boolean pollUploaders;
  protected boolean pollUpStats;
  protected int pollDelay;
  protected Socket socket;
  protected Timer timer;
  protected String username;
  protected boolean semaphore;
  protected long lastPollForStats;
  protected int requestFileInfoDelay;
  protected long lastRequestFileInfos;
  protected int timerCounter;
  protected String mldonkeyVersion = "";
  protected int opCode;

  public MLDonkeyCore(Socket socket, String username, String password, boolean requestPollMode) {
    this.socket = socket;
    this.username = username;
    this.password = password;
    this.pollMode = requestPollMode;
    this.semaphore = false;
    this.messageEncoder = new MessageEncoder(socket);
    this.updatePreferences();
  }

  public void checkIfDenied() {
    if (!initialized) {
      connected = false;
      this.connectionDenied = true;
      this.semaphore = true;
    }
  }

  public synchronized void connect() {
    this.connected = true;
  }

  public synchronized void disconnect() {
    this.connected = false;
  }

  protected void enablePollMode() {
    Object[] oArray = new Object[3];
    oArray[0] = new Short((short) 1);
    oArray[1] = new Integer(1);
    oArray[2] = new Byte((byte) 1);
    send(OpCodes.S_GUI_EXTENSIONS, oArray);
  }

  public synchronized ClientCollection getClientCollection() {
    return this.getCollectionFactory().getClientCollection();
  }

  public synchronized ClientStats getClientStats() {
    return this.getCollectionFactory().getClientStats();
  }

  public synchronized CollectionFactory getCollectionFactory() {
    return this.collectionFactory;
  }

  public synchronized ConsoleMessage getConsoleMessage() {
    return this.getCollectionFactory().getConsoleMessage();
  }

  public synchronized DefineSearchesCollection getDefineSearchesCollection() {
    return this.getCollectionFactory().getDefineSearchesCollection();
  }

  public synchronized FileCollection getFileCollection() {
    return this.getCollectionFactory().getFileCollection();
  }

  public synchronized NetworkCollection getNetworkCollection() {
    return this.getCollectionFactory().getNetworkCollection();
  }

  public synchronized OptionCollection getOptionCollection() {
    return this.getCollectionFactory().getOptionCollection();
  }

  public int getProtocol() {
    return this.activeProtocol;
  }

  public synchronized ResultCollection getResultCollection() {
    return this.getCollectionFactory().getResultCollection();
  }

  public synchronized RoomCollection getRoomCollection() {
    return this.getCollectionFactory().getRoomCollection();
  }

  public synchronized ServerCollection getServerCollection() {
    return this.getCollectionFactory().getServerCollection();
  }

  public synchronized SharedFileCollection getSharedFileCollection() {
    return this.getCollectionFactory().getSharedFileCollection();
  }

  public synchronized UserCollection getUserCollection() {
    return this.getCollectionFactory().getUserCollection();
  }

  public String getCoreVersion() {
    return mldonkeyVersion != null ? mldonkeyVersion : "";
  }

  public synchronized boolean isConnected() {
    return this.connected;
  }

  public boolean initialized() {
    return initialized;
  }

  public boolean semaphore() {
    return semaphore;
  }

  public boolean isConnectionDenied() {
    return this.connectionDenied;
  }

  public boolean isInvalidPassword() {
    return this.invalidPassword;
  }

  public void notifyInitialized() {
    if (!initialized) {
      this.invalidPassword = false;
      this.semaphore = true;
      this.initialized = true;
    }
  }

  public void notifyObject(Object object) {
    this.setChanged();
    this.notifyObservers(object);
  }

  private void onIOException(IOException e) {
    this.disconnect();
    this.setChanged();
    this.notifyObservers(e);
  }

  private void pollForStats() {
    if (pollUpStats && SharesTab.class.isInstance(activeTab)) {
      send(OpCodes.S_REFRESH_UPLOAD_STATS);
    }
    if (TransferTab.class.isInstance(activeTab)) {
      if (getProtocol() >= 23) {
        if (pollUploaders)
          send(OpCodes.S_GET_UPLOADERS);
        if (pollPending)
          send(OpCodes.S_GET_PENDING);
      } else {
        if (pollUploaders && Sancho.hasCollectionFactory())
          getClientCollection().updateUploaders(this);
      }
    }
  }

  protected void readCoreProtocol(MessageBuffer messageBuffer) {
    coreProtocol = messageBuffer.getInt32();
    activeProtocol = Math.min(coreProtocol, MAX_PROTOCOL);
    if (pollMode)
      this.enablePollMode();
    this.sendPassword();
    this.sendInterestedInSources();
    this.sendGetVersion();
    this.collectionFactory = CollectionFactory.getFactory(activeProtocol, this);
    this.startTimer();
  }

  static int c;

  void processMessage(int opCode, MessageBuffer messageBuffer) throws Exception {

    switch (opCode) {
      case OpCodes.R_CORE_PROTOCOL :
        readCoreProtocol(messageBuffer);
        break;
      case OpCodes.R_CLIENT_STATS :
        if (!initialized)
          notifyInitialized();
        this.getClientStats().read(messageBuffer);

        break;
      case OpCodes.R_CLIENT_INFO :
        this.getClientCollection().read(messageBuffer);
        break;
      case OpCodes.R_CLIENT_STATE :
        this.getClientCollection().readUpdate(messageBuffer);
        break;
      case OpCodes.R_DEFINE_SEARCHES :
        this.getDefineSearchesCollection().read(messageBuffer);
        break;
      case OpCodes.R_RESULT_INFO :
        this.getResultCollection().resultInfo(messageBuffer);
        break;
      case OpCodes.R_SEARCH_RESULT :
        this.getResultCollection().read(messageBuffer);

        break;
      case OpCodes.R_SEARCH_WAITING :
        this.getResultCollection().searchWaiting(messageBuffer);
        break;
      case OpCodes.R_FILE_UPDATE_AVAILABILITY :
        this.getClientCollection().updateAvailability(messageBuffer);
        break;
      case OpCodes.R_FILE_ADD_SOURCE :
        this.getFileCollection().addSource(messageBuffer);
        break;
      case OpCodes.R_FILE_REMOVE_SOURCE :
        this.getFileCollection().removeSource(messageBuffer);
        break;
      case OpCodes.R_SERVER_STATE :
        this.getServerCollection().readUpdate(messageBuffer);
        break;
      case OpCodes.R_SERVER_USER :
        this.getServerCollection().serverUser(messageBuffer);
        break;
      case OpCodes.R_ROOM_INFO :
        this.getRoomCollection().read(messageBuffer);
        break;
      case OpCodes.R_SHARED_FILE_UPLOAD :
        this.getSharedFileCollection().upload(messageBuffer);
        break;
      case OpCodes.R_SHARED_FILE_INFO :
        this.getSharedFileCollection().read(messageBuffer);
        break;
      case OpCodes.R_SHARED_FILE_UNSHARED :
        this.getSharedFileCollection().unshared(messageBuffer);
        break;
      case OpCodes.R_FILE_DOWNLOAD_UPDATE :
        this.getFileCollection().update(messageBuffer);
        break;
      case OpCodes.R_DOWNLOAD :
        this.getFileCollection().add(messageBuffer);
        break;
      case OpCodes.R_CONSOLE :
        this.getConsoleMessage().read(messageBuffer);
        break;
      case OpCodes.R_NETWORK_INFO :
        this.getNetworkCollection().read(messageBuffer);
        break;
      case OpCodes.R_USER_INFO :
        this.getUserCollection().read(messageBuffer);
        break;
      case OpCodes.R_SERVER_INFO :
        this.getServerCollection().read(messageBuffer);
        break;
      case OpCodes.R_ROOM_MESSAGE :
        this.getRoomCollection().roomMessage(messageBuffer);
        break;
      case OpCodes.R_ROOM_ADD_USER :
        this.getRoomCollection().addUser(messageBuffer);
        break;
      case OpCodes.R_ROOM_REMOVE_USER :
        this.getRoomCollection().removeUser(messageBuffer);
        break;
      case OpCodes.R_DOWNLOADING_LIST :
        this.getFileCollection().read(messageBuffer);
        break;
      case OpCodes.R_DOWNLOADED_LIST :
        break;
      case OpCodes.R_UPLOADERS :
        this.getClientCollection().uploaders(messageBuffer);
        break;
      case OpCodes.R_PENDING :
        this.getClientCollection().pending(messageBuffer);
        break;
      case OpCodes.R_CLEAN_TABLES :
        this.getClientCollection().clean(messageBuffer);
        this.getServerCollection().clean(messageBuffer);
        this.getFileCollection().clean();
        break;
      case OpCodes.R_CLIENT_FILE :
        this.getClientCollection().clientFile(messageBuffer);
        break;
      case OpCodes.R_ADD_SECTION_OPTION :
        this.getOptionCollection().addSectionOption(messageBuffer);
        break;
      case OpCodes.R_ADD_PLUGIN_OPTION :
        this.getOptionCollection().addPluginOption(messageBuffer);
        break;
      case OpCodes.R_MESSAGE_FROM_CLIENT :
        ClientMessage clientMessage = UtilityFactory.getClientMessage(this);
        clientMessage.read(messageBuffer);
        this.setChanged();
        this.notifyObservers(clientMessage);
        break;
      case OpCodes.R_OPTIONS_INFO :
        if (!initialized)
          notifyInitialized();
        this.getOptionCollection().read(messageBuffer);
        break;
      case OpCodes.R_BAD_PASSWORD :
        disconnect();
        this.semaphore = true;
        break;
      case OpCodes.R_VERSION :
        mldonkeyVersion = messageBuffer.getString();
        break;
      default :
        break;
    }
  }

  public String getLastMessage() {
    if (messageBuffer == null)
      return "";

    return opCode + "/" + messageBuffer.getLastLength() + ": \n" + messageBuffer.getLastMessage();
  }

  public void run() throws RuntimeException {
    opCode = -1;
    try {
      messageBuffer = new MessageBuffer(this, new BufferedInputStream(socket.getInputStream()));
      this.sendProtocolVersion();

      while (connected) {
        opCode = messageBuffer.readMessage();
        this.processMessage(opCode, messageBuffer);
      }

    } catch (SocketException e) {
      e.printStackTrace();
      checkIfDenied();
    } catch (IOException e) {
      checkIfDenied();
      onIOException(e);
    } catch (Exception e) {
      String last = "";
      int lastl = -1;
      if (messageBuffer != null) {
        last = messageBuffer.getLastMessage();
        lastl = messageBuffer.getLastLength();
      }
      Sancho.threadException("Core(" + opCode + "/" + lastl + "/" + activeTab + ")\n\n" + last, e);
      onIOException(new IOException());
    }

    stopTimer();
    if (this.collectionFactory != null)
      CollectionFactory.dispose();
  }

  public void send(short opCode) {
    send(opCode, null);
  }

  public void send(short opCode, Object object) {
    send(opCode, new Object[]{object});
  }

  public void send(short opCode, Object[] oArray) {
    try {
      messageEncoder.send(opCode, oArray);
    } catch (IOException e) {
      onIOException(e);
    }
  }

  protected void sendInterestedInSources() {
    sendInterestedInSources(PreferenceLoader.loadBoolean("mldonkey.InterestedInSources"));
  }

  protected void sendInterestedInSources(boolean b) {
    if (getProtocol() < 27)
      return;
    send(OpCodes.S_INTERESTED_IN_SOURCES, new Byte((byte) (b ? 1 : 0)));
  }

  protected void sendPassword() {
    send(OpCodes.S_PASSWORD, new String[]{this.password, this.username});
  }

  protected void sendGetVersion() {
    if (getProtocol() > 29)
      send(OpCodes.S_GET_VERSION);
  }

  protected void sendProtocolVersion() {
    send(OpCodes.S_CORE_PROTOCOL, new Integer(MAX_PROTOCOL));
  }

  public void setActiveTab(AbstractTab tab) {
    this.activeTab = tab;
    pollForStats();
  }

  private void startTimer() {
    lastRequestFileInfos = System.currentTimeMillis();

    if (timer != null)
      timer.cancel();

    timer = new Timer();
    timer.scheduleAtFixedRate(new TimerTask() {
      public void run() {
        if (!isConnected())
          this.cancel();
        else {
          long currTime = System.currentTimeMillis();
          if (currTime > lastPollForStats + (pollDelay * 1000)) {
            pollForStats();
            lastPollForStats = currTime;
          }

          if (requestFileInfoDelay > 0 && currTime > lastRequestFileInfos + (requestFileInfoDelay * 1000)) {
            getFileCollection().requestAllFileInfos();
            lastRequestFileInfos = currTime;
          }

          if (timerCounter++ == 5)
            getClientCollection().cleanDeadClients();

          getFileCollection().sendUpdate();

        }
      }
    }, 0L, 1300);
  }

  private void stopTimer() {

    if (timer != null)
      timer.cancel();
  }

  public void updatePreferences() {
    pollUpStats = PreferenceLoader.loadBoolean("pollUpStats");
    pollUploaders = PreferenceLoader.loadBoolean("pollUploaders");
    pollPending = PreferenceLoader.loadBoolean("pollPending");
    pollDelay = PreferenceLoader.loadInt("pollDelay");
    requestFileInfoDelay = PreferenceLoader.loadInt("requestFileInfoDelay");

    sendInterestedInSources();

    if (getCollectionFactory() != null) {
      getServerCollection().updatePreferences();
      getFileCollection().updatePreferences();
      getResultCollection().updatePreferences();
    }
  }

}
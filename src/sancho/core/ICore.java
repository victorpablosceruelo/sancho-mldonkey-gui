/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.core;

import java.util.Observer;

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
import sancho.view.utility.AbstractTab;

public interface ICore extends Runnable {

  // Observable
  void addObserver(Observer o);
  void deleteObserver(Observer o);
  void deleteObservers();

  // Connection
  void connect();
  void disconnect();
  boolean initialized();
  boolean isInvalidPassword();
  boolean isConnectionDenied();
  boolean isConnected();
  boolean semaphore();
  int getProtocol();
  String getCoreVersion();
  String getLastMessage();
  
  // getCollections and Objects
  ClientCollection getClientCollection();
  ClientStats getClientStats();
  ConsoleMessage getConsoleMessage();
  DefineSearchesCollection getDefineSearchesCollection();
  FileCollection getFileCollection();
  CollectionFactory getCollectionFactory();
  NetworkCollection getNetworkCollection();
  OptionCollection getOptionCollection();
  ResultCollection getResultCollection();
  RoomCollection getRoomCollection();
  ServerCollection getServerCollection();
  SharedFileCollection getSharedFileCollection();
  UserCollection getUserCollection();

  // Send 
  void send(short opCode, Object[] oArray);
  void send(short opCode, Object object);
  void send(short opCode);

  // Gui interaction
  void setActiveTab(AbstractTab tab);
  void updatePreferences();
}

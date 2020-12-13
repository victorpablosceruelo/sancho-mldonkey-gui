/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;

import java.util.ArrayList;
import java.util.List;

import sancho.core.ICore;
import sancho.model.mldonkey.utility.SearchQuery;

public class CollectionFactory {
  protected static ICore core;
  protected static CollectionFactory factory;
  protected static int protocolVersion;

  protected IObject clientStats;
  protected IObject consoleMessage;

  protected ICollection clientCollection;
  protected ICollection defineSearchesCollection;
  protected ICollection fileCollection;
  protected ICollection networkCollection;
  protected ICollection optionCollection;
  protected ICollection resultCollection;
  protected ICollection roomCollection;
  protected ICollection serverCollection;
  protected ICollection sharedFileCollection;
  protected ICollection userCollection;

  protected List collectionList = new ArrayList();
  protected List objectList = new ArrayList();

  public void disposeAll() {
    for (int i = 0; i < collectionList.size(); i++)
      ((ICollection) collectionList.get(i)).dispose();

    for (int i = 0; i < objectList.size(); i++)
      ((IObject) objectList.get(i)).deleteObservers();
  }

  public Client getClient() {
    if (protocolVersion >= 23)
      return new Client23(core);
    else if (protocolVersion >= 21)
      return new Client21(core);
    else if (protocolVersion >= 20)
      return new Client20(core);
    else if (protocolVersion >= 19)
      return new Client19(core);

    return new Client(core);
  }

  public synchronized ClientCollection getClientCollection() {
    if (clientCollection == null) {
      clientCollection = new ClientCollection(core);
      collectionList.add(clientCollection);
    }
    return (ClientCollection) clientCollection;
  }

  public synchronized ClientStats getClientStats() {
    if (clientStats == null) {
      if (protocolVersion >= 18)
        clientStats = new ClientStats18(core);
      else
        clientStats = new ClientStats(core);
      objectList.add(clientStats);
    }
    return (ClientStats) clientStats;
  }

  public synchronized ConsoleMessage getConsoleMessage() {
    if (consoleMessage == null) {
      consoleMessage = new ConsoleMessage();
      objectList.add(consoleMessage);
    }
    return (ConsoleMessage) consoleMessage;
  }

  public synchronized DefineSearchesCollection getDefineSearchesCollection() {
    if (defineSearchesCollection == null) {
      defineSearchesCollection = new DefineSearchesCollection(core);
      collectionList.add(defineSearchesCollection);
    }
    return (DefineSearchesCollection) defineSearchesCollection;
  }

  public File getFile() {
    if (protocolVersion >= 25)
      return new File25(core);
    else if (protocolVersion >= 24)
      return new File24(core);
    else if (protocolVersion >= 21)
      return new File21(core);
    else if (protocolVersion >= 20)
      return new File20(core);
    else if (protocolVersion >= 18)
      return new File18(core);

    return new File(core);
  }

  public synchronized FileCollection getFileCollection() {
    if (fileCollection == null) {
      fileCollection = new FileCollection(core);
      collectionList.add(fileCollection);
    }
    return (FileCollection) fileCollection;
  }

  public Network getNetwork() {
    if (protocolVersion >= 18)
      return new Network18(core);

    return new Network(core);
  }

  public synchronized NetworkCollection getNetworkCollection() {
    if (networkCollection == null) {
      networkCollection = new NetworkCollection(core);
      collectionList.add(networkCollection);
    }
    return (NetworkCollection) networkCollection;
  }

  public Option getOption() {
    if (protocolVersion >= 17)
      return new Option17(core);

    return new Option(core);
  }

  public synchronized OptionCollection getOptionCollection() {
    if (optionCollection == null) {
      optionCollection = new OptionCollection(core);
      collectionList.add(optionCollection);
    }
    return (OptionCollection) optionCollection;
  }

  public Result getResult() {
    if (protocolVersion >= 27)
      return new Result27(core);
    else if (protocolVersion >= 25)
      return new Result25(core);

    return new Result(core);
  }

  public synchronized ResultCollection getResultCollection() {
    if (resultCollection == null) {
      resultCollection = new ResultCollection(core);
      collectionList.add(resultCollection);
    }
    return (ResultCollection) resultCollection;
  }

  public Room getRoom() {
    if (protocolVersion >= 3)
      return new Room3(core);

    return new Room(core);
  }

  public synchronized RoomCollection getRoomCollection() {
    if (roomCollection == null) {
      roomCollection = new RoomCollection(core);
      collectionList.add(roomCollection);
    }
    return (RoomCollection) roomCollection;
  }

  public SearchQuery getSearchQuery() {
    return new SearchQuery(core);
  }

  public Server getServer() {
    if (protocolVersion >= 29)
      return new Server29(core);
    else if (protocolVersion >= 28)
      return new Server28(core);

    return new Server(core);
  }

  public synchronized ServerCollection getServerCollection() {
    if (serverCollection == null) {
      serverCollection = new ServerCollection(core);
      collectionList.add(serverCollection);
    }
    return (ServerCollection) serverCollection;
  }

  public SharedFile getSharedFile() {
    if (protocolVersion >= 25)
      return new SharedFile25(core);

    return new SharedFile(core);
  }

  public synchronized SharedFileCollection getSharedFileCollection() {
    if (sharedFileCollection == null) {
      sharedFileCollection = new SharedFileCollection(core);
      collectionList.add(sharedFileCollection);
    }
    return (SharedFileCollection) sharedFileCollection;
  }

  public User getUser() {
    return new User(core);
  }

  public synchronized UserCollection getUserCollection() {
    if (userCollection == null) {
      userCollection = new UserCollection(core);
      collectionList.add(userCollection);
    }
    return (UserCollection) userCollection;
  }

  public static void dispose() {
    if (factory != null)
      factory.disposeAll();
    factory = null;
    System.gc();
  }

  public static CollectionFactory getFactory(int protocolVersion, ICore core) {
    CollectionFactory.protocolVersion = protocolVersion;
    CollectionFactory.core = core;

    if (factory == null)
      factory = new CollectionFactory();

    return factory;
  }
}
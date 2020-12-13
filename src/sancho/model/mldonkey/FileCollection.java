/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gnu.trove.procedure.TObjectProcedure;
import sancho.core.ICore;
import sancho.model.mldonkey.enums.EnumFileState;
import sancho.model.mldonkey.utility.MessageBuffer;
import sancho.model.mldonkey.utility.OpCodes;
import sancho.utility.SwissArmy;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.transfer.FileClient;
import sancho.view.utility.SResources;

public class FileCollection extends ACollection_Int2<File> {
  private static final String RS_ACTIVE = SResources.getString("l.active");
  private static final String RS_DOWNLOADED = SResources.getString("l.downloaded");
  private static final String RS_DOWNLOADS = SResources.getString("l.downloads");
  private static final String RS_PAUSED = SResources.getString("l.paused");
  private static final String RS_QUEUED = SResources.getString("l.queued");

  private static final String S_EMPTY_HASH = "00000000000000000000000000000000";

  private static StringBuffer stringBuffer = new StringBuffer();
  private List hashList = Collections.synchronizedList(new ArrayList());
  private long[] totalArray;
  private boolean eta2;
  private int updateDelay;
  private long lastUpdate;
  boolean requiresRefresh;

  FileCollection(ICore core) {
    super(core);
    totalArray = new long[12];
    updatePreferences();
  }

  public synchronized boolean requiresRefresh() {
    boolean r = requiresRefresh;
    requiresRefresh = false;
    return r;
  }

  public synchronized void setRequiresRefresh() {
    this.setChanged();
    requiresRefresh = true;
  }

  public void add(MessageBuffer messageBuffer) {
    int id = messageBuffer.getInt32();
    File file = getFile(id);
    if (file != null) {
      file.read(id, messageBuffer);
      if (!file.isActive())
        removeHash(file.getMd4());
      if (file.isInteresting())
        addToUpdated(file);
      else
        addToRemoved(file);
    } else {
      file = core.getCollectionFactory().getFile();
      file.read(id, messageBuffer);
      this.putFile(id, file);
      if (file.isActive())
        addHash(file.getMd4());
      if (file.isInteresting()) {
        addToAdded(file);
      } else
        addToRemoved(file);

    }
    this.setChanged();
  }

  public void addHash(String hash) {
    if (!containsHash(hash))
      hashList.add(hash.toUpperCase());
  }

  public void addSource(MessageBuffer messageBuffer) {
    int fileID = messageBuffer.getInt32();
    int clientID = messageBuffer.getInt32();
    
    Client client = (Client) core.getClientCollection().get(clientID);
    File file = (File) get(fileID);

    if (file != null && client != null) {
      file.addSource(client);
      addFileToUpdated(file);
    }
  }

  public void clean() {
    forEachValue(new ManualCleanAll());
    setRequiresRefresh();
  }

  public synchronized Object[] getAllInteresting() {
    GetAllInteresting gAI = new GetAllInteresting();
    forEachValue(gAI);
    clearAllLists();
    return gAI.getArray();
  }

  public void commitAll() {
    forEachValue(new CommitAll());
  }

  public boolean containsHash(String hash) {
    if (hash.equals(S_EMPTY_HASH))
      return false;
    return hashList.contains(hash.toUpperCase());
  }

  public void dispose() {
    forEachValue(new DisposeAll());
    super.dispose();
  }

  public void dllink(String url) {
    core.send(OpCodes.S_DLLINK, url);
  }

  public boolean eta2() {
    return eta2;
  }

  public File getFile(int id) {
    return (File) get(id);
  }

  public synchronized String getHeaderText() {
    stringBuffer.setLength(0);

    Object[] oArray = getValues();
    for (int i = 0; i < totalArray.length; i++)
      totalArray[i] = 0L;

    File file;
    for (int i = 0; i < oArray.length; i++) {
      file = (File) oArray[i];
      if (file.isInteresting()) {
        totalArray[0]++; // totalFiles

        if (file.getFileStateEnum() == EnumFileState.QUEUED) {
          totalArray[1] += file.getSize(); // queuedTotal
          totalArray[2] += file.getDownloaded(); // queuedDownloaded
          totalArray[3]++; // totalQueued
        } else if (file.getFileStateEnum() == EnumFileState.DOWNLOADED) {
          totalArray[4]++; // totalDownloaded
          totalArray[5] += file.getSize(); // downloadedTotal
        } else if (file.getFileStateEnum() == EnumFileState.PAUSED) {
          totalArray[6] += file.getSize(); // pausedTotal
          totalArray[7] += file.getDownloaded(); //pausedDownloaded
          totalArray[8]++; // totalPaused
        } else {
          totalArray[9] += file.getSize(); //activeTotal
          totalArray[10] += file.getDownloaded(); //activeDownloaded
          totalArray[11]++; // totalActive
        }
      }
    }

    stringBuffer.append(RS_DOWNLOADS);

    stringBuffer.append(SResources.S_OB);
    stringBuffer.append(totalArray[0]);
    stringBuffer.append(SResources.S_CB);

    stringBuffer.append(SResources.S_COLON);
    stringBuffer.append(totalArray[11]);
    stringBuffer.append(SResources.S_SPACE);
    stringBuffer.append(RS_ACTIVE);

    if (totalArray[11] > 0) {
      stringBuffer.append(SResources.S_OBS);
      stringBuffer.append(SwissArmy.calcStringSize(totalArray[10]));
      stringBuffer.append(SResources.S_SLASH2);
      stringBuffer.append(SwissArmy.calcStringSize(totalArray[9]));
      stringBuffer.append(SResources.S_CB);
    }

    if (totalArray[8] > 0) {
      stringBuffer.append(SResources.S_COMMA);
      stringBuffer.append(RS_PAUSED);
      stringBuffer.append(SResources.S_COLON);
      stringBuffer.append(totalArray[8]);
      stringBuffer.append(SResources.S_OBS);
      stringBuffer.append(SwissArmy.calcStringSize(totalArray[7]));
      stringBuffer.append(SResources.S_SLASH2);
      stringBuffer.append(SwissArmy.calcStringSize(totalArray[6]));
      stringBuffer.append(SResources.S_CB);
    }

    if (totalArray[3] > 0) {
      stringBuffer.append(SResources.S_COMMA);
      stringBuffer.append(RS_QUEUED);
      stringBuffer.append(SResources.S_COLON);
      stringBuffer.append(totalArray[3]);
      stringBuffer.append(SResources.S_OBS);
      stringBuffer.append(SwissArmy.calcStringSize(totalArray[2]));
      stringBuffer.append(SResources.S_SLASH2);
      stringBuffer.append(SwissArmy.calcStringSize(totalArray[1]));
      stringBuffer.append(SResources.S_CB);
    }

    if (totalArray[4] > 0) {
      stringBuffer.append(SResources.S_COMMA);
      stringBuffer.append(RS_DOWNLOADED);
      stringBuffer.append(SResources.S_COLON);
      stringBuffer.append(totalArray[4]);
      stringBuffer.append(SResources.S_OBS);
      stringBuffer.append(SwissArmy.calcStringSize(totalArray[5]));
      stringBuffer.append(SResources.S_CB);
    }

    return stringBuffer.toString();
  }

  public void putFile(int id, File file) {
    if (file.isInteresting())
      addHash(file.getMd4());
    put(id, file);
  }

  public void read(MessageBuffer messageBuffer) {
    int len = messageBuffer.getUInt16();

    this.clear();
    hashList.clear();

    for (int i = 0; i < len; i++) {
      File file = core.getCollectionFactory().getFile();
      file.read(messageBuffer);
      this.putFile(file.getId(), file);
      if (file.isActive())
        addHash(file.getMd4());

      if (file.isInteresting())
        addToAdded(file);
    }

    setRequiresRefresh();
  }

  public void removeHash(String hash) {
    hashList.remove(hash.toUpperCase());
  }

  public void setBrothers(int[] iArray) {
    String cmd = "set_brothers";
    for (int i = 0; i < iArray.length; i++) {
      cmd += " " + iArray[i];
    }
    core.send(OpCodes.S_CONSOLE_MESSAGE, cmd);
  }

  public void removeSource(MessageBuffer messageBuffer) {
    File file = getFile(messageBuffer.getInt32());

    if (file != null) {
      file.removeSource(messageBuffer);
      addFileToUpdated(file);
    }
  }

  public void requestAllFileInfos() {
    forEachValue(new RequestAllFileInfos());
  }

  public void sendUpdate() {
    if (hasChanged()) {
      if (updateDelay > 0) {
        long currTime = System.currentTimeMillis();
        if (lastUpdate + (updateDelay * 1000) > currTime)
          return;
        lastUpdate = currTime;
      }
      this.notifyObservers();
    }
  }

  public void sendUpdate(FileClient fileClient) {
    this.setChanged();
    this.notifyObservers(fileClient);
  }

  public void update(MessageBuffer messageBuffer) {
    File file = getFile(messageBuffer.getInt32());

    if (file != null) {
      file.readUpdate(messageBuffer);
      addToUpdated(file);
      this.setChanged();
    }
  }

  public void updatePreferences() {
    eta2 = PreferenceLoader.loadString("downloadTableColumns").indexOf("O") >= 0;
    updateDelay = PreferenceLoader.loadInt("updateDelay");

  }

  static class CommitAll implements TObjectProcedure<File> {
    public boolean execute(File object) {
      File file = (File) object;
      if (file.getFileStateEnum() == EnumFileState.DOWNLOADED)
        file.saveFileAs(file.getName());
      return true;
    }
  }

  static class DisposeAll implements TObjectProcedure<File> {
    public boolean execute(File object) {
      ((File) object).dispose();
      return true;
    }
  }

  static class ManualCleanAll implements TObjectProcedure<File> {
    public boolean execute(File object) {
      ((File) object).manualClean();
      return true;
    }
  }

  static class GetAllInteresting implements TObjectProcedure<File> {

    List arrayList;

    public GetAllInteresting() {
      this.arrayList = new ArrayList();
    }

    public Object[] getArray() {
      return arrayList.toArray();
    }

    public boolean execute(File object) {
      File file = (File) object;
      if (file.isInteresting())
        arrayList.add(file);
      return true;
    }
  }

  static class RequestAllFileInfos implements TObjectProcedure<File> {

    public boolean execute(File object) {
      File file = (File) object;
      if (file.getFileStateEnum() == EnumFileState.DOWNLOADING)
        file.requestFileInfo();
      return true;
    }
  }

  public void addFileToUpdated(File file) {
    addToUpdated(file);
    this.setChanged();
  }

}
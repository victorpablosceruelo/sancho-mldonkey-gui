/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.program.Program;

import sancho.core.CoreFactory;
import sancho.core.ICore;
import sancho.core.Sancho;
import sancho.model.mldonkey.enums.AbstractEnum;
import sancho.model.mldonkey.enums.EnumExtension;
import sancho.model.mldonkey.enums.EnumFileState;
import sancho.model.mldonkey.enums.EnumNetwork;
import sancho.model.mldonkey.enums.EnumPriority;
import sancho.model.mldonkey.utility.FileState;
import sancho.model.mldonkey.utility.Format;
import sancho.model.mldonkey.utility.MessageBuffer;
import sancho.model.mldonkey.utility.OpCodes;
import sancho.model.mldonkey.utility.UtilityFactory;
import sancho.utility.ObjectMap;
import sancho.utility.SwissArmy;
import sancho.utility.VersionInfo;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.transfer.FileClient;
import sancho.view.utility.SResources;
import sancho.view.utility.ResourcesImageDescriptor;

public class File extends AObjectO implements Observer {
  private static final String RS_DEFAULT_ICON = "defprog";

  public static final int CHANGED_ACTIVE = 1;
  public static final int CHANGED_RAVAIL = 2;
  public static final int CHANGED_DOWNLOADED = 4;
  public static final int CHANGED_ETA = 8;
  public static final int CHANGED_LAST = 16;
  public static final int CHANGED_PERCENT = 32;
  public static final int CHANGED_RATE = 64;
  public static final int CHANGED_STATE = 128;
  public static final int CHANGED_NOT_INTERESTING = 256;
  public static final int CHANGED_SOURCES = 512;
  public static final int CHANGED_AVAIL = 1024;

  /*
   public static final String CHANGED_ACTIVE = "active";
   public static final String CHANGED_AVAIL = "avail";
   public static final String CHANGED_DOWNLOADED = "downloaded";
   public static final String CHANGED_ETA = "eta";
   public static final String CHANGED_LAST = "last";
   public static final String CHANGED_PERCENT = "percent";

   public static final String CHANGED_RATE = "rate";
   public static final String CHANGED_STATE = "state";
   public static final String CHANGED_NOT_INTERESTING = "not_interesting";
   public static final String[] ALL_PROPERTIES = {CHANGED_RATE, CHANGED_DOWNLOADED, CHANGED_PERCENT,
   CHANGED_AVAIL, CHANGED_ETA, CHANGED_LAST, CHANGED_ACTIVE};
   */

  protected static final Set dummySet = new TreeSet();

  private int changedBits;
  protected int activeSources;
  protected long age;
  protected long ageTS;
  protected String avail;
  protected int[] chunkAges;
  protected String chunks;
  protected ObjectMap clientWeakMap;
  protected String comment;
  protected long downloaded;
  protected String downloadedString;
  protected long etaSeconds;
  protected long eta2Seconds;
  protected String etaString;
  protected String eta2String;
  protected EnumExtension extensionEnum;
  protected Set fileClientSet;
  protected EnumFileState fileStateEnum;
  protected Format format;
  protected int id;
  protected int lastSeen;
  protected String md4;
  protected String name;
  protected String[] names;
  //  protected Network network;
  protected EnumNetwork networkEnum;
  protected int numChunks;
  protected int numClients;
  protected int numConnectedClients;
  protected int percent;
  protected int priority;
  protected EnumPriority priorityEnum;
  protected String programImageResString;
  protected float rate;
  protected int relativeAvail = 0;
  protected long size;
  protected int sources;

  protected FileState state;

  File(ICore core) {
    super(core);
    state = UtilityFactory.getFileState(core);
    format = UtilityFactory.getFormat(core);

    clientWeakMap = new ObjectMap(true);
  }

  protected Set getFileClientSet() {
    if (fileClientSet == null)
      fileClientSet = Collections.synchronizedSet(new HashSet());

    return fileClientSet;
  }

  public synchronized void addChangedBits(int i) {
    this.changedBits |= i;
  }

  public synchronized void removeChangedBits(int i) {
    this.changedBits &= ~i;
  }

  public synchronized int getChangedBits() {
    return this.changedBits;
  }

  public synchronized void clearChangedBits() {
    this.changedBits = 0;
  }

  public synchronized boolean hasChangedBit(int i) {
    return (this.changedBits & i) != 0;
  }

  protected void addFileClient(FileClient fileClient) {
    getFileClientSet().add(fileClient);
    addChangedBits(CHANGED_SOURCES);
    notifyChangedProperties();
  }

  public void addSource(Client client) {

    if (!this.clientWeakMap.contains(client)) {
      this.clientWeakMap.add(client);
      client.addObserver(this);

      addChangedBits(CHANGED_SOURCES);

      if (client.isTransferring()) {
        numConnectedClients++;
        setActiveSources(+1);
        if (findFileClient(client) == null) {
          addClientToFileClientSet(client);
        }

      } else if (client.isConnected())
        numConnectedClients++;

      notifyChangedProperties();
    }

  }

  public void addClientToFileClientSet(Client client) {
    FileClient fileClient = new FileClient(this, client);
    addFileClient(fileClient);
    core.getFileCollection().sendUpdate(fileClient);
  }

  protected void calcDownloadedString() {
    String oldStringDownloaded = getDownloadedString();
    this.downloadedString = SwissArmy.calcStringSize(this.getDownloaded());

    if (!oldStringDownloaded.equals(downloadedString))
      addChangedBits(CHANGED_DOWNLOADED);

    int oldPercent = this.percent;
    this.percent = getSize() > 0 ? (int) ((float) this.getDownloaded() / (float) this.getSize() * 100f) : 0;

    if (oldPercent != this.percent)
      addChangedBits(CHANGED_PERCENT);

  }

  protected void calcETA() {
    if (this.rate == 0)
      this.etaSeconds = Long.MAX_VALUE;
    else
      this.etaSeconds = (long) ((getSize() - getDownloaded()) / (this.getRate() + 1));

    String oldStringETA = getEtaString();

    EnumFileState thisState = getFileStateEnum();

    boolean eta2 = core.getFileCollection().eta2();

    if ((thisState == EnumFileState.QUEUED) || (thisState == EnumFileState.DOWNLOADED)
        || (thisState == EnumFileState.PAUSED)) {
      this.etaString = SResources.S_DASH;
      if (eta2) {
        this.eta2Seconds = Long.MAX_VALUE;
        this.eta2String = SResources.S_DASH;
      }
    } else {
      this.etaString = SwissArmy.calcStringOfSeconds(this.etaSeconds);

      if (eta2) {
        long dl = getDownloaded();
        long remain = getSize() - dl;

        if (dl == 0 || remain == 0 || getAge() == 0) {
          this.eta2String = SResources.S_DASH;
          this.eta2Seconds = Long.MAX_VALUE;
        } else {
          if (ageTS == 0)
            ageTS = System.currentTimeMillis();
          long realAge = getAge() + ((System.currentTimeMillis() - ageTS) / 1000);
          this.eta2Seconds = (remain * realAge) / dl;
          this.eta2String = SwissArmy.calcStringOfSeconds(eta2Seconds);
        }
      }
    }
    if (etaString.equals(SResources.S_ES))
      etaString = SResources.S_DASH;

    if (!oldStringETA.equals(etaString))
      addChangedBits(CHANGED_ETA);

  }

  public void connectAll() {
    core.send(OpCodes.S_CONNECT_ALL, new Integer(this.getId()));
  }

  public void dispose() {
    clientWeakMap.deleteObservers();
    deleteObservers();
  }

  public boolean equals(Object obj) {
    return (obj instanceof File && getId() == ((File) obj).getId());
  }

  public synchronized FileClient findFileClient(Client client) {
    for (Iterator i = getFileClientSet().iterator(); i.hasNext();) {
      FileClient fileClient = (FileClient) i.next();
      if (client == fileClient.getClient())
        return fileClient;
    }
    return null;
  }

  public synchronized int getActiveSources() {
    return activeSources;
  }

  public synchronized long getAge() {
    return age;
  }

  public synchronized String getAgeString() {
    return SwissArmy.calcStringOfSeconds((System.currentTimeMillis() / 1000) - this.getAge());
  }

  public Set getAllAvailNetworks() {
    return dummySet;
  }

  public synchronized String getAvail() {
    return avail;
  }

  public String getAvails(Network network) {
    return SResources.S_ES;
  }

  public synchronized int[] getChunkAges() {
    return chunkAges;
  }

  public synchronized String getChunks() {
    return chunks != null ? chunks : SResources.S_ES;
  }

  public ObjectMap getClientWeakMap() {
    return clientWeakMap;
  }

  public String getComment() {
    return SResources.S_ES;
  }

  public synchronized int getConnected() {
    return numConnectedClients;
  }

  public synchronized long getDownloaded() {
    return downloaded;
  }

  public synchronized String getDownloadedString() {
    return downloadedString != null ? downloadedString : SResources.S_ES;
  }

  public synchronized EnumNetwork getEnumNetwork() {
    return networkEnum;
  }

  public String getED2K() {
    return "ed2k://|file|" + this.getName() + "|" + this.getSize() + "|" + this.getMd4() + "|/";
  }

  public synchronized long getETA() {
    return etaSeconds;
  }

  public synchronized long getETA2() {
    return eta2Seconds;
  }

  public synchronized String getEtaString() {
    return etaString != null ? etaString : SResources.S_ES;
  }

  public synchronized String getEta2String() {
    return eta2String != null ? eta2String : SResources.S_ES;
  }

  public synchronized int getFileClientSetSize() {
    if (fileClientSet == null)
      return 0;

    return getFileClientSet().size();
  }

  public synchronized Object[] getFileClientSetArray() {
    return getFileClientSet().toArray();
  }

  public String getFileFormat() {
    int index = this.getName().lastIndexOf(".");

    if (index != -1)
      return this.getName().substring(index + 1).toLowerCase();
    else
      return SResources.S_ES;
  }

  public void getFileLocations() {
    core.send(OpCodes.S_GET_FILE_LOCATIONS, new Integer(this.getId()));
  }

  public synchronized EnumFileState getFileStateEnum() {
    return fileStateEnum;
  }

  public synchronized EnumExtension getFileType() {
    return extensionEnum != null ? extensionEnum : EnumExtension.UNKNOWN;
  }

  public Format getFormat() {
    return format;
  }

  public synchronized int getId() {
    return id;
  }

  public synchronized int getLastSeen() {
    return lastSeen;
  }

  public synchronized String getLastSeenString() {
    if (this.lastSeen == 8640000)
      return SResources.S_DASH;
    else
      return SwissArmy.calcStringOfSeconds(this.lastSeen);
  }

  public synchronized String getMd4() {
    return md4 != null ? md4 : SResources.S_ES;
  }

  public synchronized String getName() {
    return name != null ? name : SResources.S_ES;
  }

  public String[] getNames() {
    return names != null ? names : new String[0];
  }

  //  public Network getNetwork() {
  //    return network;
  //  }

  public synchronized int getNumChunks() {
    return numChunks;
  }

  public synchronized int getNumClients() {
    return numClients;
  }
  
  public synchronized int getNumSources() {
    return sources;
  }

  public synchronized int getPercent() {
    return percent;
  }

  public synchronized int getPriority() {
    return priority;
  }

  public synchronized AbstractEnum getPriorityEnum() {
    return priorityEnum;
  }

  public synchronized String getProgramImageString() {
    return programImageResString != null ? programImageResString : SResources.S_ES;
  }

  public synchronized float getRate() {
    return rate;
  }

  public synchronized String getRateString() {
    EnumFileState thisState = getFileStateEnum();

    if (thisState == EnumFileState.PAUSED || thisState == EnumFileState.QUEUED
        || thisState == EnumFileState.DOWNLOADED)
      return thisState.getName();
    else
      return getRate() == 0f ? SResources.S_DASH : String
          .valueOf((Math.round(10.0 * (getRate() / 1000f)) / 10.0));
  }

  public synchronized int getRelativeAvail() {
    return relativeAvail;
  }

  public synchronized String getRelativeAvailString() {
    return relativeAvail + SResources.S_PERCENT;
  }

  public synchronized long getSize() {
    return size;
  }

  public synchronized String getSizeString() {
    return SwissArmy.calcStringSize(this.getSize());
  }

  public int getSources() {
    // TODO: check mldonkey sources
    return clientWeakMap.size();
  }

  public String getSourcesString() {
    int s = getSources();

    if (s == 0)
      return SResources.S_DASH;
    else {
      int a = getActiveSources();
      return (a > 0 ? (s + SResources.S_OB + a + SResources.S_CB) : String.valueOf(s));
    }
  }

  public synchronized String getPercentString() {
    return getPercent() + SResources.S_PERCENT;
  }

  public synchronized String getPriorityString() {
    if (priority == 0)
      return priorityEnum.getName();
    else
      return priorityEnum.getName() + SResources.S_OB + priority + SResources.S_CB;
  }

  public boolean hasAvails() {
    return false;
  }

  public int hashCode() {
    return getId();
  }

  public boolean isActive() {
    EnumFileState thisState = getFileStateEnum();
    return (thisState == EnumFileState.DOWNLOADING) || (thisState == EnumFileState.PAUSED)
        || (thisState == EnumFileState.QUEUED);
  }

  public boolean isInteresting() {
    EnumFileState thisState = getFileStateEnum();
    return thisState == EnumFileState.DOWNLOADING || thisState == EnumFileState.PAUSED
        || thisState == EnumFileState.DOWNLOADED || thisState == EnumFileState.QUEUED;
  }

  public void manualClean() {
    synchronized (clientWeakMap) {
      Object[] oArray = clientWeakMap.getKeyArray();
      for (int i = 0; i < oArray.length; i++) {
        Client c = (Client) oArray[i];
        if (!core.getClientCollection().containsKey(c.getId()))
          clientWeakMap.removeFromMain(c);
      }
    }
  }

  public void notifyChangedProperties() {
    this.setChanged();
    this.notifyObservers();

    // only observers are ChunkCanvases
    removeChangedBits(CHANGED_AVAIL);
    //  this.notifyObservers(new String[]{"z"});
  }

  public void preview() {
    preview(null);
  }

  public void preview(String app) {

    String previewExecutable = PreferenceLoader.loadString("previewExecutable");
    String previewWorkingDirectory = PreferenceLoader.loadString("previewWorkingDirectory");
    boolean previewUseHttp = PreferenceLoader.loadBoolean("previewUseHttp");

    String previewExtensions = PreferenceLoader.loadString("previewExtensions");
    if (!previewExtensions.equals(SResources.S_ES)) {
      StringTokenizer st = new StringTokenizer(previewExtensions, ";");
      int ct = st.countTokens();

      String ext = SResources.S_ES;
      String prog = SResources.S_ES;

      while (st.hasMoreTokens()) {
        ext = st.nextToken();
        if (st.hasMoreTokens()) {
          prog = st.nextToken();
          if (getName().toLowerCase().endsWith(ext.toLowerCase())) {
            previewExecutable = prog;
          }
        }
      }
    }

    if (app != null)
      previewExecutable = app;

    CoreFactory coreFactory = Sancho.getCoreFactory();

    String httpPort = SResources.S_ES;
    Option option = (Option) core.getOptionCollection().get("http_port");
    if (option != null)
      httpPort = option.getValue();

    String userPass = SResources.S_ES;

    if (!coreFactory.getPassword().equals(SResources.S_ES))
      userPass = coreFactory.getUsername() + ":" + coreFactory.getPassword() + "@";

    String httpString = "http://" + userPass + coreFactory.getHostname() + ":" + httpPort
        + "/preview_download?q=" + getId();

    String fileString = getEnumNetwork().getTempFilePrefix() + getMd4().toUpperCase();

    if (previewExecutable.equals(SResources.S_ES)) {
      core.send(OpCodes.S_PREVIEW, new Integer(this.getId()));
    } else {
      String[] cmdArray = new String[2];

      cmdArray[0] = previewExecutable;
      cmdArray[1] = previewUseHttp ? httpString : fileString;

      SwissArmy.execInThread(cmdArray, previewWorkingDirectory.equals(SResources.S_ES)
          ? null
          : previewWorkingDirectory);
    }
  }

  // buf_file
  public void read(int fileID, MessageBuffer messageBuffer) {
    synchronized (this) {
      this.id = fileID;
      this.networkEnum = readNetwork(messageBuffer.getInt32());
      this.names = messageBuffer.getStringList();
      this.md4 = messageBuffer.getMd4();
      this.size = readSize(messageBuffer);
      this.downloaded = readDownloaded(messageBuffer);
      calcDownloadedString();
      this.sources = messageBuffer.getInt32();
      this.numClients = messageBuffer.getInt32();
      this.readState(messageBuffer);
      setChunks(messageBuffer.getString());
      readAvailability(messageBuffer);
      readRate(messageBuffer);
      this.chunkAges = readChunkAges(messageBuffer);
      this.age = readAge(messageBuffer);
      this.getFormat().read(messageBuffer);
      this.name = messageBuffer.getString();
      setLastSeen(messageBuffer.getInt32());
      this.setPriority(messageBuffer.getSignedInt32());
      this.comment = readComment(messageBuffer);
      calcETA();
      calcFileType();
      if (programImageResString == null)
        setProgramImage();
    }

    // Not sync:

    if ((this.getChangedBits() & CHANGED_STATE) != 0 && this.state.getState() == EnumFileState.DOWNLOADED)
      Sancho.getCoreFactory().notifyObject(this);

    notifyChangedProperties();
  }

  public void read(MessageBuffer messageBuffer) {
    read(messageBuffer.getInt32(), messageBuffer);
  }

  protected long readAge(MessageBuffer messageBuffer) {
    if (core.getFileCollection().eta2())
      ageTS = System.currentTimeMillis();

    try {
      return Long.parseLong(messageBuffer.getString());
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  protected int[] readChunkAges(MessageBuffer messageBuffer) {
    String[] chunkAgesList = messageBuffer.getStringList();
    int[] iArray = new int[chunkAgesList.length];
    for (int i = 0; i < chunkAgesList.length; i++)
      iArray[i] = ((int) (System.currentTimeMillis() / 1000) - Integer.parseInt(chunkAgesList[i]));

    return iArray;
  }

  protected String readComment(MessageBuffer messageBuffer) {
    return SResources.S_ES;
  }

  protected long readDownloaded(MessageBuffer messageBuffer) {
    return messageBuffer.getInt32() & 0xFFFFFFFFL;
  }

  protected void readRate(MessageBuffer messageBuffer) {
    float newRate = 0f;

    try {
      newRate = Float.parseFloat(messageBuffer.getString());
    } catch (NumberFormatException e) {
    }

    if (this.rate != newRate)
      addChangedBits(CHANGED_RATE);
    this.rate = newRate;
  }

  protected long readSize(MessageBuffer messageBuffer) {
    return messageBuffer.getInt32() & 0xFFFFFFFFL;
  }

  protected void readState(MessageBuffer messageBuffer) {
    boolean wasInteresting = isInteresting();

    EnumFileState oldState = this.getFileStateEnum();
    this.state.read(messageBuffer);
    this.fileStateEnum = state.getState();

    if (wasInteresting && !isInteresting())
      addChangedBits(CHANGED_NOT_INTERESTING);

    if (oldState != this.getFileStateEnum()) {
      addChangedBits(CHANGED_STATE);

      // On file completion
      if (this.state.getState() == EnumFileState.DOWNLOADED) {
        // can't be synced: 
        //Sancho.getCoreFactory().notifyObject(this);
        if (PreferenceLoader.loadBoolean("downloadCompleteLog") && getName() != null
            && !getName().equals(SResources.S_ES)) {
          try {
            PrintWriter p = new PrintWriter(new BufferedWriter(new FileWriter(new java.io.File(VersionInfo
                .getDownloadLogFile()), true)));
            p.write(System.currentTimeMillis() + " " + getED2K() + "\n");
            p.close();

          } catch (FileNotFoundException fnf) {
            Sancho.pDebug(fnf.toString());
          } catch (IOException io) {
            Sancho.pDebug(io.toString());
          }
        }
      }
    }
  }

  public void readUpdate(MessageBuffer messageBuffer) {
    synchronized (this) {
      this.downloaded = readDownloaded(messageBuffer);
      calcDownloadedString();
      readRate(messageBuffer);
      setLastSeen(messageBuffer.getInt32());
      calcETA();
    }
    notifyChangedProperties();
  }

  public FileClient removeFileClient(Client client) {
    FileClient foundFileClient = findFileClient(client);

    if (foundFileClient != null) {
      getFileClientSet().remove(foundFileClient);
      addChangedBits(CHANGED_SOURCES);
      notifyChangedProperties();
    }

    return foundFileClient;
  }

  public void removeSource(MessageBuffer messageBuffer) {

    int clientNum = messageBuffer.getInt32();
    
    Client client = (Client) core.getClientCollection().get(clientNum);
    if (client != null) {
      clientWeakMap.remove(client);
      client.deleteObserver(this);
      if (client.countObservers() == 0) {
        if (client.isConnected() && numConnectedClients > 0)
          numConnectedClients--;
        core.getClientCollection().removeSource(clientNum, client);
      }
      addChangedBits(CHANGED_SOURCES);
      notifyChangedProperties();
    }

  }

  public void saveFileAs(String aName) {
    Object[] oArray = new Object[2];
    oArray[0] = new Integer(this.getId());
    oArray[1] = aName;

    core.send(OpCodes.S_SAVE_FILE_AS, oArray);
  }

  public void sendPriority(boolean relative, int i) {
    Object[] oArray = new Object[2];
    oArray[0] = new Integer(this.getId());

    if (relative)
      i += getPriority();

    oArray[1] = new Integer(i);

    core.send(OpCodes.S_SET_FILE_PRIO, oArray);
  }

  public void sendPriority(EnumPriority enumPriority) {
    sendPriority(false, enumPriority.getMaxValue());
  }

  public void setActiveSources(int i) {
    int oldActiveSources = getActiveSources();

    synchronized (this) {
      if (i == 0) {
        activeSources = 0;
        Object[] oArray = clientWeakMap.getKeyArray();
        for (int j = 0; j < oArray.length; j++) {
          Client client = (Client) oArray[j];
          if (client.isTransferring())
            activeSources++;
        }
      } else
        activeSources += i;
    }

    if (oldActiveSources != getActiveSources()) {
      addChangedBits(CHANGED_ACTIVE);
      notifyChangedProperties();
    }
  }

  // changed_avail
  protected void readAvailability(MessageBuffer messageBuffer) {
    this.avail = messageBuffer.getString();
    setRelativeAvail();
  }

  protected void setChunks(String s) {
    this.chunks = s;
    numChunks = 0;

    char tempChar;

    for (int i = 0; i < chunks.length(); i++) {
      tempChar = chunks.charAt(i);
      if ((tempChar == '2') || (tempChar == '3'))
        numChunks++;
    }
  }

  public void setComment(String comment) {
    String string = "comment " + this.getMd4() + " \"" + comment + "\"";
    core.send(OpCodes.S_CONSOLE_MESSAGE, string);
    core.send(OpCodes.S_GET_FILE_INFO, new Integer(getId()));
  }

  public void requestFileInfo() {
    core.send(OpCodes.S_GET_FILE_INFO, new Integer(getId()));
  }

  protected void calcFileType() {
    int index = getName().lastIndexOf(".");
    String extension = name.substring(index + 1, name.length());
    EnumExtension enumExtension = EnumExtension.GET_EXT(extension);
    this.extensionEnum = enumExtension != null ? enumExtension : EnumExtension.UNKNOWN;
  }

  protected void setLastSeen(int i) {
    int oldLastSeen = getLastSeen();
    this.lastSeen = i;
    if (oldLastSeen != lastSeen)
      addChangedBits(CHANGED_LAST);
  }

  public void rename(String string) {
    string = "rename " + this.getId() + " \"" + string + "\"";
    core.send(OpCodes.S_CONSOLE_MESSAGE, string);
    core.send(OpCodes.S_GET_FILE_INFO, new Integer(id));
  }

  protected EnumNetwork readNetwork(int i) {
    return this.core.getNetworkCollection().getNetworkEnum(i);
  }

  protected void setPriority(int i) {
    priority = i;
    priorityEnum = EnumPriority.intToEnum(i);
  }

  public void setProgramImage() {
    Program p = null;

    if (!this.getFileFormat().equals(SResources.S_ES))
      p = Program.findProgram(this.getFileFormat());
    else {
      int index;
      String fileName = this.getName();

      if ((fileName != null) && ((index = fileName.lastIndexOf(".")) != -1))
        p = Program.findProgram(fileName.substring(index));
    }
    // Set the program image:
    Image programImage = null;

    if (p != null) {
      if ((programImage = SResources.getImage(p.getName())) == null) {
        ImageData data = p.getImageData();

        if (data != null) {
          ResourcesImageDescriptor rID = new ResourcesImageDescriptor(p.getName(), new Image(null, data));
          SResources.putImage(p.getName(), rID);
          programImageResString = p.getName();
        } else
          programImageResString = RS_DEFAULT_ICON;
      } else
        programImageResString = p.getName();

    } else
      programImageResString = RS_DEFAULT_ICON;
  }

  public void setRelativeAvail() {
    int oldRelativeAvail = relativeAvail;
    relativeAvail = 0;

    int neededChunks = 0;
    int availChunks = 0;

    if (avail == null)
      avail = SResources.S_ES;

    String myChunks = getChunks();

    if ((avail.length() > 0) && (avail.length() == myChunks.length())) {
      for (int i = 0; i < avail.length(); i++) {
        if ((myChunks.charAt(i) == '0') || (myChunks.charAt(i) == '1')) {
          neededChunks++;

          if (avail.charAt(i) > 0)
            availChunks++;
        }
      }
      if (neededChunks > 0)
        relativeAvail = (int) (((float) availChunks / (float) neededChunks) * 100f);
    }

    if (oldRelativeAvail != relativeAvail)
      addChangedBits(CHANGED_RAVAIL);
  }

  public void setState(EnumFileState newState) {
    EnumFileState oldState = this.getFileStateEnum();

    Object[] content = new Object[2];
    short opcode = OpCodes.S_SWITCH_DOWNLOAD;
    content[0] = new Integer(id);

    if (oldState == EnumFileState.PAUSED && newState == EnumFileState.DOWNLOADING) {
      content[1] = new Byte((byte) 1);
    } else if ((oldState == EnumFileState.DOWNLOADING || oldState == EnumFileState.QUEUED)
        && newState == EnumFileState.PAUSED) {
      content[1] = new Byte((byte) 0);
    } else if (newState == EnumFileState.CANCELLED) {
      opcode = OpCodes.S_REMOVE_DOWNLOAD;
      content = new Object[1];
      content[0] = new Integer(id);
    } else {
      return;
    }
    core.send(opcode, content);
  }

  protected boolean checkFileNum(Client client) {
    return true;
  }

  // fileClients aren't updated(notified) as clients are updated

  public void update(Observable o, Object obj) {
    if (o instanceof Client) {
      if (obj instanceof Integer) {
        Client client = (Client) o;
        int nInt = ((Integer) obj).intValue();

        if ((nInt & Client.TRANSFERRING_ADD) != 0) {
          if (checkFileNum(client)) {
            setActiveSources(+1);

            FileClient fileClient = findFileClient(client);

            if (fileClient == null) {
              fileClient = new FileClient(this, client);
              addFileClient(fileClient);
            }

            //this.setChanged();
            // this.notifyObservers(fileClient);
            core.getFileCollection().sendUpdate(fileClient);
          }
        } else if ((nInt & Client.TRANSFERRING_REM) != 0) {
          FileClient foundFileClient;

          // Is fileNum avail here?
          if ((foundFileClient = removeFileClient(client)) != null) {
            foundFileClient.setDelete();
            setActiveSources(-1);
            //  this.setChanged();
            //  this.notifyObservers(foundFileClient);
            core.getFileCollection().sendUpdate(foundFileClient);
          }
        }

        if ((nInt & Client.CONNECTED) != 0)
          numConnectedClients++;
        else if ((nInt & Client.DISCONNECTED) != 0 && numConnectedClients > 0)
          numConnectedClients--;

      }
      //else {
      //this.setChanged();
      //this.notifyObservers(obj);
      //}
      clientWeakMap.addOrUpdate(o);
    } //else
  }

  public void verifyChunks() {
    core.send(OpCodes.S_VERIFY_ALL_CHUNKS, new Integer(this.getId()));
  }

}
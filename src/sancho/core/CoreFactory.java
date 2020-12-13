/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.core;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import sancho.utility.SwissArmy;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.utility.SResources;
import sancho.view.utility.Splash;
import sancho.view.utility.setupWizard.SetupWizard;
import sancho.view.utility.setupWizard.SetupWizardDialog;

public class CoreFactory extends Observable implements Observer, Runnable {
  public static final int CLOSE = 1;
  public static final int OK = 0;
  public static final int RETRY = 2;
  private boolean automated;
  protected boolean autoReconnecting;
  private long connectedAt;

  private boolean brc;
  private int irc;

  private int connectRC;
  private ICore core;
  protected Display display;
  private boolean hasConnected;
  private String hostname;

  private int numRetries;
  private String password;
  protected int port;
  private int result;
  private Socket socket;
  private String username;
  private boolean wantToConnect;

  public CoreFactory(Display display) {
    this.display = display;
    this.connectRC = -1;
    this.automated = false;
  }

  public int connect() {
    int rc;

    while ((rc = startCore()) == CoreFactory.RETRY) {
      numRetries++;
      updateSplash("splash.connecting");

      int delay = PreferenceLoader.loadInt("autoReconnectDelay");
      while (autoReconnecting && delay > 0) {
        SwissArmy.threadSleep(1000);
        this.notifyObject("[" + delay-- + "] " + SResources.getString("l.waitingToReconnect"));
      }
      if (!autoReconnecting) {
        rc = OK;
        this.setChanged();
        this.notifyObservers(SResources.S_ES);
      }
    }

    return rc;
  }

  protected boolean createYesNoBox(final String text, final String message) {
    brc = false;
    display.syncExec(new Runnable() {
      public void run() {
        Splash.setVisible(false);
        brc = openQuestion(null, text, message);
      }
    });
    return brc;
  }

  private boolean createResYesNoBox(String resText, String resMessage) {
    return createYesNoBox(SResources.getString(resText), SResources.getString(resMessage));
  }

  public void disconnect() {
    if (core != null) {
      core.disconnect();
      if (core instanceof MLDonkeyCore)
        ((MLDonkeyCore) core).deleteObservers();
      setDisconnected();
    }
  }

  protected int errorHandling(String text, String message) {
    if (!automated) {
      if (!createYesNoBox(text, message))
        return CLOSE;
    }
    if (!setupWizard())
      return CLOSE;

    return RETRY;
  }

  protected synchronized int getConnectRC() {
    return connectRC;
  }

  public ICore getCore() {
    return core;
  }

  public String getHostname() {
    return this.hostname;
  }

  public int getNumRetries() {
    return numRetries;
  }

  public String getPassword() {
    return this.password;
  }

  public Socket getSocket() {
    return socket;
  }

  public String getUptime() {
    return SwissArmy.calcUptime(connectedAt);
  }

  public String getUsername() {
    return this.username;
  }

  public void initialize() {
    readPreferences(0, false);
    Thread thread = new Thread(this);
    thread.setDaemon(true);
    thread.start();
  }

  public int initializeSocket() {

    updateSplash("splash.initializeSocket", SResources.S_ES, 0);

    try {
      socket = new Socket(hostname, port);
    } catch (UnknownHostException uh) {
      return autoReconnecting ? RETRY : errorHandling(SResources.getString("core.invalidAddressTitle"),
          SResources.getString("core.invalidAddressText"));
    } catch (IOException e) {
      return autoReconnecting ? RETRY : errorHandling(SResources.getString("core.notFoundTitle"), hostname
          + ":" + port + " " + SResources.getString("core.notFoundText"));
    }
    return OK;
  }

  public int interactiveConnect() {
    if (!checkIfInitialized())
      return CLOSE;

    if (PreferenceLoader.loadBoolean("hostManagerOnStart")) {
      if (!setupWizard())
        return CLOSE;
    }
    this.automated = false;
    this.wantToConnect = true;
    return successfulConnect();
  }

  public boolean isAutoReconnecting() {
    return autoReconnecting;
  }

  public boolean isConnected() {
    if (core != null)
      return core.isConnected();

    return false;
  }

  public void notifyObject(Object object) {
    this.setChanged();
    this.notifyObservers(object);
  }

  private int onConnectionDenied() {
    if (createResYesNoBox("core.connectionDeniedTitle", "core.connectionDeniedText")) {
      if (!setupWizard())
        return CLOSE;
      return RETRY;
    } else
      return CLOSE;
  }

  private int onInvalidPassword() {
    if (createResYesNoBox("core.invalidLoginTitle", "core.invalidLoginText")) {
      if (!setupWizard())
        return CLOSE;
      return RETRY;
    } else
      return CLOSE;
  }

  public void readPreferences(int i) {
    
    
    readPreferences(i, true);
  }

  public boolean checkIfInitialized() {
    if (!(PreferenceLoader.loadBoolean("initialized"))) {
      if (!setupWizard())
        return false;
      PreferenceLoader.getPreferenceStore().setValue("initialized", true);
      PreferenceLoader.saveStore();
    }
    return true;
  }

  public void readPreferences(int i, boolean overwrite) {
    if (overwrite && !Sancho.automated && PreferenceLoader.loadBoolean("useLastFile")) {
      SwissArmy.writeLastFile(i);
    }
    
    if (!PreferenceLoader.contains("hm_" + i + "_hostname"))
      i = 0;

    port = port == 0 || overwrite ? PreferenceLoader.loadInt("hm_" + i + "_port") : port;
    hostname = hostname == null || overwrite
        ? PreferenceLoader.loadString("hm_" + i + "_hostname")
        : hostname;
    username = username == null || overwrite
        ? PreferenceLoader.loadString("hm_" + i + "_username")
        : username;
    password = password == null || overwrite
        ? PreferenceLoader.loadString("hm_" + i + "_password")
        : password;
  }

  public synchronized void reconnect() {
    wantToConnect = true;
  }

  public void reconnect(int i) {
    readPreferences(i);
    reconnect();
  }

  public void reconnectO() {
    connect();
  }

  // corefactory thread
  public void run() {
    while (true) {

      if (core == null && wantToConnect) {
        connectRC = connect();

        if (connectRC == CLOSE)
          wantToConnect = false;

      }
      SwissArmy.threadSleep(1000);
    }
  }

  public synchronized void setAutomated(boolean b) {
    this.automated = b;

  }

  public synchronized void setAutoReconnect() {
    this.wantToConnect = true;
    this.autoReconnecting = true;
  }

  public void setAutoReconnecting(boolean b) {
    autoReconnecting = b;
  }

  // run in some core's thread
  public void setDisconnected() {
    synchronized (this) {
      this.core = null;
      this.wantToConnect = false;
    }
    this.setChanged();
    this.notifyObservers(Boolean.FALSE);
    this.setChanged();
    this.notifyObservers(SResources.S_ES);
  }

  public void setHostPort(String hostname, int port) {
    this.hostname = hostname;
    this.port = port;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  private boolean setupWizard() {
    brc = false;
    irc = 0;
    display.syncExec(new Runnable() {
      public void run() {
        Splash.setVisible(false);
        SetupWizardDialog dialog = new SetupWizardDialog(null, new SetupWizard());
        dialog.create();
        brc = WizardDialog.OK == dialog.open();
        irc = dialog.getNum();
        Splash.setVisible(true);
      }
    });

    readPreferences(irc);
    return brc;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public synchronized void setWantToConnect(boolean b) {
    this.wantToConnect = b;
  }

  public int startCore() {

    int rc = initializeSocket();

    if (rc == RETRY || rc == CLOSE)
      return rc;

    if (this.core != null) {
      this.core.deleteObservers();
    }

    this.core = Sancho.monitorMode
        ? new MLDonkeyCoreMonitor(socket, username, password, automated)
        : new MLDonkeyCore(socket, username, password, automated);

    this.core.addObserver(this);
    this.core.connect();

    Thread thread = new Thread(core);
    thread.setDaemon(true);
    thread.start();

    int incr = 20 * 4;
    while (incr-- > 0 && core != null && !core.semaphore()) {
      SwissArmy.threadSleep(250);
    }

    if (core == null || core.isConnectionDenied())
      return autoReconnecting ? RETRY : onConnectionDenied();
    else if (core.isInvalidPassword())
      return autoReconnecting ? RETRY : onInvalidPassword();

    connectedAt = System.currentTimeMillis();
    autoReconnecting = false;
    hasConnected = true;
    this.setChanged();
    this.notifyObservers(SResources.getString("e.state.connected") + getConnectedString() + getCoreVersion());
    this.setChanged();
    this.notifyObservers(Boolean.TRUE);
    return OK;
  }

  public String getCoreVersion() {
    if (core != null) {
      String result = core.getCoreVersion();
      if (result.equals(""))
        return result;
      return " | " + result;

    }
    return SResources.S_ES;
  }

  public String getConnectedString() {
    return SResources.S_ES;
  }

  // from main thread
  public int successfulConnect() {
    int rc;

    while (true) {
      rc = getConnectRC();

      if (!display.readAndDispatch()) {
        SwissArmy.threadSleep(100);

        if (rc == OK || rc == CLOSE)
          break;
      }
    }
    return rc;
  }

  public void update(Observable o, Object obj) {
    if (obj instanceof IOException && o == core) { // && core.initialized()) {
      setDisconnected();

      if (hasConnected && PreferenceLoader.loadBoolean("autoReconnect"))
        setAutoReconnect();
    }
  }

  public void updateSplash(final String text) {
    display.syncExec(new Runnable() {
      public void run() {
        Splash.updateText(text);
      }
    });
  }

  public void updateSplash(final String text, final String p, final int i) {
    display.syncExec(new Runnable() {
      public void run() {
        Splash.updateText(text, p, i);
      }
    });
  }

  public static boolean openQuestion(Shell parent, String title, String message) {
    MessageDialog dialog = new MessageDialog(parent, title, SResources.getImage("ProgramIcon"), message,
        MessageDialog.QUESTION, new String[]{IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL}, 0);
    return dialog.open() == 0;
  }

  public static void openInformation(Shell parent, String title, String message) {
    MessageDialog dialog = new MessageDialog(parent, title, SResources.getImage("ProgramIcon"), message,
        MessageDialog.INFORMATION, new String[]{IDialogConstants.OK_LABEL}, 0);
    dialog.open();
    return;
  }
}
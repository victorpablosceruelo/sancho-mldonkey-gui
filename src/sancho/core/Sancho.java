/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import sancho.utility.SwissArmy;
import sancho.utility.VersionInfo;
import sancho.view.MainWindow;
import sancho.view.console.ExecConsole;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.utility.SResources;
import sancho.view.utility.Splash;
import sancho.view.utility.dialogs.BugDialog;

public class Sancho {
  private static CoreFactory coreFactory;
  public static boolean debug;
  public static boolean sDebug;
  private static Display display;
  private static ExecConsole execConsole;
  private static List linkList;
  public static boolean noBrowser;
  public static boolean noCore;
  private static File ourLockFile;
  public static long startTime;
  public static boolean spawnAborted;
  private static StringBuffer stringBuffer = new StringBuffer();
  public static boolean monitorMode;
  public static boolean hasLoaded;
  public static boolean automated;

  public static boolean hasLoaded() {
    return hasLoaded;
  }

  public static void addLink(String link) {
    if (linkList == null)
      linkList = new ArrayList();

    linkList.add(link);
  }

  public static boolean argCheck(char c, String s) {
    return s.length() == 2 && s.indexOf(c) == 1 && (s.startsWith("-") || s.startsWith("/"));
  }

  private static void automatedLaunch() {
    coreFactory.setAutomated(true);
    coreFactory.setWantToConnect(true);
    if (PreferenceLoader.loadBoolean("useLastFile")) {
      coreFactory.readPreferences(SwissArmy.readLastFile());
    }
    if (coreFactory.successfulConnect() == CoreFactory.OK)
      sendDownloadLinks();
    display.dispose();
  }

  public static void exit(int errorCode) {

    if (stringBuffer.length() > 0) {
      if (VersionInfo.getOSPlatform().equals("Windows") && stringBuffer.length() > 0) {
        MessageBox messageBox = new MessageBox(new Shell(), SWT.OK | SWT.ICON_INFORMATION);
        messageBox.setMessage(stringBuffer.toString());
        messageBox.open();
      } else
        System.out.println(stringBuffer.toString());
    }

    if (display != null && !display.isDisposed())
      display.dispose();

    System.exit(errorCode);
  }

  public static ICore getCore() {
    return coreFactory.getCore();
  }

  public static ExecConsole getCoreConsole() {
    return execConsole;
  }

  public static CoreFactory getCoreFactory() {
    return coreFactory;
  }

  public static boolean hasCollectionFactory() {
    return getCore() != null && getCore().getCollectionFactory() != null;
  }

  public static String getUptime() {
    return SwissArmy.calcUptime(Sancho.startTime);
  }

  private static void initializeResources() {
    try {
      PreferenceLoader.initialize();
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(2);
    }
    SResources.initialize();
    PreferenceLoader.initialize2();
  }

  private static void interactiveLaunch() {
    startTime = System.currentTimeMillis();

    new Splash(display);

    if (!PreferenceLoader.loadString("coreExecutable").equals(""))
      spawnCore();

    if (noCore || coreFactory.interactiveConnect() == CoreFactory.OK) {
      new MainWindow(display);
      if (coreFactory.isConnected()) {
        coreFactory.disconnect();
      }
    }

    if (ourLockFile != null)
      ourLockFile.delete();

    Splash.dispose();
  }

  public static void main(String[] argv) {
    display = new Display();

    coreFactory = new SSHCoreFactory(display);
    parseArgs(argv);
    initializeResources();
    coreFactory.initialize();

    if (linkList != null) {
      automated = true;
      automatedLaunch();
      exit(0);
    }

    if (!debug) {
      if (!PreferenceLoader.loadBoolean("allowMultipleInstances")) {
        boolean running = false;

        ourLockFile = new File(VersionInfo.getHomeDirectory() + ".lock");
        running = ourLockFile.exists();

        if (!running) {
          FileOutputStream out;

          try {
            out = new FileOutputStream(ourLockFile);
            PrintStream p = new PrintStream(out);

            p.close();
            out.close();
            ourLockFile.deleteOnExit();

          } catch (FileNotFoundException fnf) {
          } catch (IOException io) {
          }
        }

        if (running) {
          MessageBox alreadyRunning = new MessageBox(new Shell(display), SWT.YES | SWT.NO | SWT.ICON_ERROR);
          alreadyRunning.setText(SResources.getString("core.multipleCoresTitle"));
          alreadyRunning.setMessage(SResources.getString("core.multipleCoresText"));

          if (alreadyRunning.open() == SWT.NO)
            exit(0);
        }
      }
    }
    interactiveLaunch();
    exit(0);
  }

  public static void parseArgs(String[] argv) {
    for (int i = 0; i < argv.length; i++) {
      if (parseSingle(argv[i])) {
      } else if (argv.length > (i + 1) && parseDouble(argv[i], argv[i + 1]))
        i++;
      else
        break;
    }
  }

  public static void pDebug(String string) {
    if (debug || sDebug)
      System.err.println("[" + System.currentTimeMillis() + "] " + string);
  }

  public static boolean parseDouble(String arg, String param) {
    if (argCheck('c', arg)) {
      PreferenceLoader.setPrefFile(param);
      return true;
    } else if (argCheck('j', arg)) {
      PreferenceLoader.setHomeDirectory(param);
      return true;
    } else if (argCheck('l', arg)) {
      addLink(param);
      return true;
    } else if (argCheck('f', arg)) {
      PreferenceLoader.setLocaleString(param);
      return true;
    } else if (argCheck('H', arg) || argCheck('h', arg)) {
      String[] tokens = SwissArmy.split(param, ':');
      if (tokens.length == 2) {
        int port;
        try {
          port = Integer.parseInt(tokens[1]);
          coreFactory.setHostPort(tokens[0], port);
        } catch (NumberFormatException n) {
        }
      }
      return true;
    } else if (argCheck('U', arg) || argCheck('u', arg)) {
      coreFactory.setUsername(param);
      return true;
    } else if (argCheck('P', arg) || argCheck('p', arg)) {
      coreFactory.setPassword(param);
      return true;
    }
    return false;
  }

  public static boolean parseSingle(String arg) {
    if (argCheck('?', arg)) {
      printHelp();
      exit(1);
      return false;
    } else if (argCheck('v', arg) || argCheck('V', arg)) {
      printVersion();
      exit(1);
      return false;
    } else if (argCheck('d', arg)) {
      debug = true;
      return true;
    } else if (argCheck('b', arg)) {
      noBrowser = true;
      return true;
    } else if (argCheck('m', arg)) {
      monitorMode = true;
      return true;
    } else if (argCheck('n', arg)) {
      noCore = true;
      return true;
    }
    return false;
  }

  public static void printHelp() {
    initializeResources();
    printVersion();
    stringBuffer.append("Usage: " + VersionInfo.getName() + " [OPTION]...\n\n");
    printOption('c', "<filename>", "cmdline.c");
    printOption('j', "<directory>", "cmdline.j");
    printOption('l', "<linkname>", "cmdline.l");
    printOption('h', "<host:port>", "cmdline.h");
    printOption('u', "<username>", "cmdline.u");
    printOption('p', "<password>", "cmdline.p");
    printOption('f', "<xx_XX>", "cmdline.f");
    stringBuffer.append("\n");
    printOption('b', "", "cmdline.b");
    printOption('n', "", "cmdline.n");
    printOption('d', "", "cmdline.d");
    printOption('m', "", "cmdline.m");
  }

  public static void printOption(char c, String param, String help) {
    stringBuffer.append("  -" + c + " " + param + " \t" + SResources.getString(help) + "\n");
  }

  public static void printVersion() {
    stringBuffer.append(VersionInfo.getName() + " " + VersionInfo.getVersion() + "\n");
  }

  private static void sendDownloadLinks() {
    if (Sancho.getCore() == null)
      return;

    for (int i = 0; i < linkList.size(); i++)
      SwissArmy.sendLink(Sancho.getCore(), (String) linkList.get(i));
  }

  private static void spawnCore() {
    Splash.updateText("splash.spawningCore");
    File coreEXE = new File(PreferenceLoader.loadString("coreExecutable"));

    int port = PreferenceLoader.loadInt("hm_0_port");

    spawnAborted = SwissArmy.portInUse(port);

    if (spawnAborted) {
      Splash.updateText("splash.spawningAborted");
      return;
    }

    if (execConsole == null && coreEXE.exists() && coreEXE.isFile()) {
      execConsole = new ExecConsole();
      int i = 0;
      while (i++ < 235 && !execConsole.coreStarted())
        SwissArmy.threadSleep(250);
    }
  }

  public static void send(short opCode, Object[] oArray) {
    if (getCore() == null)
      return;

    getCore().send(opCode, oArray);
  }

  public static void send(short opCode, Object object) {
    send(opCode, new Object[]{object});
  }

  public static void send(short opCode) {
    send(opCode, null);
  }

  public static void threadException(final String string, final Exception e) {

    if (Sancho.debug)
      e.printStackTrace();
    else {
      if (display != null && !display.isDisposed()) {
        display.asyncExec(new Runnable() {
          public void run() {
            StringWriter sw = new StringWriter();
            sw.write("From Thread: " + string + "\n\n");
            e.printStackTrace(new PrintWriter(sw, true));
            new BugDialog(new Shell(display), sw.toString()).open();
          }
        });
      }
    }
  }
}
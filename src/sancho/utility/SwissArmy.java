/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.utility;

import gnu.regexp.RE;
import gnu.regexp.REException;
import gnu.regexp.REMatch;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;

import sancho.core.ICore;
import sancho.core.Sancho;
import sancho.model.mldonkey.Network;
import sancho.model.mldonkey.enums.EnumNetwork;
import sancho.model.mldonkey.utility.OpCodes;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.utility.SResources;

public class SwissArmy {
  private static final float k = 1024f;
  private static final float m = k * k;
  private static final float g = m * k;
  private static final float t = g * k;

  private static String S_D = "d";
  private static String S_H = "h";
  private static String S_M = "m";
  private static String S_S = "s";
  private static String S_0M = "0m";

  private static String S_TB = "tb";
  private static String S_GB = "gb";
  private static String S_MB = "mb";
  private static String S_KB = "kb";

  private static String S_STB = " TB";
  private static String S_SGB = " GB";
  private static String S_SMB = " MB";
  private static String S_SKB = " KB";

  public static DecimalFormat df00 = new DecimalFormat("0.0");
  public static DecimalFormat df000 = new DecimalFormat("0.00");

  public static String[] split(String string, char delimiter) {
    RE regex = null;
    String expression = "([^" + delimiter + "])*";

    try {
      regex = new RE(expression);
    } catch (REException e) {
      e.printStackTrace();
    }

    List stringList = new ArrayList();
    REMatch[] matches = regex.getAllMatches(string);

    for (int i = 0; i < matches.length; i++) {
      String match = matches[i].toString();

      if (!match.equals(SResources.S_ES))
        stringList.add(match);
    }

    String[] resultArray = new String[stringList.size()];
    stringList.toArray(resultArray);

    return resultArray;
  }

  public static String replaceAll(String input, String toBeReplaced, String replaceWith) {
    RE regex = null;

    try {
      regex = new RE(toBeReplaced);
    } catch (REException e) {
      e.printStackTrace();
    }

    return regex.substituteAll(input, replaceWith);
  }

  public static StringBuffer stringBuffer2 = new StringBuffer(10);

  public static FieldPosition FP = new FieldPosition(1);

  public static String calcStringSize(long size) {
    if (size == 0)
      return SResources.S_0;

    double fsize = size;
    stringBuffer2.setLength(0);

    if (fsize >= t) {
      df000.format(fsize / t, stringBuffer2, FP);
      return stringBuffer2.append(S_STB).toString().intern();
    } else if (fsize >= g) {
      df000.format(fsize / g, stringBuffer2, FP);
      return stringBuffer2.append(S_SGB).toString().intern();
    } else if (fsize >= m) {
      df00.format(fsize / m, stringBuffer2, FP);
      return stringBuffer2.append(S_SMB).toString().intern();
    } else if (fsize >= k) {
      df00.format(fsize / k, stringBuffer2, FP);
      return stringBuffer2.append(S_SKB).toString().intern();
    } else
      return String.valueOf(size).intern();
  }

  private static long secondsInDay = 60 * 60 * 24;
  private static long secondsInHour = 60 * 60;

  public static StringBuffer stringBuffer4 = new StringBuffer(10);

  public static String calcStringOfSeconds(long inSeconds) {
    if (inSeconds < 60)
      return S_0M;

    long days = inSeconds / secondsInDay;

    if (days > 9999)
      return SResources.S_ES;

    long rest = inSeconds - (days * secondsInDay);
    long hours = rest / secondsInHour;
    rest = rest - (hours * secondsInHour);

    stringBuffer4.setLength(0);

    long minutes = rest / 60;

    if (days > 0)
      return stringBuffer4.append(days).append(S_D).toString().intern();
    else if (hours > 0) {
      stringBuffer4.append(hours).append(S_H);
      if (minutes > 0)
        stringBuffer4.append(SResources.S_SPACE).append(minutes).append(S_M);
      return stringBuffer4.toString().intern();
    }

    return stringBuffer4.append(minutes).append(S_M).toString().intern();
  }

  public static String calcUptime(long l) {
    long seconds = 0;
    long current = System.currentTimeMillis();
    if (current > l)
      seconds = (current - l) / 1000;
    return calcStringOfSeconds(seconds);
  }

  public static StringBuffer stringBuffer3 = new StringBuffer();

  public static String calcTimeOfSeconds(long inSeconds) {
    stringBuffer3.setLength(0);
    long hours = inSeconds / 60 / 60;
    long rest = inSeconds - (hours * 60 * 60);
    long min = rest / 60;
    rest = rest - (min * 60);
    if (hours > 0) {
      stringBuffer3.append(hours);
      stringBuffer3.append(S_H);
    }
    if (min > 0) {
      stringBuffer3.append(min);
      stringBuffer3.append(S_M);
    }
    if (rest > 0) {
      stringBuffer3.append(rest);
      stringBuffer3.append(S_S);
    }

    return stringBuffer3.toString();
  }

  public static long stringSizeToLong(String value, String unit) {
    int factor = 1;

    if (unit.equalsIgnoreCase(S_KB))
      factor = 1024;
    else if (unit.equalsIgnoreCase(S_MB))
      factor = 1024 * 1024;
    else if (unit.equalsIgnoreCase(S_GB))
      factor = 1024 * 1024 * 1024;
    else if (unit.equalsIgnoreCase(S_TB))
      factor = 1024 * 1024 * 1024 * 1024;

    float size;
    try {
      size = Float.parseFloat(value);
    } catch (NumberFormatException e) {
      size = 1;
    }
    return (long) (size * factor);
  }

  public static int log2(int value) {
    int result = -1;
    for (; value > 0; result++)
      value >>= 1;
    return result;
  }

  /**
   * Counts number of 1 bits in a 32 bit unsigned number.
   * 
   * @param x
   *          unsigned 32 bit number whose bits you wish to count.
   * 
   * @return number of 1 bits in x.
   * @author Roedy Green
   */
  public static int countBits(int x) {
    x = (x >>> 1 & 0x55555555) + (x & 0x55555555);
    x = (x >>> 2 & 0x33333333) + (x & 0x33333333);
    x = (x >>> 4 & 0x0f0f0f0f) + (x & 0x0f0f0f0f);
    x = (x >>> 8 & 0x00ff00ff) + (x & 0x00ff00ff);
    return (x >>> 16) + (x & 0x0000ffff);
  }

  public static void threadSleep(int sleepTime) {
    try {
      Thread.sleep(sleepTime);
    } catch (InterruptedException e) {
    }
  }

  static RE linksRE1;
  static RE linksRE2;
  static RE hrefRE;
  static RE hrefRE1;
  static {
    String str1 = "(ed2k://\\|file\\|[^\\|]+\\|(\\d+)\\|([\\dabcdef]+)\\|)"
        + "|(sig2dat:///?\\|File:[^\\|]+\\|Length:.+?\\|UUHash:.+?\\=.+?\\=)" + "|(magnet:\\?xt=.+)"
        + "|(\"http://.+\\.torrent\\?[^>]+\")" + "|(http://.+\\.torrent)";

    String str2 = str1 + "|(http://.+)" + "|(ftp://.+)";

    String href = "href.*?=.+?>";

    try {
      linksRE1 = new RE(str1, RE.REG_ICASE);
      linksRE2 = new RE(str1 + str2, RE.REG_ICASE);
      hrefRE = new RE(href, RE.REG_ICASE);
    } catch (REException e) {
    }
  }

  public static String[] parseLinks(String[] stringArray) {
    RE re = PreferenceLoader.loadBoolean("linkRipperShowAll") ? linksRE2 : linksRE1;

    List stringList = new ArrayList();
    String string = null;
    REMatch[] reMatch;
    for (int i = 0; i < stringArray.length; i++) {
      reMatch = re.getAllMatches(stringArray[i]);
      if (reMatch.length > 0) {
        string = reMatch[0].toString();
        if (!stringList.contains(string))
          stringList.add(string);
      }
    }
    String[] resultArray = new String[stringList.size()];
    stringList.toArray(resultArray);
    Arrays.sort(resultArray, String.CASE_INSENSITIVE_ORDER);

    return resultArray;
  }

  public static StringBuffer stringBuffer = new StringBuffer();

  public static String[] parseLinks(String string) {
    REMatch[] matches = hrefRE.getAllMatches(string);

    int ind;
    String[] stringArray = new String[matches.length];
    for (int i = 0; i < matches.length; i++) {
      stringBuffer.setLength(0);
      stringBuffer.append(matches[i].toString());
      ind = stringBuffer.indexOf("=");
      if (ind != -1 && stringBuffer.length() > ind + 1)
        stringBuffer.delete(0, ind + 1);

      stringBuffer.deleteCharAt(stringBuffer.length() - 1);
      lrTrim(stringBuffer);
      stringArray[i] = replaceAll(stringBuffer.toString(), "\"", SResources.S_ES);
    }
    return parseLinks(stringArray);
  }

  public static void lrTrim(StringBuffer stringBuffer) {
    while (stringBuffer.length() > 0 && (stringBuffer.charAt(0) == '"' || stringBuffer.charAt(0) == ' '))
      stringBuffer.deleteCharAt(0);

    int len = 0;
    while ((len = stringBuffer.length() - 1) > 0) {
      if (stringBuffer.charAt(len) != '"' && stringBuffer.charAt(len) != ' ')
        break;
      stringBuffer.deleteCharAt(len);
    }
  }

  static RE ts = null;
  static RE ats = null;

  static {
    try {
      ts = new RE("torrent_servers\\[(.+?)\\](.+?)=(.+?)'(.+?)'");
      ats = new RE("at2\\(.+?,.+?,(.+?),(?:.+?,){4}'(.+?)',.+?\\);");
    } catch (Exception e) {

    }
  }

  public static String getRandomString(int length) {
    Random rnd = new Random();
    rnd.setSeed(System.currentTimeMillis());

    StringBuffer buf = new StringBuffer(length);
    int nextChar;
    int range = 'z' - 'a' + 1;
    for (int i = 0; i < length; i++) {
      nextChar = 'a' + rnd.nextInt(range);
      buf.append((char) nextChar);
    }
    return buf.toString();
  }

  public static Object[] toArray(Set set) {
    Collection c = new ArrayList(set.size());
    try {
      for (Iterator i = set.iterator(); i.hasNext();)
        c.add(i.next());
    } catch (NoSuchElementException e) {
    }
    return c.toArray();
  }

  public static void clear(Map map) {
    Iterator itr = map.entrySet().iterator();
    int pos = map.size();
    try {
      for (Iterator i = map.entrySet().iterator(); --pos >= 0;) {
        try {
          itr.next();
          itr.remove();
        } catch (NoSuchElementException e) {
        }
      }
    } catch (NoSuchElementException e) {
    }
  }

  public static String[] getPreviewApps(String filename) {

    List stringList = new ArrayList();

    String previewExtensions = PreferenceLoader.loadString("previewExtensions");
    if (!previewExtensions.equals(SResources.S_ES)) {
      StringTokenizer st = new StringTokenizer(previewExtensions, ";");
      int ct = st.countTokens();
      String rExt = "";
      String prog = "";
      while (st.hasMoreTokens()) {
        rExt = st.nextToken();
        if (st.hasMoreTokens()) {
          prog = st.nextToken();
          if (filename.toLowerCase().endsWith(rExt.toLowerCase())) {
            stringList.add(prog);
          }
        }
      }
    }

    if (!PreferenceLoader.loadString("previewExecutable").equals(""))
      stringList.add(PreferenceLoader.loadString("previewExecutable"));

    String[] resultArray = new String[stringList.size()];
    stringList.toArray(resultArray);
    return resultArray;
  }

  // http://gcc.gnu.org/bugzilla/show_bug.cgi?id=11801
  public static void execInThread(final String[] cmdArray, final String workingDir) {
    Thread t = new Thread() {
      public void run() {
        Runtime rt = Runtime.getRuntime();
        Process p;

        try {
          if (workingDir == null)
            p = rt.exec(cmdArray);
          else
            p = rt.exec(cmdArray, null, new File(workingDir));

          StreamMonitor errorMonitor = new StreamMonitor(p.getErrorStream());
          StreamMonitor outputMonitor = new StreamMonitor(p.getInputStream());
          errorMonitor.setDaemon(true);
          errorMonitor.start();
          outputMonitor.setDaemon(true);
          outputMonitor.start();

          p.waitFor();

        } catch (Exception e) {
          Sancho.pDebug("execInThread: " + e);
        }
      }
    };
    t.setDaemon(true);
    t.start();
  }

  public static byte[] fileToByteArray(String filename) {

    File file = new File(filename);
    if (file.exists()) {
      ByteArrayOutputStream bOS = new ByteArrayOutputStream();
      int bufSize = 8192;
      byte[] buffer = new byte[bufSize];
      BufferedInputStream is;
      try {
        is = new BufferedInputStream(new FileInputStream(file));
        int result;
        while ((result = is.read(buffer, 0, bufSize)) != -1)
          bOS.write(buffer, 0, result);

        is.close();
        bOS.close();

        return bOS.toByteArray();
      } catch (IOException e) {
        return null;
      }
    } else {
      return null;
    }
  }
 

  public static boolean isSupportedProtocol(String link) {
    String[] supported = {"http:", "ftp:", "ssh:", "ed2k:", "magnet:", "sig2dat:"};
    String linkL = link.toLowerCase();

    for (int i = 0; i < supported.length; i++)
      if (linkL.startsWith(supported[i]))
        return true;

    return false;
  }

  public static boolean portInUse(int port) {

    try {
      ServerSocket listenerSocket = new ServerSocket(port);
      listenerSocket.close();
    } catch (BindException b) {
      return true;
    } catch (IOException e) {
      return true;
    }

    return false;
  }

  public static void sendLink(ICore core, String link) {
    String linkL = link.toLowerCase();
    if (core == null || !Sancho.hasCollectionFactory())
      return;

    Network btNetwork = core.getNetworkCollection().getByEnum(EnumNetwork.BT);
      if (linkL.startsWith("ftp:") || linkL.startsWith("ssh:")
          || (linkL.startsWith("http:") && (!linkL.endsWith("torrent") && !linkL.endsWith("tor")))) {
        Sancho.send(OpCodes.S_CONSOLE_MESSAGE, "http " + link);
      } else {
        Sancho.send(OpCodes.S_DLLINK, link);
      }
  }

  public static int readLastFile() {
    File lastFile = new File(VersionInfo.getHomeDirectory() + ".last");
    if (!lastFile.exists())
      return 0;
    BufferedReader input = null;
    try {
      input = new BufferedReader(new FileReader(lastFile));
      String line = input.readLine();
      try {
        int result = Integer.parseInt(line);
        return result;
      } catch (NumberFormatException e) {
        return 0;
      }
    } catch (FileNotFoundException ex) {
      return 0;
    } catch (IOException ex) {
      return 0;
    } finally {
      try {
        if (input != null) {
          input.close();
        }
      } catch (IOException ex) {
        return 0;
      }
    }
  }

  public static void writeLastFile(int i) {
    File lastFile = new File(VersionInfo.getHomeDirectory() + ".last");
    FileOutputStream out;
    try {
      out = new FileOutputStream(lastFile);
      PrintStream p = new PrintStream(out);
      p.println(i);
      p.close();
      out.close();
    } catch (FileNotFoundException fnf) {
    } catch (IOException io) {
    }
  }

  static class StreamMonitor extends Thread {
    InputStream inputStream;

    StreamMonitor(InputStream is) {
      this.inputStream = is;
    }

    public void run() {
      try {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = br.readLine()) != null) {
        }
        br.close();
      } catch (IOException ioe) {
        ioe.printStackTrace();
      }
    }
  }

}
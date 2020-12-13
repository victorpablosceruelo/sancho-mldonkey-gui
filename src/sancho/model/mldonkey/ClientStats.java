/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;

import java.text.DecimalFormat;
import java.text.FieldPosition;

import sancho.core.ICore;
import sancho.model.mldonkey.utility.MessageBuffer;

public class ClientStats extends AObjectO {

  private static final String S_TCP = "TCP: ";
  private static final String S_UDP = " | UDP: ";
  private static final String S_TOTAL = " | Total: ";
  private static final String S_KBS = " KB/s";
  private static final String S_00 = "00";

  protected Network[] connectedNetworks;
  protected long downloadCounter;
  protected int numDownloadedFiles;
  protected int numDownloadingFiles;
  protected int numSharedFiles;
  protected long sharedCounter;
  protected float tcpDownloadRate;
  protected float tcpUploadRate;
  protected float udpDownloadRate;
  protected float udpUploadRate;
  protected long uploadCounter;

  ClientStats(ICore core) {
    super(core);
  }

  public synchronized long getNumDownloadedFiles() {
    return numDownloadedFiles;
  }

  public synchronized long getSharedCounter() {
    return sharedCounter;
  }

  public synchronized int getNumDownloadingFiles() {
    return numDownloadingFiles;
  }

  public synchronized long getDownloadCounter() {
    return downloadCounter;
  }

  public synchronized int getNumSharedFiles() {
    return numSharedFiles;
  }

  public synchronized float getTcpDownloadRate() {
    return tcpDownloadRate;
  }

  public String getTcpDownRateString() {
    return toKBs(getTcpDownloadRate(), false);
  }

  public String getTcpDownRateStringS() {
    return toKBs(getTcpDownloadRate(), true);
  }

  public synchronized float getTcpUploadRate() {
    return tcpUploadRate;
  }

  public static StringBuffer stringBuffer = new StringBuffer();

  public String getDownloadToolTip() {
    stringBuffer.setLength(0);
    stringBuffer.append(S_TCP);
    stringBuffer.append(getTcpDownRateString());
    stringBuffer.append(S_UDP);
    stringBuffer.append(getUdpDownRateString());
    stringBuffer.append(S_TOTAL);
    stringBuffer.append(getTotalDownRateString());
    return stringBuffer.toString();
  }

  public String getUploadToolTip() {
    stringBuffer.setLength(0);
    stringBuffer.append(S_TCP);
    stringBuffer.append(getTcpUpRateString());
    stringBuffer.append(S_UDP);
    stringBuffer.append(getUdpUpRateString());
    stringBuffer.append(S_TOTAL);
    stringBuffer.append(getTotalUpRateString());
    return stringBuffer.toString();
  }

  public String getTcpUpRateString() {
    return toKBs(getTcpUploadRate(), false);
  }

  public String getTcpUpRateStringS() {
    return toKBs(getTcpUploadRate(), true);
  }

  public String getTotalDownRateString() {
    return toKBs(getTcpDownloadRate() + getUdpDownloadRate(), false);
  }

  public String getTotalUpRateString() {
    return toKBs(getTcpUploadRate() + getUdpUploadRate(), false);
  }

  public synchronized float getUdpDownloadRate() {
    return udpDownloadRate;
  }

  public String getUdpDownRateString() {
    return toKBs(getUdpDownloadRate(), false);
  }

  public synchronized float getUdpUploadRate() {
    return udpUploadRate;
  }

  public String getUdpUpRateString() {
    return toKBs(getUdpUploadRate(), false);
  }

  public synchronized long getUploadCounter() {
    return uploadCounter;
  }

  // guiDecoding#client_stats
  public synchronized void read(MessageBuffer messageBuffer) {
    this.uploadCounter = messageBuffer.getUInt64();
    this.downloadCounter = messageBuffer.getUInt64();
    this.sharedCounter = messageBuffer.getUInt64();
    this.numSharedFiles = messageBuffer.getInt32();
    this.tcpUploadRate = (float) messageBuffer.getInt32() / (float) 1024;
    this.tcpDownloadRate = (float) messageBuffer.getInt32() / (float) 1024;
    this.udpUploadRate = (float) messageBuffer.getInt32() / (float) 1024;
    this.udpDownloadRate = (float) messageBuffer.getInt32() / (float) 1024;
    this.numDownloadingFiles = messageBuffer.getInt32();
    this.numDownloadedFiles = messageBuffer.getInt32();

    readNetworks(messageBuffer);

    this.setChanged();
    this.notifyObservers();
  }

  public void readNetworks(MessageBuffer messageBuffer) {
    int len = messageBuffer.getUInt16();

    if (connectedNetworks == null || connectedNetworks.length != len)
      connectedNetworks = new Network[len];

    for (int i = 0; i < len; i++)
      connectedNetworks[i] = (Network) core.getNetworkCollection().get(messageBuffer.getInt32());
  }

  static StringBuffer stringBuffer2 = new StringBuffer(11);
  //static StringBuffer stringBuffer3 = new StringBuffer();

  public static DecimalFormat df000 = new DecimalFormat("0.00");
  public static FieldPosition FP = new FieldPosition(1);

  static String toKBs(float d, boolean s) {
    stringBuffer2.setLength(0);
    df000.format(d, stringBuffer2, FP);
    if (!s)
      stringBuffer2.append(S_KBS);

    return stringBuffer2.toString();

  }
  /*
   static String toKBs2(float d, boolean s) {
   stringBuffer2.setLength(0);
   stringBuffer3.setLength(0);
   stringBuffer2.append(Math.round(d * 100.0) / 100.0);
   stringBuffer2.append(S_00);
   stringBuffer3.append(stringBuffer2.substring(0, stringBuffer2.indexOf(SResources.S_DOT) + 3));
   if (!s)
   stringBuffer3.append(S_KBS);
   return stringBuffer3.toString();
   }
   */

}
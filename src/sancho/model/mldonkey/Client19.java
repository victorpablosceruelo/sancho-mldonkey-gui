/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;

import org.eclipse.swt.graphics.Image;

import sancho.core.ICore;
import sancho.model.mldonkey.enums.EnumHostState;
import sancho.model.mldonkey.utility.MessageBuffer;
import sancho.utility.SwissArmy;
import sancho.view.utility.SResources;

public class Client19 extends Client {

  protected static final String S_SZA = "sza";
  protected static final String S_EMU = "emu";
  protected static final String S_EDK = "edk";
  protected static final String S_OVR = "ovr";
  protected static final String S_CDK = "cdk";
  protected static final String S_LMU = "lmu";
  protected static final String S_XMU = "xmu";
  protected static final String S_AMU = "amu";
  protected static final String S_TML = "tml";
  protected static final String S_NML = "nml";
  protected static final String S_OML = "oml";

  protected static final String S_AZUREUS = "azureus";
  protected static final String S_ABC = "abc";
  protected static final String S_BITTORNADO = "bittornado";
  protected static final String S_MAINLINE = "mainline";
  protected static final String S_G3 = "g3";
  protected static final String S_TORRENTSTORM = "torrentstorm";
  protected static final String S_BITCOMET = "bitcomet";

  protected String clientSoftwareImageString;

  protected long downloaded;
  protected String downloadedString;
  protected boolean isUploader;
  protected String software;
  protected long uploaded;
  protected String uploadedString;
  protected String uploadFilename;

  public Client19(ICore core) {
    super(core);
  }

  protected String calcClientSoftwareImageString() {
    String lSoftware = software.toLowerCase();

    if (lSoftware.equalsIgnoreCase(S_EMU))
      return "epClientType0";
    else if (lSoftware.equalsIgnoreCase(S_SZA))
      return "epClientType6";
    else if (lSoftware.equalsIgnoreCase(S_EDK))
      return "epClientType3";
    else if (lSoftware.equalsIgnoreCase(S_OVR))
      return "epClientType2";
    else if (lSoftware.equalsIgnoreCase(S_CDK))
      return "epClientType1";
    else if (lSoftware.equalsIgnoreCase(S_LMU) || lSoftware.equalsIgnoreCase(S_XMU)
        || lSoftware.equalsIgnoreCase(S_AMU))
      return "epClientType7";
    else if (lSoftware.equalsIgnoreCase(S_TML) || lSoftware.equalsIgnoreCase(S_NML)
        || lSoftware.equalsIgnoreCase(S_OML))
      return "epClientType4";
    else if (lSoftware.startsWith(S_AZUREUS))
      return "azureus";
    else if (lSoftware.startsWith(S_ABC))
      return "abc";
    else if (lSoftware.startsWith(S_BITTORNADO))
      return "bittornado";
    else if (lSoftware.startsWith(S_MAINLINE))
      return "e.network.bittorrent.connected";
    else if (lSoftware.startsWith(S_G3))
      return "g3";
    else if (lSoftware.startsWith(S_TORRENTSTORM))
      return "torrentstorm";
    else if (lSoftware.startsWith(S_BITCOMET))
      return "bitcomet";
    else
      return "epClientType8";
  }

  public synchronized long getDownloaded() {
    return downloaded;
  }

  public synchronized String getDownloadedString() {
    return downloadedString != null ? downloadedString : SResources.S_ES;
  }

  public synchronized String getSoftware() {
    return software != null ? software : SResources.S_ES;
  }

  public synchronized Image getSoftwareImage() {
    return SResources.getImage(clientSoftwareImageString);
  }

  public synchronized long getUploaded() {
    return uploaded;
  }

  public synchronized String getUploadedString() {
    return uploadedString != null ? uploadedString : SResources.S_ES;
  }

  public String getUploadFilename() {
    // TODO: remove cheap hack when bittorrent supplies filenames
    if (!isUploader() && getStateEnum() == EnumHostState.CONNECTED_DOWNLOADING) {
      int fileNum = getStateFileNum();
      File file;
      if (fileNum != -1 && (file = core.getFileCollection().getFile(fileNum)) != null)
        return file.getName();
    }
    return getRealUploadFilename();
  }

  private synchronized String getRealUploadFilename() {
    return uploadFilename != null ? uploadFilename : SResources.S_ES;
  }

  public synchronized boolean isUploader() {
    return isUploader;
  }

  protected void readMore(MessageBuffer messageBuffer) {
    this.software = messageBuffer.getString();
    this.downloaded = messageBuffer.getUInt64();
    this.uploaded = messageBuffer.getUInt64();
    this.uploadFilename = messageBuffer.getString();

    this.uploadedString = SwissArmy.calcStringSize(uploaded);
    this.downloadedString = SwissArmy.calcStringSize(downloaded);
    this.clientSoftwareImageString = calcClientSoftwareImageString();
    this.isUploader = !uploadFilename.equals(SResources.S_ES);
  }

}
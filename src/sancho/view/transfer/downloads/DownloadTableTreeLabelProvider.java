/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.transfer.downloads;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import sancho.model.mldonkey.Client;
import sancho.model.mldonkey.File;
import sancho.model.mldonkey.enums.EnumFileState;
import sancho.model.mldonkey.enums.EnumHostState;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.transfer.FileClient;
import sancho.view.utility.SResources;
import sancho.view.viewer.GView;
import sancho.view.viewer.table.GTableLabelProvider;

public class DownloadTableTreeLabelProvider extends GTableLabelProvider
    implements
      IColorProvider,
      IFontProvider {
  private Color availableFileColor;

  private boolean displayColors = true;
  private Color downloadedFileColor;
  private Font downloadedFont;
  private Color pausedFileColor;

  private Font pausedFont;
  private Color queuedFileColor;
  private Font queuedFont;
  private Color rateAbove0Color;
  private Font rateAbove0Font;
  private Color rateAbove10Color;
  private Font rateAbove10Font;
  private Color rateAbove20Color;

  private Font rateAbove20Font;
  private Color unAvailableFileColor;

  public DownloadTableTreeLabelProvider(GView gViewer) {
    super(gViewer);
  }

  public Color getBackground(Object arg0) {
    return null;
  }

  public Image getColumnImage(Object arg0, int arg1) {
    if (arg0 instanceof File) {
      File file = (File) arg0;
      switch (cViewer.getColumnIDs()[arg1]) {
        case DownloadTableTreeView.NETWORK :
          return file.getEnumNetwork().getImage();
        case DownloadTableTreeView.NAME :
          return SResources.getImage(file.getProgramImageString());
        default :
          return null;
      }
    } else if (arg0 instanceof FileClient) {
      Client client = ((FileClient) arg0).getClient();

      switch (cViewer.getColumnIDs()[arg1]) {
        case DownloadTableTreeView.NETWORK :
          return client.getEnumNetwork().getImage();
        case DownloadTableTreeView.ID :
          return client.getEnumNetwork().getImage();
        case DownloadTableTreeView.NAME :
          return client.getStateEnum().getImage();
        case DownloadTableTreeView.PRIORITY :
          return client.getSoftwareImage();
        default :
          return null;
      }
    }
    return null;
  }

  public String getColumnText(Object arg0, int arg1) {

    if (arg0 instanceof File) {
      File file = (File) arg0;

      switch (cViewer.getColumnIDs()[arg1]) {
        case DownloadTableTreeView.ID :
          return String.valueOf(file.getId());
        case DownloadTableTreeView.NETWORK :
          return file.getEnumNetwork().getName();
        case DownloadTableTreeView.NAME :
          return file.getName();
        case DownloadTableTreeView.SIZE :
          return file.getSizeString();
        case DownloadTableTreeView.DOWNLOADED :
          return file.getDownloadedString();
        case DownloadTableTreeView.PERCENT :
          return file.getPercentString();
        case DownloadTableTreeView.SOURCES :
          return file.getSourcesString();
        case DownloadTableTreeView.AVAIL :
          return file.getRelativeAvailString();
        case DownloadTableTreeView.RATE :
          return file.getRateString();
        case DownloadTableTreeView.CHUNKS :
          return String.valueOf(file.getNumChunks());
        case DownloadTableTreeView.ETA :
          return file.getEtaString();
        case DownloadTableTreeView.PRIORITY :
          return file.getPriorityString();
        case DownloadTableTreeView.LAST :
          return file.getLastSeenString();
        case DownloadTableTreeView.AGE :
          return file.getAgeString();
        case DownloadTableTreeView.ETA2 :
          return file.getEta2String();
        case DownloadTableTreeView.NUMCLIENTS :
          return String.valueOf(file.getNumClients());
        case DownloadTableTreeView.NUMSOURCES :
          return String.valueOf(file.getNumSources());
          
        default :
          return SResources.S_ES;
      }
    } else if (arg0 instanceof FileClient) {
      Client client = ((FileClient) arg0).getClient();

      switch (cViewer.getColumnIDs()[arg1]) {
        case DownloadTableTreeView.ID :
          return String.valueOf(client.getId());
        case DownloadTableTreeView.NETWORK :
          return client.getEnumNetwork().getName();
        case DownloadTableTreeView.NAME :
          return client.getName();
        case DownloadTableTreeView.SIZE :
          return client.getModeString();
        case DownloadTableTreeView.SOURCES :
          return client.getAddr().toString();
        case DownloadTableTreeView.DOWNLOADED :
          return client.getDownloadedString();
        case DownloadTableTreeView.PERCENT :
          return String.valueOf(client.getPort());
        case DownloadTableTreeView.PRIORITY :
          return client.getSoftware();
        case DownloadTableTreeView.CHUNKS :
          return String.valueOf(client.getNumChunks(((FileClient) arg0).getFile().getId()));
        case DownloadTableTreeView.AGE :
          return client.getConnectedTimeString();
        default :
          return SResources.S_ES;
      }
    } else
      return SResources.S_ES;
  }

  public Font getFont(Object obj) {

    if (obj instanceof File) {
      File file = (File) obj;
      if (file.getFileStateEnum() == EnumFileState.QUEUED)
        return queuedFont;
      else if (file.getFileStateEnum() == EnumFileState.PAUSED)
        return pausedFont;
      else if (file.getFileStateEnum() == EnumFileState.DOWNLOADED)
        return downloadedFont;
      else if ((file.getRate() / 1000f) > 20f)
        return rateAbove20Font;
      else if ((file.getRate() / 1000f) > 10f)
        return rateAbove10Font;
      else if ((file.getRate() / 1000f) > 0f)
        return rateAbove0Font;
      else
        return null;
    } else
      return null;
  }

  public Color getForeground(Object obj) {
    if (!displayColors)
      return null;

    if (obj instanceof File) {
      File file = (File) obj;
      if (file.getFileStateEnum() == EnumFileState.QUEUED)
        return queuedFileColor;
      else if (file.getFileStateEnum() == EnumFileState.PAUSED)
        return pausedFileColor;
      else if (file.getFileStateEnum() == EnumFileState.DOWNLOADED)
        return downloadedFileColor;
      else if ((file.getRate() / 1000f) > 20f)
        return rateAbove20Color;
      else if ((file.getRate() / 1000f) > 10f)
        return rateAbove10Color;
      else if ((file.getRate() / 1000f) > 0f)
        return rateAbove0Color;
      else if (file.getRelativeAvail() == 0)
        return unAvailableFileColor;
      else
        return availableFileColor;
    } else if (obj instanceof FileClient) {
      Client client = ((FileClient) obj).getClient();
      if (client.getStateEnum() == EnumHostState.CONNECTED_DOWNLOADING)
        return rateAbove0Color;
      else
        return null;
    }
    return null;
  }

  public boolean isLabelProperty(Object obj, String arg1) {
    return true;
  }

  public void updateDisplay() {
    displayColors = PreferenceLoader.loadBoolean("displayTableColors");
    unAvailableFileColor = PreferenceLoader.loadColor("downloadsUnAvailableFileColor");
    downloadedFileColor = PreferenceLoader.loadColor("downloadsDownloadedFileColor");
    queuedFileColor = PreferenceLoader.loadColor("downloadsQueuedFileColor");
    pausedFileColor = PreferenceLoader.loadColor("downloadsPausedFileColor");
    availableFileColor = PreferenceLoader.loadColor("downloadsAvailableFileColor");
    rateAbove0Color = PreferenceLoader.loadColor("downloadsRateAbove0FileColor");
    rateAbove10Color = PreferenceLoader.loadColor("downloadsRateAbove10FileColor");
    rateAbove20Color = PreferenceLoader.loadColor("downloadsRateAbove20FileColor");

    rateAbove20Font = PreferenceLoader.loadFont("downloadsRateAbove20FontData");
    rateAbove10Font = PreferenceLoader.loadFont("downloadsRateAbove10FontData");
    rateAbove0Font = PreferenceLoader.loadFont("downloadsRateAbove0FontData");

    pausedFont = PreferenceLoader.loadFont("downloadsPausedFontData");
    queuedFont = PreferenceLoader.loadFont("downloadsQueuedFontData");
    downloadedFont = PreferenceLoader.loadFont("downloadsDownloadedFontData");
  }
}
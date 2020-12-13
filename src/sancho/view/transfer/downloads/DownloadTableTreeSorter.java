/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.transfer.downloads;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableTreeViewer;
import org.eclipse.jface.viewers.Viewer;

import sancho.model.mldonkey.Client;
import sancho.model.mldonkey.File;
import sancho.model.mldonkey.enums.EnumFileState;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.transfer.FileClient;
import sancho.view.utility.SResources;
import sancho.view.viewer.GSorter;
import sancho.view.viewer.GView;

public class DownloadTableTreeSorter extends GSorter {
  private ITableLabelProvider labelProvider;
  private boolean maintainSortOrder = false;

  public DownloadTableTreeSorter(GView gViewer) {
    super(gViewer);
  }

  public int category(Object element) {
    if (element instanceof File)
      return 1;
    else if (element instanceof FileClient)
      return 2;

    return 3;
  }

  public int compare(Viewer viewer, Object e1, Object e2) {
    int cat1 = category(e1);
    int cat2 = category(e2);

    if (cat1 != cat2)
      return cat1 - cat2;

    // fill in all columns
    if (e1 instanceof File) {
      File file1 = (File) e1;
      File file2 = (File) e2;

      switch (cViewer.getColumnIDs()[columnIndex]) {
        case DownloadTableTreeView.ID :
          return compareInts(file1.getId(), file2.getId());

        case DownloadTableTreeView.NETWORK :
          return compareStrings(file1.getEnumNetwork().getName(), file2.getEnumNetwork().getName());

        case DownloadTableTreeView.NAME :
          return compareStrings(file1.getName(), file2.getName());

        case DownloadTableTreeView.SIZE :
          return compareLongs(file1.getSize(), file2.getSize());

        case DownloadTableTreeView.DOWNLOADED :
          return compareLongs(file1.getDownloaded(), file2.getDownloaded());

        case DownloadTableTreeView.PERCENT :
          return compareInts(file1.getPercent(), file2.getPercent());

        case DownloadTableTreeView.SOURCES :
          return compareInts(file1.getSources(), file2.getSources());

        case DownloadTableTreeView.AVAIL :
          return compareInts(file1.getRelativeAvail(), file2.getRelativeAvail());

        case DownloadTableTreeView.RATE :

          if (file1.getFileStateEnum() == EnumFileState.DOWNLOADED)
            return -1;
          else if (file2.getFileStateEnum() == EnumFileState.DOWNLOADED)
            return 1;
          else if (file1.getFileStateEnum() == EnumFileState.QUEUED)
            return 2;
          else if (file2.getFileStateEnum() == EnumFileState.QUEUED)
            return -2;
          else if (file1.getFileStateEnum() == EnumFileState.PAUSED)
            return 3;
          else if (file2.getFileStateEnum() == EnumFileState.PAUSED)
            return -3;
          else
            return compareFloats(file1.getRate(), file2.getRate());

        case DownloadTableTreeView.CHUNKS :
          return compareInts(file1.getNumChunks(), file2.getNumChunks());

        case DownloadTableTreeView.ETA :
          labelProvider = (ITableLabelProvider) ((TableTreeViewer) viewer).getLabelProvider();

          if (labelProvider.getColumnText(e1, columnIndex).equals(SResources.S_ES))
            return 1;
          else if (labelProvider.getColumnText(e2, columnIndex).equals(SResources.S_ES))
            return -1;
          else

            return compareLongs(file1.getETA(), file2.getETA());

        case DownloadTableTreeView.PRIORITY :
          return compareInts(file1.getPriority(), file2.getPriority());

        case DownloadTableTreeView.LAST :
          return compareInts(file1.getLastSeen(), file2.getLastSeen());

        case DownloadTableTreeView.AGE :
          return compareLongs(file1.getAge(), file2.getAge());

        case DownloadTableTreeView.ETA2 :
          return compareLongs(file1.getETA2(), file2.getETA2());

        case DownloadTableTreeView.NUMCLIENTS :
          return compareInts(file1.getNumClients(), file2.getNumClients());

        case DownloadTableTreeView.NUMSOURCES :
          return compareInts(file1.getNumSources(), file2.getNumSources());

        default :
          return 0;
      }
    } else {

      Client client1 = ((FileClient) e1).getClient();
      Client client2 = ((FileClient) e2).getClient();

      switch (cViewer.getColumnIDs()[columnIndex]) {
        case DownloadTableTreeView.ID :
          return compareInts(client1.getId(), client2.getId());

        case DownloadTableTreeView.NETWORK :
          return compareStrings(client1.getEnumNetwork().getName(), client2.getEnumNetwork().getName());

        case DownloadTableTreeView.NAME :
          return compareStrings(client1.getName(), client2.getName());

        case DownloadTableTreeView.SIZE :
        case DownloadTableTreeView.PRIORITY :
          labelProvider = (ITableLabelProvider) ((TableTreeViewer) viewer).getLabelProvider();

          return compareStrings(labelProvider.getColumnText(e1, columnIndex), labelProvider.getColumnText(e2,
              columnIndex));

        case DownloadTableTreeView.DOWNLOADED :
          return compareLongs(client1.getDownloaded(), client2.getDownloaded());

        case DownloadTableTreeView.AGE :
          return compareInts(client1.getConnectedTime(), client2.getConnectedTime());

        case DownloadTableTreeView.SOURCES :
          return compareAddrs(client1.getAddr(), client2.getAddr());

        case DownloadTableTreeView.PERCENT :
          return compareInts(client1.getPort(), client2.getPort());

        case DownloadTableTreeView.CHUNKS :
          FileClient fileClient1 = (FileClient) e1;
          FileClient fileClient2 = (FileClient) e2;

          return compareInts(client1.getNumChunks(fileClient1.getFile().getId()), client2
              .getNumChunks(fileClient2.getFile().getId()));

        default :
          return 0;
      }
    }
  }

  public boolean isSorterProperty(Object element, String property) {

    if (maintainSortOrder && element instanceof File) {
      File file = (File) element;

      switch (cViewer.getColumnIDs()[columnIndex]) {
        case DownloadTableTreeView.DOWNLOADED :
          return file.hasChangedBit(File.CHANGED_DOWNLOADED);
        //return property.equals(File.CHANGED_DOWNLOADED);

        case DownloadTableTreeView.PERCENT :
          return file.hasChangedBit(File.CHANGED_PERCENT);
        //return property.equals(File.CHANGED_PERCENT);

        case DownloadTableTreeView.SOURCES :
          return file.hasChangedBit(File.CHANGED_SOURCES);
        //return property.equals(File.CHANGED_ACTIVE);

        case DownloadTableTreeView.AVAIL :
          return file.hasChangedBit(File.CHANGED_RAVAIL);
        // return property.equals(File.CHANGED_AVAIL);

        case DownloadTableTreeView.RATE :
          return file.hasChangedBit(File.CHANGED_RATE) || file.hasChangedBit(File.CHANGED_STATE);
        // return property.equals(File.CHANGED_RATE) || property.equals(File.CHANGED_STATE);

        case DownloadTableTreeView.ETA :
          return file.hasChangedBit(File.CHANGED_ETA);
        // return property.equals(File.CHANGED_ETA);

        case DownloadTableTreeView.LAST :
          return file.hasChangedBit(File.CHANGED_LAST);
        //return property.equals(File.CHANGED_LAST);

        default :
          return false;
      }
    }

    return false;
  }

  public boolean sortOrder(int columnIndex) {
    switch (cViewer.getColumnIDs()[columnIndex]) {
      case DownloadTableTreeView.ID :
      case DownloadTableTreeView.NAME :
      case DownloadTableTreeView.NETWORK :
      case DownloadTableTreeView.LAST :
      case DownloadTableTreeView.ETA :
        return true;

      default :
        return false;
    }
  }

  public void updateDisplay() {
    maintainSortOrder = PreferenceLoader.loadBoolean("maintainSortOrder");
  }
}
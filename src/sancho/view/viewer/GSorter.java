/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewer;

import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;

import sancho.model.mldonkey.Client;
import sancho.model.mldonkey.enums.EnumHostState;
import sancho.model.mldonkey.utility.Addr;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.utility.SResources;
import sancho.view.viewer.table.GTableLabelProvider;

public abstract class GSorter extends ViewerSorter implements DisposeListener {
  protected int columnIndex;
  protected ICustomViewer cViewer;
  protected GView gViewer;
  protected int lastColumnIndex;
  protected boolean lastSort;
  protected PreferenceStore preferenceStore = PreferenceLoader.getPreferenceStore();

  public GSorter(GView gViewer) {
    this.gViewer = gViewer;
  }

  public int compare(Viewer viewer, Object obj1, Object obj2) {
    return 0;
  }

  protected int compareAddrs(Addr addr1, Addr addr2) {
    return lastSort ? addr1.compareTo(addr2) : addr2.compareTo(addr1);
  }

  protected int compareBooleans(boolean b1, boolean b2) {
    return lastSort ? (b1 ? b2 ? 0 : 1 : b2 ? -1 : 0) : (b2 ? b1 ? 0 : 1 : b1 ? -1 : 0);
  }

  protected int compareClientStates(Client client1, Client client2) {
    if (client1.getStateEnum() == EnumHostState.CONNECTED_DOWNLOADING)
      return -1;
    else if (client2.getStateEnum() == EnumHostState.CONNECTED_DOWNLOADING)
      return 1;
    else {
      int rank1 = client1.getState().getRank();
      int rank2 = client2.getState().getRank();
      if (rank1 != 0 && rank2 != 0)
        return compareInts(rank1, rank2);
      else if (rank1 != 0)
        return -1;
      else if (rank2 != 0)
        return 1;
      else
        return 0;
    }
  }

  protected int compareDefault(TableViewer tableViewer, int columnIndex, Object object1, Object object2) {
    GTableLabelProvider gTLP = (GTableLabelProvider) tableViewer.getLabelProvider();
    String s1 = gTLP.getColumnText(object1, columnIndex);
    String s2 = gTLP.getColumnText(object2, columnIndex);
    return compareStrings(s1, s2);
  }

  protected int compareFloats(float float1, float float2) {
    if (float1 == float2)
      return 0;
    return lastSort ? (float1 - float2 > 0f ? 1 : -1) : (float2 - float1 > 0f ? 1 : -1);
  }

  protected int compareInts(int int1, int int2) {
    return lastSort ? (int1 - int2) : (int2 - int1);
  }

  protected int compareLongs(long long1, long long2) {
    if (long1 == long2)
      return 0;
    return lastSort ? (long1 - long2 > 0L ? 1 : -1) : (long2 - long1 > 0L ? 1 : -1);
  }

  protected int compareStrings(String aString1, String aString2) {
    if (aString1.equals(SResources.S_ES))
      return 1;

    if (aString2.equals(SResources.S_ES))
      return -1;

    return (lastSort ? aString1.compareToIgnoreCase(aString2) : aString2.compareToIgnoreCase(aString1));
  }

  protected int getLastColumnIndex() {
    return lastColumnIndex;
  }

  protected boolean getLastSort() {
    return lastSort;
  }

  public void initialize() {
    cViewer = (ICustomViewer) gViewer.getViewer();
    gViewer.getTable().addDisposeListener(this);

    String savedSort = PreferenceLoader.loadString(gViewer.getPreferenceString() + "LastSortColumn");
    if (!savedSort.equals(SResources.S_ES) && (gViewer.getColumnIDs().indexOf(savedSort) != -1)) {
      setColumnIndex(gViewer.getColumnIDs().indexOf(savedSort));
      setLastSort(PreferenceLoader.loadBoolean(gViewer.getPreferenceString() + "LastSortOrder"));
    }
  }

  public boolean isSorterProperty(Object element, String property) {
    return true;
  }

  public void setColumnIndex(int i) {
    columnIndex = i;
    lastSort = (columnIndex == lastColumnIndex) ? (!lastSort) : sortOrder(columnIndex);
    lastColumnIndex = columnIndex;
  }

  public void setLastColumnIndex(int i) {
    lastColumnIndex = i;
  }

  public void setLastSort(boolean b) {
    lastSort = b;
  }

  public boolean sortOrder(int columnIndex) {
    return true;
  }

  public void updateDisplay() {
  }

  public void widgetDisposed(DisposeEvent e) {
    preferenceStore.setValue(gViewer.getPreferenceString() + "LastSortColumn", String.valueOf(gViewer
        .getColumnIDs().charAt(columnIndex)));
    preferenceStore.setDefault(gViewer.getPreferenceString() + "LastSortOrder", true);
    preferenceStore.setValue(gViewer.getPreferenceString() + "LastSortOrder", lastSort);
  }
}

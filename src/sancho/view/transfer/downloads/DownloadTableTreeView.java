/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.transfer.downloads;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableTreeItem;
import org.eclipse.swt.widgets.Composite;

import sancho.core.Sancho;
import sancho.model.mldonkey.File;
import sancho.model.mldonkey.enums.AbstractEnum;
import sancho.model.mldonkey.enums.EnumExtension;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.transfer.ClientDetailDialog;
import sancho.view.transfer.FileClient;
import sancho.view.transfer.clients.ClientTableView;
import sancho.view.utility.WidgetFactory;
import sancho.view.viewFrame.ViewFrame;
import sancho.view.viewer.CustomTableTreeViewer;
import sancho.view.viewer.GView;
import sancho.view.viewer.tableTree.GTableTreeView;

public class DownloadTableTreeView extends GTableTreeView implements ICellModifier, IDoubleClickListener {
  public static final String BASIC_COLUMNS = "ABCDFIK";
  public static final String NAME_COLUMN = "C";
  public static final String RATE_COLUMN = "I";
  public static final String CHUNK_COLUMN = "J";
  public static final int ID = 0;
  public static final int NETWORK = 1;
  public static final int NAME = 2;
  public static final int SIZE = 3;
  public static final int DOWNLOADED = 4;
  public static final int PERCENT = 5;
  public static final int SOURCES = 6;
  public static final int AVAIL = 7;
  public static final int RATE = 8;
  public static final int CHUNKS = 9;
  public static final int ETA = 10;
  public static final int PRIORITY = 11;
  public static final int LAST = 12;
  public static final int AGE = 13;
  public static final int ETA2 = 14;
  public static final int NUMCLIENTS = 15;
  public static final int NUMSOURCES = 16;
  
  private CellEditor[] cellEditors = null;
  private CustomTableTreeViewer tableTreeViewer;
  private GView clientView;

  public DownloadTableTreeView(ViewFrame viewFrame) {
    super(viewFrame);

    preferenceString = "download";
    columnLabels = new String[]{"download.id", "download.network", "download.name", "download.size",
        "download.downloaded", "download.percent", "download.sources", "download.availability",
        "download.rate", "download.chunks", "download.eta", "download.priority", "download.last",
        "download.age", "download.eta2", "download.numClients", "download.numSources"};

    columnAlignment = new int[]{SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.RIGHT, SWT.RIGHT, SWT.RIGHT, SWT.RIGHT,
        SWT.RIGHT, SWT.RIGHT, SWT.LEFT, SWT.RIGHT, SWT.LEFT, SWT.RIGHT, SWT.RIGHT, SWT.RIGHT, SWT.RIGHT, SWT.RIGHT};

    columnDefaultWidths = new int[]{50, 50, 250, 75, 75, 50, 50, 50, 50, 75, 75, 50, 75, 75, 75, 75, 75};

    validExtensions = new AbstractEnum[]{EnumExtension.AUDIO, EnumExtension.VIDEO, EnumExtension.ARCHIVE,
        EnumExtension.CDIMAGE, EnumExtension.PICTURE, EnumExtension.DOCUMENT};

    gSorter = new DownloadTableTreeSorter(this);
    tableTreeContentProvider = new DownloadTableTreeContentProvider(this);
    tableLabelProvider = new DownloadTableTreeLabelProvider(this);
    tableTreeMenuListener = new DownloadTableTreeMenuListener(this);

    saveExclusionStateFilters = true;

    //  ETA2 off by default unless explicit
    StringBuffer sb = new StringBuffer();
    StringBuffer sb2 = new StringBuffer();
    sb.append(PreferenceLoader.loadString(preferenceString + "TableColumns"));
    int ind;
    if ((ind = sb.indexOf("O")) == -1) {
      sb2.append(PreferenceLoader.loadString(preferenceString + "TableColumnsOff"));
      if (sb2.indexOf("O") == -1)
        sb2.append("O");
      PreferenceLoader.getPreferenceStore().setValue(preferenceString + "TableColumnsOff", sb2.toString());
    }
    // end

    createContents(viewFrame.getChildComposite());
  }

  public boolean canModify(Object element, String property) {
    return element instanceof File;
  }

  public boolean clientsDisplayed() {
    if (clientView != null)
      return ((DownloadViewFrame) viewFrame).getParentSashForm(false).getMaximizedControl() == null;
    else

      return false;
  }

  public void createColumns() {
    super.createColumns();
    tableTreeViewer = getTableTreeViewer();
    tableTreeViewer.setColumnProperties(columnLabels);

    if (cellEditors != null) {
      for (int i = 0; i < cellEditors.length; i++) {
        if (cellEditors[i] != null)
          cellEditors[i].dispose();
      }

      cellEditors = null;
    }

    if (columnIDs.indexOf(NAME_COLUMN) > 0) {
      cellEditors = new CellEditor[columnIDs.length()];
      cellEditors[columnIDs.indexOf(NAME_COLUMN)] = new TextCellEditor(getTable());
    }
  }

  public void createContents(Composite parent) {
    super.createContents(parent);
    addMenuListener();
    tableTreeViewer.addDoubleClickListener(this);
    tableTreeViewer.addSelectionChangedListener((DownloadTableTreeMenuListener) tableTreeMenuListener);
  }

  public void doubleClick(DoubleClickEvent e) {
    IStructuredSelection sSel = (IStructuredSelection) e.getSelection();
    Object o = sSel.getFirstElement();

    if (o instanceof File) {
      File file = (File) o;

      if (tableTreeViewer.getExpandedState(file))
        tableTreeViewer.collapseToLevel(file, AbstractTreeViewer.ALL_LEVELS);
      else
        tableTreeViewer.expandToLevel(file, AbstractTreeViewer.ALL_LEVELS);
    } else if (o instanceof FileClient) {
      FileClient fileClient = (FileClient) o;
      ClientDetailDialog c = new ClientDetailDialog(tableTreeViewer.getTableTree().getShell(), fileClient
          .getFile(), fileClient.getClient(), getCore());
      c.open();
    }
  }

  public Object getValue(Object element, String property) {
    File file = (File) element;
    return file.getName();
  }

  public ViewFrame getViewFrame() {
    return viewFrame;
  }

  public void modify(Object element, String property, Object value) {
    TableTreeItem item = (TableTreeItem) element;
    File file = (File) item.getData();
    String newName = ((String) value).trim();

    if (newName.length() > 0)
      file.rename(newName);
  }

  public void setClientTableView(ClientTableView clientTableView) {
    clientView = clientTableView;
    ((DownloadTableTreeMenuListener) tableTreeMenuListener).setClientView(clientTableView);
  }

  public void setInput() {
    if (Sancho.hasCollectionFactory())
      sViewer.setInput(getCore().getFileCollection());
  }
 
  public void setPreferences() {
  //  Table table = getTable();
   // table.setFont(PreferenceLoader.loadFont("downloadsFontData"));

    if (PreferenceLoader.loadBoolean("tableCellEditors")) {
      tableTreeViewer.setCellEditors(cellEditors);
      tableTreeViewer.setCellModifier(this);
    } else {
      tableTreeViewer.setCellEditors(null);
      tableTreeViewer.setCellModifier(null);
    }

    boolean b = PreferenceLoader.loadBoolean("displayChunkGraphs");

    tableTreeViewer.closeAllTTE();
    tableTreeViewer.setChunksColumn(columnIDs.indexOf(CHUNK_COLUMN));
    tableTreeViewer.setEditors(b);

    if (b)
      tableTreeViewer.openAllTTE();
  }

  public void toggleClientsTable() {
    if (clientView == null)
      return;

    DownloadViewFrame downloadViewFrame = (DownloadViewFrame) viewFrame;
    boolean isMax = WidgetFactory.setMaximizedSashFormControl(downloadViewFrame.getParentSashForm(false),
        downloadViewFrame.getViewForm());

    updateClientsTable(!isMax);

  }

  public void unsetInput() {
    super.unsetInput();
    tableTreeViewer.closeAllTTE();
  }

  public void updateClientsTable(boolean b) {
    ((DownloadTableTreeMenuListener) tableTreeMenuListener).updateClientsTable(b);
  }

  public void updateDisplay() {
    super.updateDisplay();
    setPreferences();
    tableLabelProvider.updateDisplay();
    tableTreeContentProvider.updateDisplay();
    gSorter.updateDisplay();
    tableTreeViewer.refresh();
  }
}
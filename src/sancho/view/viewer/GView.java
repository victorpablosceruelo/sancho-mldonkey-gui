/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewer;

import java.util.StringTokenizer;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import sancho.core.ICore;
import sancho.core.Sancho;
import sancho.model.mldonkey.enums.AbstractEnum;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.utility.IDSelector;
import sancho.view.utility.SResources;
import sancho.view.viewFrame.ViewFrame;
import sancho.view.viewer.filters.AbstractViewerFilter;
import sancho.view.viewer.filters.ExclusionStateViewerFilter;
import sancho.view.viewer.filters.FileExtensionViewerFilter;
import sancho.view.viewer.filters.NetworkViewerFilter;
import sancho.view.viewer.filters.RefineFilter;
import sancho.view.viewer.filters.StateViewerFilter;
import sancho.view.viewer.table.GTableContentProvider;
import sancho.view.viewer.table.GTableLabelProvider;
import sancho.view.viewer.table.GTableMenuListener;

public abstract class GView implements DisposeListener {

  public static final String S_GVIEW = "gView";

  public static final int STATE_FILTER = 1;
  public static final int EXCLUSION_STATE_FILTER = 2;
  public static final int NETWORK_FILTER = 3;
  public static final int FILE_EXTENSION_FILTER = 4;
  public static final int REFINE_FILTER = 5;

  protected boolean active;
  protected String allColumns;
  protected int[] columnAlignment;
  protected int[] columnDefaultWidths;
  protected String columnIDs;
  protected String[] columnLabels;
  protected ControlAdapter controlAdapter;
  protected String dynamicColumn = SResources.S_ES;
  protected boolean forceRedraw = SWT.getPlatform().equals("win32") || SWT.getPlatform().equals("gtk");
  protected GSorter gSorter;
  protected int minDynamicColumnWidth = 100;
  protected boolean oldTableScrollBar;
  protected int oldTableWidth;
  protected String preferenceString;
  protected RefineFilter refineFilter;
  protected String refineString = SResources.S_ES;
  protected boolean saveExclusionStateFilters;
  protected boolean saveNetworkFilters;
  protected boolean saveStateFilters;
  protected StructuredViewer sViewer;
  protected GTableLabelProvider tableLabelProvider;
  protected AbstractEnum[] validExtensions;
  protected AbstractEnum[] validStates;
  protected ViewFrame viewFrame;
  protected boolean visible;

  public void addFilter(ViewerFilter viewerFilter) {
    redrawTable(false);
    sViewer.addFilter(viewerFilter);
    redrawTable(true);
  }

  protected void addMenuListener() {
    Menu menu = getTable().getMenu();
    menu.addMenuListener(new MenuAdapter() {
      public void menuShown(MenuEvent e) {
        Menu aMenu = getTable().getMenu();
        if (!((StructuredViewer) getViewer()).getSelection().isEmpty())
          if (aMenu.getItemCount() > 0)
            aMenu.setDefaultItem(aMenu.getItem(0));
      }
    });
  }

  protected void createColumns() {
    columnIDs = IDSelector.loadIDs(preferenceString + "TableColumns", allColumns);
    ((ICustomViewer) getViewer()).setColumnIDs(columnIDs);
    final PreferenceStore p = PreferenceLoader.getPreferenceStore();
    Table table = getTable();
    table.setHeaderVisible(true);
    TableColumn[] tableColumns = table.getColumns();
    for (int i = tableColumns.length - 1; i > -1; i--)
      tableColumns[i].dispose();

    for (int i = 0; i < columnIDs.length(); i++) {
      final int columnIndex = i;
      final int arrayItem = columnIDs.charAt(i) - IDSelector.MAGIC_NUMBER;
      TableColumn tableColumn = new TableColumn(table, columnAlignment[arrayItem]);
      p.setDefault(columnLabels[arrayItem], columnDefaultWidths[arrayItem]);
      tableColumn.setText(SResources.getString(columnLabels[arrayItem]));
      int oldWidth = p.getInt(columnLabels[arrayItem]);
      tableColumn.setWidth((oldWidth > 0) ? oldWidth : columnDefaultWidths[arrayItem]);

      tableColumn.addDisposeListener(new DisposeListener() {
        public synchronized void widgetDisposed(DisposeEvent e) {
          TableColumn thisColumn = (TableColumn) e.widget;
          if (thisColumn.getWidth() > 0)
            p.setValue(columnLabels[arrayItem], thisColumn.getWidth());
        }
      });

      if (preferenceString.equals("result"))
        tableColumn.addControlListener(new ControlAdapter() {
          public void controlResized(ControlEvent e) {
            TableColumn tableColumn = (TableColumn) e.widget;
            if (tableColumn.getWidth() > 0)
              p.setValue(columnLabels[arrayItem], tableColumn.getWidth());
          }
        });

      tableColumn.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event e) {
          sortByColumn(columnIndex);
        }
      });
    }
  }

  protected void createContents() {
    sViewer.setUseHashlookup(true);
    allColumns = IDSelector.createIDString(columnLabels);
    Table table = getTable();
    table.setRedraw(false);
    table.setLayoutData(new GridData(GridData.FILL_BOTH));
    createColumns();
    getTableContentProvider().initialize();
    getTableLabelProvider().initialize();
    gSorter.initialize();
    getTableMenuListener().initialize();

    sViewer.setContentProvider(getTableContentProvider());
    sViewer.setLabelProvider(getTableLabelProvider());
    updateDisplay();
    MenuManager popupMenu = new MenuManager(SResources.S_ES);
    popupMenu.setRemoveAllWhenShown(true);
    popupMenu.addMenuListener(getTableMenuListener());
    table.setMenu(popupMenu.createContextMenu(getTable()));
    sViewer.setSorter(gSorter);
    loadDynamicColumn();
    getTable().addDisposeListener(this);
    loadFilters();
    setInput();
    table.setRedraw(true);

  }

  public String getAllColumnIDs() {
    return allColumns;
  }

  public String getColumnIDs() {
    return columnIDs;
  }

  public String[] getColumnLabels() {
    return columnLabels;
  }

  public ICore getCore() {
    return Sancho.getCoreFactory().getCore();
  }

  public int getDynamicColumn() {
    if (dynamicColumn.equals(SResources.S_ES))
      return -1;
    else
      return columnIDs.indexOf(dynamicColumn); // dynamicCOlumn.charAt(0) - IDSelector.MAGIC_NUMBER;
  }

  public AbstractViewerFilter getFilter(Class aClass) {

    for (int i = 0; i < getFilters().length; i++) {
      if (aClass.isInstance(getFilters()[i]))
        return (AbstractViewerFilter) getFilters()[i];
    }
    return null;
  }

  public ViewerFilter[] getFilters() {
    return sViewer.getFilters();
  }

  public int getMinDynamicColumnWidth() {
    return minDynamicColumnWidth;
  }

  public String getPreferenceString() {
    return preferenceString;
  }

  public String getRefineString() {
    return refineString;
  }

  public Shell getShell() {
    return getTable().getShell();
  }

  public int getSortColumn() {
    return gSorter.getLastColumnIndex();
  }

  public abstract Table getTable();

  public abstract GTableContentProvider getTableContentProvider();

  public GTableLabelProvider getTableLabelProvider() {
    return tableLabelProvider;
  }

  public abstract GTableMenuListener getTableMenuListener();

  public AbstractEnum[] getValidExtensions() {
    return validExtensions;
  }

  public AbstractEnum[] getValidStates() {
    return validStates;
  }

  public StructuredViewer getViewer() {
    return this.sViewer;
  }

  public ViewFrame getViewFrame() {
    return viewFrame;
  }

  public boolean isActive() {
    return active;
  }

  public boolean isDisposed() {
    return ((getViewer() == null) || (getTable() == null) || getTable().isDisposed());
  }

  public boolean isVisible() {
    return visible;
  }

  public void loadDynamicColumn() {
    String tmp = PreferenceLoader.loadString(preferenceString + "DynamicColumn");
    if (!tmp.equals(SResources.S_ES))
      setDynamicColumn(tmp);

    int minWidth = PreferenceLoader.loadInt(preferenceString + "MinDynamicColumnWidth");
    if (minWidth > 0)
      setMinDynamicColumnWidth(minWidth);
  }

  public void loadExclusionStateFilters() {
    int tmp = PreferenceLoader.loadInt(preferenceString + "ExclusionStateFilters");
    if (tmp > 0) {
      ExclusionStateViewerFilter s = new ExclusionStateViewerFilter(this);
      s.setFiltered(tmp);
      addFilter(s);
    }
  }

  public void swapFilters(String string) {
    redrawTable(false);
    resetFilters();
    if (string != null)
      addFilters(string);
    redrawTable(true);
  }

  public String filtersToString() {
    String result = SResources.S_ES;

    ViewerFilter[] filters = getFilters();
    for (int i = 0; i < filters.length; i++)
      result += filters[i].toString();
    return result;
  }

  public void addFilters(String string) {
    StringTokenizer st = new StringTokenizer(string, ",");

    String s1, s2;
    int filterType;
    while (st.hasMoreTokens()) {
      s1 = st.nextToken();
      s2 = st.nextToken();
      try {
        filterType = Integer.parseInt(s1);
      } catch (NumberFormatException e) {
        filterType = 0;
      }
      loadFilter(filterType, s2);
    }
  }

  public static int filterToInt(ViewerFilter viewerFilter) {
    if (viewerFilter instanceof StateViewerFilter)
      return STATE_FILTER;
    else if (viewerFilter instanceof ExclusionStateViewerFilter)
      return EXCLUSION_STATE_FILTER;
    else if (viewerFilter instanceof NetworkViewerFilter)
      return NETWORK_FILTER;
    else if (viewerFilter instanceof RefineFilter)
      return REFINE_FILTER;
    else if (viewerFilter instanceof FileExtensionViewerFilter)
      return FILE_EXTENSION_FILTER;
    return 0;
  }

  public void loadFilter(int filterType, String filter) {

    int filterValue = 0;

    try {
      filterValue = Integer.parseInt(filter);
    } catch (NumberFormatException e) {
    }

    switch (filterType) {
      case STATE_FILTER :
        StateViewerFilter f = new StateViewerFilter(this);
        f.setFiltered(filterValue);
        addFilter(f);
        break;
      case EXCLUSION_STATE_FILTER :
        ExclusionStateViewerFilter e = new ExclusionStateViewerFilter(this);
        e.setFiltered(filterValue);
        addFilter(e);
        break;
      case NETWORK_FILTER :
        NetworkViewerFilter n = new NetworkViewerFilter(this);
        n.setFiltered(filterValue);
        addFilter(n);
        break;
      case FILE_EXTENSION_FILTER :
        FileExtensionViewerFilter fe = new FileExtensionViewerFilter(this);
        fe.setFiltered(filterValue);
        addFilter(fe);
        break;
      case REFINE_FILTER :
        refineString = filter;
        viewFrame.setRefineText(filter);
        updateRefineFilter();
        break;
    }

  }

  public void loadFilters() {
    if (saveStateFilters)
      loadStateFilters();
    if (saveExclusionStateFilters)
      loadExclusionStateFilters();
    if (saveNetworkFilters)
      loadNetworkFilters();
  }

  public void loadNetworkFilters() {
    int tmp = PreferenceLoader.loadInt(preferenceString + "NetworkFilters");
    if (tmp > 0) {
      NetworkViewerFilter n = new NetworkViewerFilter(this);
      n.setFiltered(tmp);
      addFilter(n);
    }
  }

  public void loadStateFilters() {
    int tmp = PreferenceLoader.loadInt(preferenceString + "StateFilters");
    if (tmp > 0) {
      StateViewerFilter s = new StateViewerFilter(this);
      s.setFiltered(tmp);
      addFilter(s);
    }
  }

  public void redrawTable(boolean b) {
    if (forceRedraw)
      getTable().setRedraw(b);
  }

  public void refresh() {
    sViewer.refresh();
  }
  
  public void refresh(boolean b) {
   if (b)
     redrawTable(false);
     refresh();
   if (b)
     redrawTable(true);
    
  }

  public void removeDynamicColumn() {
    dynamicColumn = SResources.S_ES;
    if (controlAdapter != null) {
      getTable().removeControlListener(controlAdapter);
      controlAdapter = null;
    }
  }

  public void removeFilter(ViewerFilter viewerFilter) {
    redrawTable(false);
    sViewer.removeFilter(viewerFilter);
    redrawTable(true);
  }

  public void resetColumns() {
    ICustomViewer cViewer = (ICustomViewer) getViewer();
    Object o = cViewer.getInput();
    cViewer.setInput(null);
    cViewer.setEditors(false);
    gSorter.setColumnIndex(0);
    cViewer.closeAllTTE();
    createColumns();
    cViewer.setInput(o);
    resetDynamicColumn();
    updateDisplay();
  }

  public void resetFilters() {
    redrawTable(false);
    this.sViewer.resetFilters();
    refineFilter = null;
    viewFrame.setRefineText(SResources.S_ES);
    redrawTable(true);
  }

  public void resizeColumns() {
    Table table = getTable();
    if ((table == null) || table.isDisposed())
      return;
    int width = table.getBounds().width;
    boolean scrollBar = table.getVerticalBar().getThumb() < table.getVerticalBar().getMaximum();
    if ((oldTableWidth == width) && (scrollBar == oldTableScrollBar))
      return;
    oldTableWidth = width;
    oldTableScrollBar = scrollBar;
    ICustomViewer customViewer = ((ICustomViewer) getViewer());
    int otherColumns = 0;
    TableColumn dynamicTableColumn = null;
    int columnNum = dynamicColumn.charAt(0) - IDSelector.MAGIC_NUMBER;
    TableColumn tableColumn;
    if (table.getColumns() == null)
      return;
    TableColumn[] tableColumns = table.getColumns();
    for (int i = 0; i < tableColumns.length; i++) {
      tableColumn = tableColumns[i];
      if (columnNum == customViewer.getColumnIDs()[i])
        dynamicTableColumn = tableColumn;
      else
        otherColumns += tableColumn.getWidth();
    }
    if (dynamicTableColumn != null) {
      if (scrollBar)
        otherColumns += table.getVerticalBar().getSize().x;
      if ((dynamicTableColumn != null) && !dynamicTableColumn.isDisposed()) {
        try {
          if (forceRedraw)
            getTable().setRedraw(false);
          dynamicTableColumn.setWidth(Math.max(minDynamicColumnWidth, width - otherColumns));
          if (forceRedraw)
            getTable().setRedraw(true);
        } catch (Exception e) {
          // Some SWT thing ?
        }
      }
    }
  }

  public void saveEmptyGViewerFilter(String prefName) {
    PreferenceLoader.getPreferenceStore().setValue(preferenceString + prefName, 0);
  }

  public void saveFilters() {
    if (saveStateFilters)
      saveFilters("StateFilters", StateViewerFilter.class);
    if (saveExclusionStateFilters)
      saveFilters("ExclusionStateFilters", ExclusionStateViewerFilter.class);
    if (saveNetworkFilters)
      saveFilters("NetworkFilters", NetworkViewerFilter.class);
  }

  public void saveFilters(String filterString, Class filterClass) {
    boolean found = false;
    for (int i = 0; i < getFilters().length; i++) {
      if (filterClass.isInstance(getFilters()[i])) {
        saveGViewerFilter((AbstractViewerFilter) getFilters()[i], filterString);
        found = true;
      }
    }
    if (!found)
      saveEmptyGViewerFilter(filterString);
  }

  public void saveGViewerFilter(AbstractViewerFilter gViewerFilter, String prefName) {
    PreferenceLoader.getPreferenceStore().setValue(preferenceString + prefName, gViewerFilter.getFiltered());
  }

  public void setActive(boolean b) {
    active = b;
    getTableContentProvider().setActive(b);
  }

  
  public void resetDynamicColumn() {
    String tmp = dynamicColumn;
    dynamicColumn = SResources.S_ES;
    setDynamicColumn(tmp);
  }
  
  public void setDynamicColumn(int columnIndex) {
    setDynamicColumn(String.valueOf(columnIDs.charAt(columnIndex)));
        // String.valueOf((char) (IDSelector.MAGIC_NUMBER + columnIndex)));
  }

  public void setDynamicColumn(String string) {
    if ("gtk".equals(SWT.getPlatform()) || string == null || string.length() > 1)
      return;

    if ((columnIDs.indexOf(string) == -1) || dynamicColumn.equals(string)) {
      removeDynamicColumn();
      return;
    } else
      dynamicColumn = string;
    if (controlAdapter == null) {
      controlAdapter = new ControlAdapter() {
        public void controlResized(ControlEvent e) {
          resizeColumns();
        }
      };
      getTable().addControlListener(controlAdapter);
      resizeColumns();
    }
  }

  public abstract void setInput();

  public void setMinDynamicColumnWidth(int width) {
    minDynamicColumnWidth = width;
  }

  public void setRefineString(String string) {
    refineString = string;
    updateRefineFilter();
  }

  public void setVisible(boolean b) {
    visible = b;
    getTableContentProvider().setVisible(b);
  }

  public void sortByColumn(int column) {
    gSorter.setColumnIndex(column);
    refresh();
  }

  public void unsetInput() {
    sViewer.setInput(null);
  }

  public void updateDisplay() {
    ((ICustomViewer) getViewer()).updateDisplay();
    getTable().setLinesVisible(PreferenceLoader.loadBoolean("displayGridLines"));
    getTable().setFont(PreferenceLoader.loadFont("tableFontData"));
    getTableLabelProvider().updateDisplay();
  }

  public void updateRefineFilter() {
    if (refineString.equals(SResources.S_ES)) {
      if (refineFilter != null) {
        removeFilter(refineFilter);
        refineFilter = null;
      }
    } else {
      if (refineFilter == null) {
        refineFilter = new RefineFilter(this);
        addFilter(refineFilter);
      } else
        refineFilter.update();
    }
  }

  public void widgetDisposed(DisposeEvent e) {
    PreferenceLoader.getPreferenceStore().setValue(preferenceString + "DynamicColumn", dynamicColumn);
    PreferenceLoader.getPreferenceStore().setValue(preferenceString + "MinDynamicColumnWidth",
        minDynamicColumnWidth);
    saveFilters();
  }
}
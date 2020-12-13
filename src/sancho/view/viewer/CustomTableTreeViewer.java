/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewer;

import java.util.Hashtable;
import java.util.Iterator;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableTreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableTreeItem;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Widget;

import sancho.model.mldonkey.Client;
import sancho.model.mldonkey.File;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.transfer.CTableTreeEditor;
import sancho.view.transfer.ChunkCanvas;
import sancho.view.transfer.FileClient;
import sancho.view.utility.IDSelector;
import sancho.view.utility.SResources;

public class CustomTableTreeViewer extends TableTreeViewer implements ICustomViewer {
  private Hashtable tableTreeEditors = new Hashtable();
  private boolean activeEditors = false;
  private int chunksColumn = -1;
  private int[] columnIDs;
  private static Color alternateColor;
  private static Color tableBGColor;
  public static Color sortedBGColor;
  private static boolean alternateColors;
  public static boolean hilightSorted;
  private boolean adjustFonts = SWT.getPlatform().equals("win32") || SWT.getPlatform().equals("gtk")
      || SWT.getPlatform().equals("motif");

  // make public for retarded gtk
  public void updateSelection(ISelection selection) {
    super.updateSelection(selection);
  }

  public void updateDisplay() {
    alternateColor = PreferenceLoader.loadColor("tableAlternateBGColor");
    alternateColors = PreferenceLoader.loadBoolean("tableAlternateBGColors");
    tableBGColor = PreferenceLoader.loadColor("tablesBackgroundColor");

    boolean newHigh =  PreferenceLoader.loadBoolean("tableHilightSorted");
    sortedBGColor = PreferenceLoader.loadColor("tableSortedColumnBGColor");
    
    
    if (!newHigh && hilightSorted)
      resetSelColumn();
    
    hilightSorted = newHigh;
    
    
    if (!alternateColors)
      resetColors();
    else
      recolor();
  }

  public CustomTableTreeViewer(Composite parent, int style) {
    super(parent, style);

    Table table = getTableTree().getTable();
    table.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        nudgeColumn();
      }

      public void keyReleased(KeyEvent e) {
        nudgeColumn();
      }
    });
  }

  // 3rd attempt to hack in TTEs
  public void setChunksColumn(int i) {
    chunksColumn = i;
  }

  public int getChunksColumn() {
    return chunksColumn;
  }

  public void setEditors(boolean b) {
    activeEditors = b;
  }

  public boolean getEditors() {
    return activeEditors;
  }

  public void nudgeColumn() {
    if (activeEditors && chunksColumn > -1) {
      TableColumn c = this.getTableTree().getTable().getColumn(chunksColumn);
      int width = c.getWidth();
      if (width > 0)
        c.setWidth(width);
    }
  }

  public void closeAllTTE() {
    Iterator i = tableTreeEditors.keySet().iterator();

    while (i.hasNext()) {
      Object object = i.next();
      disposeTTE((CTableTreeEditor) tableTreeEditors.get(object));
    }

    tableTreeEditors.clear();
  }

  public void openAllTTE() {
    TableTreeItem[] parents = this.getTableTree().getItems();

    for (int i = 0; i < parents.length; i++) {
      associateTTE(parents[i].getData(), parents[i], true);

      TableTreeItem[] children = parents[i].getItems();

      for (int j = 0; j < children.length; j++) {
        if (children[j].getData() != null)
          associateTTE(children[j].getData(), children[j], true);
      }
    }
  }

  private void disposeTTE(CTableTreeEditor tableTreeEditor) {
    if (tableTreeEditor != null) {
      if (tableTreeEditor.getEditor() != null) {
        tableTreeEditor.getEditor().dispose();
      }

      tableTreeEditor.dispose();
    }
  }

  public void remove(Object[] elements) {
    super.remove(elements);
    if (activeEditors)
      removeTTE(elements);
    if (alternateColors)
      recolor();
  }

  public void removeTTE(Object[] elements) {
    if (!activeEditors)
      return;

    for (int i = 0; i < elements.length; i++) {
      if (elements[i] != null) { // why is it null? 
        disposeTTE((CTableTreeEditor) tableTreeEditors.get(elements[i]));
        tableTreeEditors.remove(elements[i]);
      }
    }
    nudgeColumn();
  }

  public void add(Object parentElement, Object[] childElements) {
    super.add(parentElement, childElements);
    nudgeColumn();
  }

  private void associateTTE(Object element, Item item, boolean forceUpdate) {
    if ((item.getData() != element) || forceUpdate) {
      if (!tableTreeEditors.containsKey(element)) {
        File file = null;
        Client client = null;

        if (element instanceof File)
          file = (File) element;
        else if (element instanceof FileClient) {
          FileClient fileClient = (FileClient) element;
          file = fileClient.getFile();
          client = fileClient.getClient();
        }

        CTableTreeEditor tableTreeEditor = new CTableTreeEditor(this.getTableTree());
        tableTreeEditor.horizontalAlignment = SWT.LEFT;
        tableTreeEditor.grabHorizontal = true;

        ChunkCanvas chunkCanvas = new ChunkCanvas(this.getTableTree().getTable(), SWT.NO_BACKGROUND, client,
            file, null, true);
        tableTreeEditor.setEditor(chunkCanvas, (TableTreeItem) item, chunksColumn);

        /*   if (client != null)
         client.addObserver(chunkCanvas);
         else
         file.addObserver(chunkCanvas); */

        tableTreeEditors.put(element, tableTreeEditor);
      } else {
        CTableTreeEditor tableTreeEditor = (CTableTreeEditor) tableTreeEditors.get(element);
        if (!item.equals(tableTreeEditor.getItem()))
          tableTreeEditor.setItem((TableTreeItem) item);
      }
    }
  }

  protected void mapElement(Object element, Widget item) {
    super.mapElement(element, item);
    //  if (activeEditors)
    //   associateTTE(element, (Item) item, true);
  }

  protected void unmapElement(Object element) {
    super.unmapElement(element);
    // if (activeEditors)
    //  disassociateTTE(element);
  }

  private void disassociateTTE(Object element) {
    // Fully dispose if filters are active, or processing child items
    if ((getFilters().length > 0) || !(element instanceof File)) {
      disposeTTE((CTableTreeEditor) tableTreeEditors.get(element));
      tableTreeEditors.remove(element);
    } else {
      CTableTreeEditor tableTreeEditor = (CTableTreeEditor) tableTreeEditors.get(element);
      if (tableTreeEditor != null)
        tableTreeEditor.setItem(null);
    }
  }

  public void refresh() {
    super.refresh();
    if (activeEditors)
      nudgeColumn();
  }

  public void expandAll() {
    super.expandAll();
    nudgeColumn();
  }

  public void collapseAll() {
    super.collapseAll();
    nudgeColumn();
  }

  public void collapseToLevel(Object element, int level) {
    super.collapseToLevel(element, level);
    nudgeColumn();
  }

  public void expandToLevel(Object element, int level) {
    super.expandToLevel(element, level);
    nudgeColumn();
  }

  // End TTE hack
  public void setColumnIDs(String string) {
    columnIDs = new int[string.length()];

    for (int i = 0; i < string.length(); i++)
      columnIDs[i] = string.charAt(i) - IDSelector.MAGIC_NUMBER;
  }

  public int[] getColumnIDs() {
    return columnIDs;
  }

  public void resetColors() {
    TableTreeItem[] ti = getTableTree().getItems();
    for (int i = 0; i < ti.length; i++)
      ti[i].setBackground(null);
  }
  
  public void resetSelColumn() {
    int column = ((GSorter)getSorter()).getLastColumnIndex();
    TableTreeItem[] ti = getTableTree().getItems();
    for (int i = 0; i < ti.length; i++) {
      ti[i].setBackground(column, null);
      TableTreeItem[] tii = ti[i].getItems();
      for (int j = 0; j < tii.length; j++) {
        tii[j].setBackground(column, null);
      }
    }
    
  }

  public void recolor() {
    TableTreeItem[] ti = getTableTree().getItems();
    for (int i = 0; i < ti.length; i++) {
      if (i % 2 != 0) {
        if (!ti[i].getBackground().getRGB().equals(alternateColor.getRGB()))
          ti[i].setBackground(alternateColor);
      } else {
        if (!ti[i].getBackground().getRGB().equals(tableBGColor.getRGB()))
          ti[i].setBackground(tableBGColor);
      }
    }
  }

  // crap jface code

  public Object getElementAt(int index) {
    // XXX: Workaround for 1GBCSB1: SWT:WIN2000 - TableTree should have getItem(int index)
    //TableTreeItem i = tableTree.getItems()[index];
    //if (i != null)
    //    return i.getData();
    //return null;

    // crap workaround arrayoutofbounds 
    TableTreeItem[] items = getTableTree().getItems();

    if (index >= items.length)
      return null;

    return items[index].getData();
  }

  protected Item getItem(int x, int y) {
    // XXX: Workaround for 1GBCSHG: SWT:WIN2000 - TableTree should have getItem(Point point)
    //  Item i = getTableTree().getTable().getItem(
    //         getTableTree().toControl(new Point(x, y)));
    // Item ii = getTableTree().getItem(new Point(x, y));

    return getTableTree().getItem(new Point(x, y));
  }

  protected void checkTTE(Item item, Object element) {

    //item.

    if (!tableTreeEditors.containsKey(element)) {
      File file = null;
      Client client = null;

      if (element instanceof File)
        file = (File) element;
      else if (element instanceof FileClient) {
        FileClient fileClient = (FileClient) element;
        file = fileClient.getFile();
        client = fileClient.getClient();
      }

      CTableTreeEditor tableTreeEditor = new CTableTreeEditor(this.getTableTree());
      tableTreeEditor.horizontalAlignment = SWT.LEFT;
      tableTreeEditor.grabHorizontal = true;

      ChunkCanvas chunkCanvas = new ChunkCanvas(this.getTableTree().getTable(), SWT.NO_BACKGROUND, client,
          file, null, true);
      tableTreeEditor.setEditor(chunkCanvas, (TableTreeItem) item, chunksColumn);

      /*   if (client != null)
       client.addObserver(chunkCanvas);
       else
       file.addObserver(chunkCanvas); */

      tableTreeEditors.put(element, tableTreeEditor);
    } else {
      CTableTreeEditor tableTreeEditor = (CTableTreeEditor) tableTreeEditors.get(element);
      Item TEitem = tableTreeEditor.getItem();
      if (!TEitem.equals(item)) {
        tableTreeEditor.setItem((TableTreeItem) item);
      }
    }
  }

  // Copyright (c) 2000, 2003 IBM Corporation and others.
  // TableTreeViewer.java
  protected void doUpdateItem(Item item, Object element) {
    // update icon and label
    // Similar code in TableTreeViewer.doUpdateItem()

    if (activeEditors)
      checkTTE(item, element);

    IBaseLabelProvider prov = getLabelProvider();
    ITableLabelProvider tprov = null;
    ILabelProvider lprov = null;

    if (prov instanceof ITableLabelProvider)
      tprov = (ITableLabelProvider) prov;
    else
      lprov = (ILabelProvider) prov;

    int columnCount = this.getTableTree().getTable().getColumnCount();
    TableTreeItem ti = (TableTreeItem) item;

    // Also enter loop if no columns added. See 1G9WWGZ: JFUIF:WINNT -
    // TableViewer with 0 columns does not work
    for (int column = 0; (column < columnCount) || (column == 0); column++) {
      String text = SResources.S_ES; //$NON-NLS-1$
      Image image = null;

      if (tprov != null) {
        text = tprov.getColumnText(element, column);
        image = tprov.getColumnImage(element, column);
      } else {
        if (column == 0) {
          text = lprov.getText(element);
          image = lprov.getImage(element);
        }
      }

      // Only set text if it changes
      if (!text.equals(ti.getText(column)))
        ti.setText(column, text);

      // Apparently a problem to setImage to null if already null
      if (ti.getImage(column) != image)
        ti.setImage(column, image);

      if (hilightSorted) {
        RGB curr = ti.getBackground(column).getRGB();
        if (((GSorter) getSorter()).getLastColumnIndex() == column) {
          if (!curr.equals(alternateColor.getRGB())) {
            ti.setBackground(column, alternateColor);

          }
        } else {
          if (!curr.equals(getTableTree().getBackground().getRGB())) {
            ti.setBackground(column, null);
          }
        }
      }
    }
    if (adjustFonts && prov instanceof IFontProvider) {
      IFontProvider fprov = (IFontProvider) prov;
      Font f = fprov.getFont(element);
      if (f != null && f != ti.getData("fontOn")) {
        ti.setFont(f);
        ti.setData("fontOn", f);
      } else if (f == null && ti.getData("fontOn") != null) {
        ti.setData("fontOn", null);
        ti.setFont(null);

      };
    }

    if (prov instanceof IColorProvider) {
      IColorProvider cprov = (IColorProvider) prov;

      if (ti.getForeground() != cprov.getForeground(element)) {
        if ((cprov.getForeground(element) != null)
            || (!ti.getParent().getForeground().getRGB().equals(ti.getForeground().getRGB())))
          ti.setForeground(cprov.getForeground(element));
      }

      //      if (ti.getBackground() != cprov.getBackground(element)) {
      //        if ((cprov.getBackground(element) != null)
      //            ||
      // (!ti.getParent().getBackground().getRGB().equals(ti.getBackground().getRGB())))
      //          ti.setBackground(cprov.getBackground(element));
      //      }
    }
    if (alternateColors)
      recolor();

  }

}
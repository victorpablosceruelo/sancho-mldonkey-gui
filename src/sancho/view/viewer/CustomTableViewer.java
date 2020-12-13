/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewer;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;

import sancho.view.preferences.PreferenceLoader;
import sancho.view.utility.IDSelector;
import sancho.view.utility.SResources;

public class CustomTableViewer extends TableViewer implements ICustomViewer {
  private int[] columnIDs;
  private static Color alternateColor;
  private static Color tableBGColor;
  private static boolean alternateColors;
  private boolean adjustFonts = true;
  public static Color sortedBGColor;
  public static boolean hilightSorted;

  //make public for retarded gtk
  public void updateSelection(ISelection selection) {
    super.updateSelection(selection);
  }

  public void updateDisplay() {
    alternateColor = PreferenceLoader.loadColor("tableAlternateBGColor");
    alternateColors = PreferenceLoader.loadBoolean("tableAlternateBGColors");
    tableBGColor = PreferenceLoader.loadColor("tablesBackgroundColor");
    boolean newHigh = PreferenceLoader.loadBoolean("tableHilightSorted");
    sortedBGColor = PreferenceLoader.loadColor("tableSortedColumnBGColor");

    if (!newHigh && hilightSorted)
      resetSelColumn();

    hilightSorted = newHigh;
    if (!alternateColors)
      resetColors();
    else
      recolor();
  }

  public CustomTableViewer(Composite parent, int style) {
    super(parent, style);
  }

  // Start ICustomViewer
  public void closeAllTTE() {
  }

  public void setEditors(boolean b) {
  }

  // End ICustomViewer

  public void setColumnIDs(String string) {
    columnIDs = new int[string.length()];

    for (int i = 0; i < string.length(); i++) {
      columnIDs[i] = string.charAt(i) - IDSelector.MAGIC_NUMBER;
    }
  }

  public int[] getColumnIDs() {
    return columnIDs;
  }

  public void resetColors() {
    TableItem[] ti = getTable().getItems();
    for (int i = 0; i < ti.length; i++) {
      ti[i].setBackground(null);
    }
  }

  public void resetSelColumn() {
    int column = ((GSorter) getSorter()).getLastColumnIndex();
    TableItem[] ti = getTable().getItems();
    for (int i = 0; i < ti.length; i++) {
      ti[i].setBackground(column, null);
    }
  }

  public void recolor() {
    TableItem[] ti = getTable().getItems();
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

  public void remove(final Object[] elements) {
    super.remove(elements);
    if (alternateColors)
      recolor();
  }

  // Copyright (c) 2000, 2003 IBM Corporation and others.
  // TableViewer.java
  protected void doUpdateItem(Widget widget, Object element, boolean fullMap) {
    if (widget instanceof TableItem) {
      TableItem item = (TableItem) widget;

      // remember element we are showing
      if (fullMap) {
        associate(element, item);
      } else {
        item.setData(element);
        mapElement(element, item);
      }

      IBaseLabelProvider prov = getLabelProvider();
      ITableLabelProvider tprov = null;
      ILabelProvider lprov = null;

      if (prov instanceof ITableLabelProvider) {
        tprov = (ITableLabelProvider) prov;
      } else {
        lprov = (ILabelProvider) prov;
      }

      int columnCount = this.getTable().getColumnCount();
      TableItem ti = item;

      // Also enter loop if no columns added. See 1G9WWGZ: JFUIF:WINNT -
      // TableViewer with 0 columns does not work
      for (int column = 0; (column < columnCount) || (column == 0); column++) {
        // Similar code in TableTreeViewer.doUpdateItem()
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
        if (!text.equals(ti.getText(column))) {
          ti.setText(column, text);
        }

        // Apparently a problem to setImage to null if already null
        if (ti.getImage(column) != image) {
          ti.setImage(column, image);
        }

        if (hilightSorted) {
          RGB curr = ti.getBackground(column).getRGB();
          if (((GSorter) getSorter()).getLastColumnIndex() == column) {
            if (!curr.equals(alternateColor.getRGB())) {
              ti.setBackground(column, alternateColor);

            }
          } else {
            if (!curr.equals(getTable().getBackground().getRGB())) {
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
              || (!ti.getParent().getForeground().getRGB().equals(ti.getForeground().getRGB()))) {
            ti.setForeground(cprov.getForeground(element));
          }
        }
      }

      if (alternateColors)
        recolor();
    }

  }
}
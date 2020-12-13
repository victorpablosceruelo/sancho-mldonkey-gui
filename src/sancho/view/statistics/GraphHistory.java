/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.statistics;

import gnu.trove.TIntArrayList;

import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Shell;

import sancho.view.preferences.PreferenceLoader;
import sancho.view.utility.SResources;

public class GraphHistory implements PaintListener {
  private Shell shell;
  private Graph graph;
  private TIntArrayList maxList;
  private TIntArrayList avgList;
  private Color gridColor = PreferenceLoader.loadColor("graphGridColor");
  private Color backgroundColor = PreferenceLoader.loadColor("graphBackgroundColor");
  private Color textColor = PreferenceLoader.loadColor("graphTextColor");

  private static String RS_HOUR = SResources.getString("graph.historyHour");
  private static String RS_AVG = SResources.getString("graph.historyAvg");
  private static String RS_MAX = SResources.getString("graph.historyMax");
  private static String RS_KBS = SResources.getString("graph.historyKBS");

  public GraphHistory(Graph graph) {
    this.graph = graph;
    this.maxList = graph.getMaxList();
    this.avgList = graph.getAvgList();

    createContents();
  }

  public void createContents() {
    shell = new Shell(SWT.NO_BACKGROUND | SWT.MAX | SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.BORDER
        | SWT.APPLICATION_MODAL);
    shell.setImage(SResources.getImage("ProgramIcon"));
    shell.setText(graph.getName());
    shell.setLayout(new FillLayout());
    shell.addDisposeListener(new DisposeListener() {
      public synchronized void widgetDisposed(DisposeEvent e) {
        PreferenceStore p = PreferenceLoader.getPreferenceStore();
        PreferenceConverter.setValue(p, "graphHistoryWindowBounds", shell.getBounds());
      }
    });

    shell.addPaintListener(this);
    shell.setBounds(PreferenceLoader.loadRectangle("graphHistoryWindowBounds"));
    shell.open();
  }

  public void paintControl(PaintEvent e) {
    if ((shell.getClientArea().width < 5) || (shell.getClientArea().height < 5))
      return;

    int height = shell.getClientArea().height;
    int width = shell.getClientArea().width;

    Image imageBuffer = new Image(shell.getDisplay(), shell.getClientArea());
    GC gc = new GC(imageBuffer);

    gc.setBackground(backgroundColor);
    gc.fillRectangle(0, 0, shell.getClientArea().width, shell.getClientArea().height);

    // draw grid
    gc.setForeground(gridColor);

    // vertical lines
    for (int i = 0; i < width; i += 20)
      gc.drawLine(i, 0, i, height + 1);

    // horizontal lines
    for (int i = height + 1; i > 0; i -= 20)
      gc.drawLine(0, i, width + 1, i);

    // looks worse when under the grid
    drawGraph(gc);

    e.gc.drawImage(imageBuffer, 0, 0);
    gc.dispose();
    imageBuffer.dispose();
  }

  private void drawGraph(GC gc) {
    if (maxList.size() == 0) {
      gc.setForeground(textColor);
      gc.drawText(SResources.getString("graph.noHistory"), 0, 0);

      return;
    }

    int lineHeight = gc.getFontMetrics().getHeight() + 2;

    int width = shell.getClientArea().width;
    int barWidth = width / maxList.size();
    float height = (float) shell.getClientArea().height - (lineHeight * 3);

    float maxValueY;
    float avgValueY;
    int maxValue;
    int avgValue;

    float maximum = 2;

    // maxList.max() crashes..
    for (int i = 0; i < maxList.size(); i++) {
      if ((maxList.getQuick(i) / 10) > maximum)
        maximum = (float) maxList.getQuick(i) / 10;
    }

    float zoom = (height - 10f) / maximum;
    int xCoord = 0;

    gc.setForeground(textColor);

    for (int i = 0; i < maxList.size(); i++) {
      maxValue = maxList.getQuick(i);
      avgValue = avgList.getQuick(i);

      xCoord = i * barWidth;

      maxValueY = maxValue / 10;
      avgValueY = avgValue / 10;

      maxValueY = maxValueY * zoom;
      avgValueY = avgValueY * zoom;

      gc.setBackground(graph.getColor2());
      gc.fillRectangle(xCoord, shell.getClientArea().height + 1, barWidth - 2, -(int) maxValueY);

      gc.setBackground(graph.getColor1());
      gc.fillRectangle(xCoord, shell.getClientArea().height + 1, barWidth - 2, -(int) avgValueY);

      gc.drawText(RS_HOUR + (i + 1), xCoord, 0, true);
      gc.drawText(RS_AVG + ((double) avgValue / 100) + RS_KBS, xCoord, lineHeight, true);
      gc.drawText(RS_MAX + ((double) maxValue / 100) + RS_KBS, xCoord, (2 * lineHeight), true);
    }
  }
}
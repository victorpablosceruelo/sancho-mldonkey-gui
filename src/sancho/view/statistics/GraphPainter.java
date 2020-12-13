/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.statistics;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import sancho.utility.SwissArmy;
import sancho.view.preferences.PreferenceLoader;

// Nice and ugly
public class GraphPainter implements DisposeListener {

  private static final int START_X = 1;
  private static final String S_KBS = " KB/s";

  private Color backgroundColor;
  private Graph graph;
  private Color gridColor;
  private Color labelBackgroundColor;
  private Color labelLineColor;
  private Color labelTextColor;
  final private Composite parent;
  private Color textColor;
  private int updateDelay;

  public GraphPainter(Composite parent) {
    this.parent = parent;
    updateDisplay();
  }

  private void drawBarGraph(GC gcb, int width, float height, float zoom) {
    int positionInArray = graph.getInsertAt() - 1;
    int validPoints = ((Graph.MAX_POINTS > graph.getAmount()) ? graph.getAmount() : Graph.MAX_POINTS);
    float valueY;
    gcb.setForeground(graph.getColor1());

    int first = START_X;
    int incr = 1;

    if (graph.getReverse()) {
      first = width - 2;
      incr = -1;
    }

    for (int k = first; 0 <= k && (k < width) && (validPoints > 0); k += incr, validPoints--) {
      if (positionInArray < 0)
        positionInArray = Graph.MAX_POINTS - 1;

      valueY = graph.getPointAt(positionInArray) / 10;
      valueY = height - (valueY * zoom);
      gcb.drawLine(k, (int) height + 1, k, (int) (valueY));
      positionInArray--;
    }
  }

  private void drawGradiantGraph(GC gcb, int width, float height, float zoom) {
    int positionInArray = graph.getInsertAt() - 1;
    int validPoints = ((Graph.MAX_POINTS > graph.getAmount()) ? graph.getAmount() : Graph.MAX_POINTS);
    float valueY;

    gcb.setBackground(graph.getColor1());
    gcb.setForeground(graph.getColor2());

    int first = START_X;
    int incr = 1;

    if (graph.getReverse()) {
      first = width - 2;
      incr = -1;
    }

    for (int k = first; 0 <= k && k < width && validPoints > 0; k += incr, validPoints--) {
      if (positionInArray < 0)
        positionInArray = Graph.MAX_POINTS - 1;

      valueY = graph.getPointAt(positionInArray) / 10;
      valueY = height - (valueY * zoom);
      gcb.fillGradientRectangle(k, (int) height + 1, 1, (int) (valueY - height), true);
      positionInArray--;
    }
  }

  private void drawLineGraph(GC gcb, int width, float height, float zoom) {
    int positionInArray = graph.getInsertAt() - 1;
    int validPoints = ((Graph.MAX_POINTS > graph.getAmount()) ? graph.getAmount() : Graph.MAX_POINTS);
    float valueY;
    float lastY = -1;
    gcb.setForeground(graph.getColor1());
    gcb.setLineWidth(3);

    int first = START_X;
    int incr = 1;
    int pnt = -1;

    boolean reverse = graph.getReverse();
    if (reverse) {
      first = width - 2;
      incr = -1;
      pnt = 1;
    }

    for (int k = first; 0 <= k && (k < width) && (validPoints > 0); k += incr, validPoints--) {
      if (positionInArray < 0)
        positionInArray = Graph.MAX_POINTS - 1;

      valueY = graph.getPointAt(positionInArray) / 10;
      valueY = height - (valueY * zoom);

      if (lastY == -1)
        lastY = valueY;

      gcb.drawLine(k + pnt, (int) lastY, k, (int) (valueY));
      lastY = valueY;
      positionInArray--;
    }

    gcb.setLineWidth(1);
  }

  public void paint(GC gc, Image image) {
    GC gcb = new GC(image);
    gcb.setBackground(backgroundColor);
    gcb.setForeground(backgroundColor);

    int iWidth = image.getBounds().width;
    int iHeight = image.getBounds().height;

    gcb.fillRectangle(0, 0, iWidth, iHeight);

    int bottomSpace = gcb.getFontMetrics().getHeight() + 2;
    int height = iHeight - bottomSpace;

    int graphWidth = iWidth - START_X;
    float zoom = 0;
    float maximum = graph.findMax(iWidth) / 10;
    zoom = (height - 10f) / maximum;

    // draw graph gradiant lines (switch outside the for loop)
    switch (graph.getGraphType()) {
      case 1 :
        drawBarGraph(gcb, iWidth, height, zoom);
        break;
      case 2 :
        drawLineGraph(gcb, iWidth, height, zoom);
        break;
      default :
        drawGradiantGraph(gcb, iWidth, height, zoom);
        break;
    }

    // draw grid
    gcb.setForeground(gridColor);

    // vertical lines
    int cntr = 0;

    int first = START_X - 1;
    int last = (START_X + graphWidth);
    int incr = 20;

    if (graph.getReverse()) {
      first = iWidth - 1;
      incr = -incr;
      last = 0;
    }

    boolean reverse = graph.getReverse();
    for (int i = first; reverse ? i > last : i < last; i += incr) {
      gcb.drawLine(i, 0, i, (int) height + 1);
      if (cntr > 0 && cntr % 3 == 0)
        gcb.drawText(SwissArmy.calcTimeOfSeconds((updateDelay > 0 ? updateDelay : 1) * (cntr * 20)), i - 5,
            parent.getClientArea().height - gcb.getFontMetrics().getHeight(), true);
      cntr++;
    }

    // horizontal lines
    for (int i = (int) height + 1; i > 0; i -= 20)
      gcb.drawLine(START_X, i, START_X + graphWidth, i);

    // draw floating box
    double value = (double) graph.getNewestPoint() / 100;
    String boxString = String.valueOf(value) + S_KBS;

    int linePosition = (int) (height - ((graph.getNewestPoint() / 10) * zoom));
    int linePositionEnd = linePosition;
    int textPosition = linePosition - 6;

    if ((textPosition + bottomSpace) >= (int) height) {
      textPosition = (int) height - bottomSpace - 3;
      linePositionEnd = linePositionEnd - 6;
    }

    int boxWidth = gcb.textExtent(boxString).x + 20;
    int boxHeight = gcb.textExtent(boxString).y + 5;

    int box_X = START_X + 10;
    int text_X = box_X + 10;
    int line_X1 = box_X;
    int line_X2 = START_X;

    if (reverse) {
      box_X = iWidth - boxWidth - 10;
      text_X = box_X + 10;
      line_X2 = iWidth - START_X;
      line_X1 = box_X + boxWidth;
    }
    gcb.setForeground(labelTextColor);
    gcb.setBackground(labelBackgroundColor);
    gcb.fillRoundRectangle(box_X, textPosition, boxWidth, boxHeight, 18, 18);
    gcb.drawRoundRectangle(box_X, textPosition, boxWidth, boxHeight, 18, 18);
    gcb.drawText(boxString, text_X, textPosition + 2);
    gcb.setForeground(labelLineColor);
    gcb.drawLine(line_X1, linePositionEnd, line_X2, linePosition);

    gc.drawImage(image, 0, 0);
    gcb.dispose();
  }

  public void setGraph(final Graph graph) {
    this.graph = graph;
    parent.addDisposeListener(this);
  }

  public void updateDisplay() {
    backgroundColor = PreferenceLoader.loadColor("graphBackgroundColor");
    gridColor = PreferenceLoader.loadColor("graphGridColor");
    textColor = PreferenceLoader.loadColor("graphTextColor");

    labelBackgroundColor = PreferenceLoader.loadColor("graphLabelBackgroundColor");
    labelTextColor = PreferenceLoader.loadColor("graphLabelTextColor");
    labelLineColor = PreferenceLoader.loadColor("graphLabelLineColor");

    updateDelay = PreferenceLoader.loadInt("graphUpdateDelay");
  }

  public void widgetDisposed(DisposeEvent e) {
    if (graph != null) {
      PreferenceLoader.getPreferenceStore()
          .setValue("graph" + graph.getName() + "Type", graph.getGraphType());
      PreferenceLoader.getPreferenceStore().setValue("graph" + graph.getName() + "Reverse",
          graph.getReverse());
    }
  }
}